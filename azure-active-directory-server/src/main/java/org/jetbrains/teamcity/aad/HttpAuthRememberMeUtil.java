package org.jetbrains.teamcity.aad;

import jetbrains.buildServer.serverSide.TeamCityProperties;

//NOTE: copy-pasted from jetbrains.buildServer.controllers.interceptors.auth.impl package

class HttpAuthRememberMeUtil {
  static boolean mustRememberMe() {
    return TeamCityProperties.getBoolean("teamcity.http.auth.remember.me");
  }
}
