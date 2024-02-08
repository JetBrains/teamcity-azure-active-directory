

package org.jetbrains.teamcity.aad

import javax.servlet.http.HttpServletRequest

/**
 * @author Evgeniy.Koshkin
 */
    fun HttpServletRequest.getSessionId(): String {
        // we must use requested session id, if it is presented, and only if not, then we can use current session id, see TW-23821
        return this.requestedSessionId ?: this.session.id
    }