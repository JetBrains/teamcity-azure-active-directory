

package org.jetbrains.teamcity.aad

import jetbrains.buildServer.RootUrlHolder
import jetbrains.buildServer.controllers.BaseController
import jetbrains.buildServer.web.openapi.WebControllerManager
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.view.RedirectView
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AADAuthCallbackController(
        private val webManager: WebControllerManager,
        private val rootUrlHolder: RootUrlHolder
) : BaseController() {
    init {
        webManager.registerController(AADConstants.DEDICATED_AUTH_PATH, this)
    }

    override fun doHandle(request: HttpServletRequest, response: HttpServletResponse): ModelAndView? {
        val redirectUrl = request.getSession().getAttribute("URL_KEY") as String?
        request.getSession().removeAttribute("URL_KEY")
        return ModelAndView(RedirectView(redirectUrl ?: rootUrlHolder.rootUrl))
    }
}