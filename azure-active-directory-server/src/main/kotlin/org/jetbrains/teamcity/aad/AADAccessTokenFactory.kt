

package org.jetbrains.teamcity.aad

interface AADAccessTokenFactory {
    fun create() : String
}