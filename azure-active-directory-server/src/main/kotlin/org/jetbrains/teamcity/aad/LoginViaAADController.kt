/*
 * Copyright 2000-2020 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.teamcity.aad

import jetbrains.buildServer.RootUrlHolder
import jetbrains.buildServer.controllers.AuthorizationInterceptor
import jetbrains.buildServer.controllers.BaseController
import jetbrains.buildServer.web.openapi.WebControllerManager
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.view.RedirectView
import org.springframework.web.util.UriComponentsBuilder
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author Evgeniy.Koshkin
 */
class LoginViaAADController(webManager: WebControllerManager,
                            authInterceptor: AuthorizationInterceptor,
                            private val aadSchemeProperties: AADSchemeProperties,
                            private val rootUrlHolder: RootUrlHolder) : BaseController() {
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
        val requestUrl = StringBuilder("$endpoint$separator")
                .append("response_type=id_token")
                .append("&client_id=$clientId")
                .append("&scope=openid")
                .append("&nonce=$nonce")
                .append("&response_mode=form_post")
                .apply {
                    aadSchemeProperties.authPrompt?.let {
                        if (it.isNotEmpty()) {
                            this.append("&prompt=${it.trim()}")
                        }
                    }
                    UriComponentsBuilder.fromUriString(rootUrlHolder.rootUrl)
                            .pathSegment("overview.html")
                            .toUriString().let {
                        this.append("&redirect_uri=$it")
                    }
                }

        return ModelAndView(RedirectView(requestUrl.toString()))
    }
}
