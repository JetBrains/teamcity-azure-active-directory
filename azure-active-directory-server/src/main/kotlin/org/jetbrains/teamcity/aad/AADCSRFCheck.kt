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

import jetbrains.buildServer.web.CSRFFilter
import jetbrains.buildServer.web.CsrfCheck
import jetbrains.buildServer.web.CsrfCheck.CheckResult.safe
import jetbrains.buildServer.web.CsrfCheck.CheckResult.unsafe
import java.net.URL
import javax.servlet.http.HttpServletRequest

class AADCSRFCheck : CsrfCheck {
    override fun describe(verbose: Boolean) = "Azure Active Directory Authentication plugin CSRF check"

    override fun isSafe(request: HttpServletRequest): CsrfCheck.CheckResult {
        if (!ACTION_METHODS.contains(request.method)) {
            return CsrfCheck.UNKNOWN
        }

        if (URL(request.requestURL.toString()).path?.endsWith(URL_SUFFIX, ignoreCase = true) != true) {
            return CsrfCheck.UNKNOWN
        }

        val session = request.getSession(false)
        if (session != null) {
            val parameter = request.getParameter(STATE_PARAMETER)
            if (parameter != null) {
                return if (parameter == session.getAttribute(CSRFFilter.ATTRIBUTE)) safe() else unsafe("CSRF parameter " + STATE_PARAMETER + " does not match CSRF session value")
            }
        }

        return CsrfCheck.UNKNOWN
    }

    companion object {
        val ACTION_METHODS = setOf("POST")
        val STATE_PARAMETER = "state"
        val URL_SUFFIX = "/overview.html"
    }
}