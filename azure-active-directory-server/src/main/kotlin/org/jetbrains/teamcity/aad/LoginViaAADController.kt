package org.jetbrains.teamcity.aad

import jetbrains.buildServer.controllers.AuthorizationInterceptor
import jetbrains.buildServer.controllers.BaseController
import jetbrains.buildServer.web.openapi.WebControllerManager
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.view.RedirectView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author Evgeniy.Koshkin
 */
class LoginViaAADController(webManager: WebControllerManager,
                            authInterceptor: AuthorizationInterceptor,
                            private val aadSchemeProperties: AADSchemeProperties) : BaseController() {
    init {
        webManager.registerController(AADConstants.LOGIN_PATH, this)
        authInterceptor.addPathNotRequiringAuth(AADConstants.LOGIN_PATH)
    }

    override fun doHandle(request: HttpServletRequest, response: HttpServletResponse): ModelAndView? {
        val nonce = request.getSessionId()
        val endpoint = aadSchemeProperties.appOAuthEndpoint
        val clientId = aadSchemeProperties.clientId
        if (endpoint == null || clientId == null) return null

        val separator = if (endpoint.contains('?')) '&' else '?'
        val requestUrl = "$endpoint${separator}response_type=id_token&client_id=$clientId&scope=openid&nonce=$nonce&response_mode=form_post"

        response.setHeader("Access-Control-Allow-Origin", "*")

        return ModelAndView(RedirectView(requestUrl))
    }
}
