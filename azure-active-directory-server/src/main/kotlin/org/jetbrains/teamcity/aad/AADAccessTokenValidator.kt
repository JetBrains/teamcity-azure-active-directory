

package org.jetbrains.teamcity.aad

interface AADAccessTokenValidator {
    fun validate(token: String) : Boolean
}