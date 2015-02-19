package org.jetbrains.teamcity.aad;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jetbrains.buildServer.controllers.interceptors.auth.HttpAuthenticationResult;
import jetbrains.buildServer.controllers.interceptors.auth.HttpAuthenticationSchemeAdapter;
import jetbrains.buildServer.serverSide.auth.LoginConfiguration;
import jetbrains.buildServer.serverSide.auth.ServerPrincipal;
import jetbrains.buildServer.users.PropertyKey;
import org.apache.commons.codec.binary.Base64;
import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

/**
 * @author Evgeniy.Koshkin
 */
public class AADAuthenticationScheme extends HttpAuthenticationSchemeAdapter {

  private static final String POST_METHOD = "POST";
  private static final String ID_TOKEN = "id_token";
  private static final String AAD_REALM = "AAD";
  private static final String NONCE = "nonce";
  private static final String NAME = "name";
  private static final String OVERVIEW_HTML = "/overview.html";

  public AADAuthenticationScheme(@NotNull final LoginConfiguration loginConfiguration) {
    loginConfiguration.registerAuthModuleType(this);
  }

  @NotNull
  @Override
  protected String doGetName() {
    return "AAD";
  }

  @NotNull
  @Override
  public String getDisplayName() {
    return "Microsoft Azure Active Directory";
  }

  @NotNull
  @Override
  public String getDescription() {
    return "Authentication via Microsoft Azure Active Directory";
  }

  @NotNull
  @Override
  public HttpAuthenticationResult processAuthenticationRequest(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Map<String, String> properties) throws IOException {
    if (!request.getMethod().equals(POST_METHOD)) return HttpAuthenticationResult.notApplicable();

    final String idToken = request.getParameter(ID_TOKEN);
    if(idToken == null) return HttpAuthenticationResult.notApplicable();

    final String[] jwtParts = idToken.split("\\.");
    final JsonObject jwtPayload = new JsonParser().parse(new String(Base64.decodeBase64(jwtParts[1].getBytes()))).getAsJsonObject();

    final JsonElement nonce = jwtPayload.get(NONCE);
    final JsonElement name = jwtPayload.get(NAME);

    if (nonce == null || name == null) return HttpAuthenticationResult.unauthenticated();
    if(!nonce.getAsString().equals(SessionUtil.getSessionId(request))) return HttpAuthenticationResult.unauthenticated();

    final ServerPrincipal principal = new ServerPrincipal(AAD_REALM, name.getAsString(), null, true, Collections.<PropertyKey, String>emptyMap());

    return HttpAuthenticationResult.authenticated(principal, true).withRedirect(OVERVIEW_HTML);
  }
}
