package org.jetbrains.teamcity.aad;

import jetbrains.buildServer.serverSide.auth.AuthModuleUtil;
import jetbrains.buildServer.serverSide.auth.ServerPrincipal;
import jetbrains.buildServer.users.PropertyKey;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.users.UserModel;
import jetbrains.buildServer.users.UserSet;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Evgeniy.Koshkin
 */
public class ServerPrincipalFactory {

  private static final Logger LOG = Logger.getLogger(ServerPrincipalFactory.class);

  private static final boolean DEFAULT_ALLOW_CREATING_NEW_USERS_BY_LOGIN = true;;

  @NotNull private final UserModel myUserModel;

  public ServerPrincipalFactory(@NotNull UserModel userModel) {
    myUserModel = userModel;
  }

  @NotNull
  public ServerPrincipal getServerPrincipal(@NotNull String userName, @NotNull final String aadUserUID, @Nullable final String email, @NotNull Map<String, String> schemeProperties) {
    final ServerPrincipal existingPrincipal = findExistingPrincipalByUID(aadUserUID);
    if(existingPrincipal != null) return existingPrincipal;
    
    if(email != null && allowMatchUserByEmail(schemeProperties)){ 
      final SUser userWithTheSameEmail = findExistingUserByEmail(email);
      if(userWithTheSameEmail != null){
        final String username = userWithTheSameEmail.getUsername();
        LOG.info(String.format("Associated aad user %s with TeamCity user %s by email %s", userName, username, email));
        userWithTheSameEmail.setUserProperty(AADConstants.OID_USER_PROPERTY_KEY, aadUserUID);
        return new ServerPrincipal(AADConstants.AAD_AUTH_SCHEME_NAME, username);
      }
    }
    
    final boolean allowCreatingNewUsersByLogin = AuthModuleUtil.allowCreatingNewUsersByLogin(schemeProperties, DEFAULT_ALLOW_CREATING_NEW_USERS_BY_LOGIN);
    final HashMap<PropertyKey, String> userProperties = new HashMap<PropertyKey, String>() {{
      put(AADConstants.OID_USER_PROPERTY_KEY, aadUserUID);
    }};
    return allowCreatingNewUsersByLogin ? new ServerPrincipal(AADConstants.AAD_AUTH_SCHEME_NAME, userName, null, allowCreatingNewUsersByLogin, userProperties) : null;
  }

  @Nullable
  private SUser findExistingUserByEmail(@NotNull String email) {
    for(SUser user : myUserModel.getAllUsers().getUsers()){
      if(email.equalsIgnoreCase(user.getEmail())) return user;
    }
    return null;
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

  private static boolean allowMatchUserByEmail(Map<String, String> schemeProperties) {
    Object value = schemeProperties.get(AADConstants.ALLOW_MATCHING_USERS_BY_EMAIL);
    return value != null && Boolean.parseBoolean((String) value);
  }
}
