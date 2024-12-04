

package org.jetbrains.teamcity.aad

import jetbrains.buildServer.web.CsrfCheck
import jetbrains.buildServer.web.CsrfCheck.CheckResult.safe
import jetbrains.buildServer.web.CsrfCheck.CheckResult.unsafe
import org.jetbrains.teamcity.aad.AADConstants.ID_TOKEN
import java.net.URL
import javax.servlet.http.HttpServletRequest

class AADCSRFCheck(
        private val accessTokenValidator: AADAccessTokenValidator,
        private val callbackPathProvider: AADAuthCallbackPathProvider
) : CsrfCheck {
    override fun describe(verbose: Boolean) = "Microsoft Entra ID Authentication plugin CSRF check"

    override fun isSafe(request: HttpServletRequest): CsrfCheck.CheckResult {
        if (!ACTION_METHODS.contains(request.method)) {
            return CsrfCheck.UNKNOWN
        }

        if (URL(request.requestURL.toString()).path?.endsWith(callbackPathProvider.path, ignoreCase = true) != true) {
            return CsrfCheck.UNKNOWN
        }

        var nonce = request.getParameter(NONCE_PARAMETER)
        if (nonce == null) {
            val idToken = request.getParameter(ID_TOKEN)
            if (idToken == null) {
                return CsrfCheck.UNKNOWN
            }

            val token =  JWT.parse(idToken)
            if (token == null) {
                return CsrfCheck.UNKNOWN
            }
            nonce = token.getClaim(AADConstants.NONCE_CLAIM)
        }
        if (nonce != null) {
            return if (accessTokenValidator.validate(nonce)) safe() else unsafe("NONCE parameter is incorrect")
        }

        return CsrfCheck.UNKNOWN
    }

    companion object {
        val ACTION_METHODS = setOf("POST")
        val NONCE_PARAMETER = "nonce"
    }
}