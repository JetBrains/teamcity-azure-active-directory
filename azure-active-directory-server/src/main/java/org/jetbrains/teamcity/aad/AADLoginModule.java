package org.jetbrains.teamcity.aad;

import jetbrains.buildServer.serverSide.auth.ServerPrincipal;
import jetbrains.buildServer.serverSide.auth.TeamCityFailedLoginException;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.LoginException;
import java.util.Map;

/**
 * @author Evgeniy.Koshkin
 */
public class AADLoginModule implements javax.security.auth.spi.LoginModule {

  private Subject mySubject;
  private CallbackHandler myCallbackHandler;
  private Callback[] myCallbacks;
  private NameCallback myNameCallback;
  private PasswordCallback myPasswordCallback;

  public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
    myCallbackHandler = callbackHandler;
    myNameCallback = new NameCallback("login:");
    myPasswordCallback = new PasswordCallback("password:", false);
    myCallbacks = new Callback[]{myNameCallback, myPasswordCallback};
    mySubject = subject;
  }

  public boolean login() throws LoginException {
    try {
      myCallbackHandler.handle(myCallbacks);
    }
    catch (Throwable t) {
      throw new jetbrains.buildServer.serverSide.auth.TeamCityLoginException(t);
    }

    final String login = myNameCallback.getName();
    final String password = new String(myPasswordCallback.getPassword());

    if (checkPassword(login, password)) {
      mySubject.getPrincipals().add(new ServerPrincipal(null, login));
      return true;
    }

    throw new TeamCityFailedLoginException();
  }

  public boolean commit() throws LoginException {
    return true;
  }

  public boolean abort() throws LoginException {
    return true;
  }

  public boolean logout() throws LoginException {
    return true;
  }

  private boolean checkPassword(String login, String password) {
    return true;
  }
}
