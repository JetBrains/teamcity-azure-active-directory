package org.jetbrains.teamcity.aad

import jetbrains.buildServer.web.openapi.PagePlaces
import jetbrains.buildServer.web.openapi.PlaceId
import jetbrains.buildServer.web.openapi.PluginDescriptor
import jetbrains.buildServer.web.openapi.SimplePageExtension

import javax.servlet.http.HttpServletRequest

/**
 * @author Evgeniy.Koshkin
 */
class LoginViaAADLoginPageExtension(pagePlaces: PagePlaces,
                                    pluginDescriptor: PluginDescriptor,
                                    private val mySchemeProperties: AADSchemeProperties)
    : SimplePageExtension(
        pagePlaces,
        PlaceId.LOGIN_PAGE,
        LoginViaAADLoginPageExtension::class.java.name,
        pluginDescriptor.getPluginResourcesPath("loginViaAAD.jsp")) {

    init {
        register()
    }

    override fun isAvailable(request: HttpServletRequest) = mySchemeProperties.isSchemeConfigured
}
