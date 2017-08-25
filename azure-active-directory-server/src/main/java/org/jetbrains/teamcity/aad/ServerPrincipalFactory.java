package org.jetbrains.teamcity.aad;

import jetbrains.buildServer.PluginTypes;
import jetbrains.buildServer.serverSide.auth.AuthModuleUtil;
import jetbrains.buildServer.serverSide.auth.ServerPrincipal;
import jetbrains.buildServer.users.*;
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

  private static final boolean DEFAULT_ALLOW_CREATING_NEW_USERS_BY_LOGIN = true;
  private static final String ALLOW_MATCHING_USERS_BY_EMAIL = "allowMatchingUsersByEmail";
  private static final String ALLOW_USER_DETAILS_SYNC = "allowUserDetailsSync";

  @NotNull private final UserModel myUserModel;

  public ServerPrincipalFactory(@NotNull UserModel userModel) {
    myUserModel = userModel;
  }

  @NotNull
  public ServerPrincipal getServerPrincipal(@NotNull String userName, @NotNull final String aadUserUID, @Nullable final String displayName, @Nullable final String email, @NotNull Map<String, String> schemeProperties) {
    // Match by UID
    final SUser userWithTheSameUID = findExistingUserByUID(aadUserUID);
    if (userWithTheSameUID != null) {
      if(allowUserDetailsSync(schemeProperties)) {
          userWithTheSameUID.updateUserAccount(userName, displayName, email);
      }
      return new ServerPrincipal(AADConstants.AAD_AUTH_SCHEME_NAME, userWithTheSameUID.getUsername());
    }

    // Match by email
    if(email != null && allowMatchUserByEmail(schemeProperties)){
      final SUser userWithTheSameEmail = findExistingUserByEmail(email);
      if(userWithTheSameEmail != null){
        final String usernameFound = userWithTheSameEmail.getUsername();
        LOG.info(String.format("Associated aad user %s with TeamCity user %s by email %s", userName, usernameFound, email));
        userWithTheSameEmail.setUserProperty(AADConstants.OID_USER_PROPERTY_KEY, aadUserUID);
        if(allowUserDetailsSync(schemeProperties)) {
          userWithTheSameEmail.updateUserAccount(userName, displayName, email);
        }
        return new ServerPrincipal(AADConstants.AAD_AUTH_SCHEME_NAME, userWithTheSameEmail.getUsername());
      }
    }

    // Create user and populate with users details
    final boolean allowCreatingNewUsersByLogin = AuthModuleUtil.allowCreatingNewUsersByLogin(schemeProperties, DEFAULT_ALLOW_CREATING_NEW_USERS_BY_LOGIN);
    final HashMap<PropertyKey, String> userProperties = new HashMap<PropertyKey, String>() {{
      put(AADConstants.OID_USER_PROPERTY_KEY, aadUserUID);
    }};

    if (allowCreatingNewUsersByLogin) {
      SUser createUser = myUserModel.createUserAccount(AADConstants.AAD_AUTH_SCHEME_NAME, userName);
      createUser.updateUserAccount(userName, displayName, email);
      createUser.setUserProperty(AADConstants.OID_USER_PROPERTY_KEY, aadUserUID);
    }
   return new ServerPrincipal(AADConstants.AAD_AUTH_SCHEME_NAME, userName, null, allowCreatingNewUsersByLogin, userProperties);
  }

  @Nullable
  private SUser findExistingUserByEmail(@NotNull String email) {
    for(SUser user : myUserModel.getAllUsers().getUsers()){
      if(email.equalsIgnoreCase(user.getEmail())) return user;
    }
    return null;
  }

  @Nullable
  private SUser findExistingUserByUID(@NotNull String userUID) {
    UserSet<SUser> userSet = myUserModel.findUsersByPropertyValue(AADConstants.OID_USER_PROPERTY_KEY, userUID, true);
    Set<SUser> users = userSet.getUsers();
    if (!users.isEmpty()) {
      SUser user = users.iterator().next();
      return user;
    }
    return null;
  }

  private static boolean allowMatchUserByEmail(Map<String, String> schemeProperties) {
    Object value = schemeProperties.get(ALLOW_MATCHING_USERS_BY_EMAIL);
    return value != null && Boolean.parseBoolean((String) value);
  }

  private static boolean allowUserDetailsSync(Map<String, String> schemeProperties) {
    Object value = schemeProperties.get(ALLOW_USER_DETAILS_SYNC);
    return value != null && Boolean.parseBoolean((String) value);
  }

}
