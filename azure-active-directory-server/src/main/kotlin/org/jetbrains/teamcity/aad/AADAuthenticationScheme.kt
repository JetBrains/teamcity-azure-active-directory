

package org.jetbrains.teamcity.aad

import jetbrains.buildServer.controllers.interceptors.auth.HttpAuthenticationProtocol
import jetbrains.buildServer.controllers.interceptors.auth.HttpAuthenticationResult
import jetbrains.buildServer.controllers.interceptors.auth.HttpAuthenticationSchemeAdapter
import jetbrains.buildServer.controllers.interceptors.auth.util.HttpAuthUtil
import jetbrains.buildServer.serverSide.TeamCityProperties
import jetbrains.buildServer.serverSide.auth.LoginConfiguration
import jetbrains.buildServer.web.openapi.PluginDescriptor
import org.apache.log4j.Logger
import org.jetbrains.teamcity.aad.AADConstants.ID_TOKEN
import org.jetbrains.teamcity.aad.AADConstants.NONCE_CLAIM
import org.springframework.http.HttpStatus
import java.net.MalformedURLException
import java.net.URL
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author Evgeniy.Koshkin
 */
class AADAuthenticationScheme(loginConfiguration: LoginConfiguration,
                              private val pluginDescriptor: PluginDescriptor,
                              private val principalFactory: ServerPrincipalFactory,
                              private val accessTokenValidator: AADAccessTokenValidator)
    : HttpAuthenticationSchemeAdapter() {

    init {
        loginConfiguration.registerAuthModuleType(this)
    }

    override fun doGetName() = AADConstants.AAD_AUTH_SCHEME_NAME

    override fun getDisplayName() = "Microsoft Entra ID"

    override fun getDescription() = "Single sign-on via $displayName"

    override fun getEditPropertiesJspFilePath() = pluginDescriptor.getPluginResourcesPath("editAADSchemeProperties.jsp")

    override fun validate(properties: Map<String, String>): Collection<String>? {
        val errors = arrayListOf<String>()
        properties[AADConstants.AUTH_ENDPOINT_SCHEME_PROPERTY_KEY].apply {
            if (this.isNullOrBlank()) {
                errors.add("OAuth 2.0 authorization endpoint should be specified.")
            } else {
                try {
                    URL(this)
                } catch (e: MalformedURLException) {
                    errors.add("Invalid URL was specified for OAuth 2.0 authorization endpoint.")
                }
            }
        }

        if (properties[AADConstants.CLIENT_ID_SCHEME_PROPERTY_KEY].isNullOrBlank()) {
            errors.add("Client ID should be specified.")
        }
        return if (errors.isEmpty()) super.validate(properties) else errors
    }

    override fun processAuthenticationRequest(request: HttpServletRequest, response: HttpServletResponse, schemeProperties: Map<String, String>): HttpAuthenticationResult {
        if (request.method != "POST") {
            return HttpAuthenticationResult.notApplicable()
        }

        var uniqueNameClaimName = UNIQUE_NAME_CLAIM
        if (schemeProperties[AADConstants.AUTH_ENDPOINT_SCHEME_PROPERTY_KEY]?.contains("v2.0") == true)
            uniqueNameClaimName = UPN_CLAIM

        val idTokenString = request.getParameter(ID_TOKEN)
        if (idTokenString == null) {
            LOG.debug("POST request contains no $ID_TOKEN parameter so scheme is not applicable.")
            return HttpAuthenticationResult.notApplicable()
        }

        val token = JWT.parse(idTokenString) ?:
                return sendBadRequest(response, "Marked request as unauthenticated since failed to parse JWT from retrieved $ID_TOKEN $idTokenString")

        val error = token.getClaim(ERROR_CLAIM)
        if (error != null) {
            LOG.warn(error)
            val errorDescription = token.getClaim(ERROR_DESCRIPTION_CLAIM)
            return sendUnauthorized(request, response, errorDescription)
        }

        val nonce = token.getClaim(NONCE_CLAIM)
        val uniqueName = token.getClaim(uniqueNameClaimName)
        val oid = token.getClaim(OID_CLAIM)

        if (nonce == null || uniqueName == null || oid == null) {
            return sendBadRequest(response, "Some of required claims were not found in parsed JWT. $NONCE_CLAIM - $nonce; $uniqueNameClaimName - $uniqueName, $OID_CLAIM - $oid")
        }

        if (!accessTokenValidator.validate(nonce)) {
            return sendBadRequest(response, "Marked request as unauthenticated since retrieved JWT '$NONCE_CLAIM' claim is incorrect.")
        }

        // Get e-mail
        val email = token.getClaim(EMAIL_CLAIM) ?: token.getClaim(UPN_CLAIM)

        // Get full user name
        val lastName = token.getClaim(FAMILY_NAME_CLAIM)
        val firstName = token.getClaim(GIVEN_NAME_CLAIM)
        val fullName = token.getClaim(FULL_NAME_CLAIM)

        val userName = if (!fullName.isNullOrEmpty()) {
            fullName
        } else {
            StringBuilder("").apply {
                if (!firstName.isNullOrEmpty()) {
                    this.append(firstName).append(" ")
                }
                if (!lastName.isNullOrEmpty()) {
                    this.append(lastName)
                }
            }.toString().trim()
        }

        val principal = principalFactory.getServerPrincipal(uniqueName, oid, userName, email, schemeProperties)
        LOG.debug("Request authenticated. Determined user ${principal.name}")

        val shouldRemember = TeamCityProperties.getBoolean("teamcity.http.auth.remember.me")
        return HttpAuthenticationResult.authenticated(principal, shouldRemember)
    }

    private fun sendUnauthorized(request: HttpServletRequest, response: HttpServletResponse, reason: String?): HttpAuthenticationResult {
        return HttpAuthUtil.sendUnauthorized(request, response, reason, emptySet<HttpAuthenticationProtocol>())
    }

    private fun sendBadRequest(response: HttpServletResponse, reason: String): HttpAuthenticationResult {
        LOG.warn(reason)
        response.sendError(HttpStatus.BAD_REQUEST.value(), reason)
        return HttpAuthenticationResult.unauthenticated()
    }

    companion object {
        private val LOG = Logger.getLogger(AADAuthenticationScheme::class.java.name)

        private val OID_CLAIM = "oid" //object ID
        private val UPN_CLAIM = "upn"
        private val EMAIL_CLAIM = "email"
        private val UNIQUE_NAME_CLAIM = "unique_name"
        private val FULL_NAME_CLAIM = "name"
        private val FAMILY_NAME_CLAIM = "family_name"
        private val GIVEN_NAME_CLAIM = "given_name"
        private val ERROR_CLAIM = "error"
        private val ERROR_DESCRIPTION_CLAIM = "error_description"
    }
}