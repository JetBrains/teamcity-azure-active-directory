package org.jetbrains.teamcity.aad;

import jetbrains.buildServer.serverSide.auth.AuthModuleUtil;
import jetbrains.buildServer.serverSide.auth.ServerPrincipal;
import jetbrains.buildServer.users.PropertyKey;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.users.UserModel;
import jetbrains.buildServer.users.UserSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Evgeniy.Koshkin
 */
public class ServerPrincipalFactory {

  private static final boolean DEFAULT_ALLOW_CREATING_NEW_USERS_BY_LOGIN = true;

  @NotNull private final UserModel myUserModel;

  public ServerPrincipalFactory(@NotNull UserModel userModel) {
    myUserModel = userModel;
  }

  @NotNull
  public ServerPrincipal getServerPrincipal(@NotNull String userName, @NotNull final String userUID, @NotNull Map<String, String> schemeProperties) {
    final ServerPrincipal existingPrincipal = findExistingPrincipalByUID(userUID);
    if(existingPrincipal != null) return existingPrincipal;

    final boolean allowCreatingNewUsersByLogin = AuthModuleUtil.allowCreatingNewUsersByLogin(schemeProperties, DEFAULT_ALLOW_CREATING_NEW_USERS_BY_LOGIN);
    final HashMap<PropertyKey, String> userProperties = new HashMap<PropertyKey, String>() {{
      put(AADConstants.OID_USER_PROPERTY_KEY, userUID);
    }};
    return new ServerPrincipal(AADConstants.AAD_AUTH_SCHEME_NAME, userName, null, allowCreatingNewUsersByLogin, userProperties);
  }

  @Nullable
  private ServerPrincipal findExistingPrincipalByUID(@NotNull final String userUID) {
    final UserSet<SUser> userSet = myUserModel.findUsersByPropertyValue(AADConstants.OID_USER_PROPERTY_KEY, userUID, true);
    final Set<SUser> users = userSet.getUsers();
    if (!users.isEmpty()) {
      final SUser user = users.iterator().next();
      return new ServerPrincipal(AADConstants.AAD_AUTH_SCHEME_NAME, user.getUsername());
    }
    return null;
  }
}
