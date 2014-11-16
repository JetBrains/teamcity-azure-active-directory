package org.jetbrains.teamcity.aad;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import java.util.Map;

/**
 * @author Evgeniy.Koshkin
 */
public class AADLoginModule implements javax.security.auth.spi.LoginModule {
  public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {

  }

  public boolean login() throws LoginException {
    return false;
  }

  public boolean commit() throws LoginException {
    return false;
  }

  public boolean abort() throws LoginException {
    return false;
  }

  public boolean logout() throws LoginException {
    return false;
  }
}
