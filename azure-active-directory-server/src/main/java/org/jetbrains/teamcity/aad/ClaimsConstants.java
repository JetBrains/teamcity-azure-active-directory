package org.jetbrains.teamcity.aad;

import jetbrains.buildServer.PluginTypes;
import jetbrains.buildServer.users.PluginPropertyKey;
import jetbrains.buildServer.users.PropertyKey;

/**
 * @author Evgeniy.Koshkin
 */
public class ClaimsConstants {

	public static final String NONCE_CLAIM = "nonce";
	public static final String NAME_CLAIM = "unique_name";
	public static final String OID_CLAIM = "oid"; //object ID
	public static final String APPID_CLAIM = "appid";
	public static final String EMAIL_CLAIM = "upn";
	public static final String ERROR_CLAIM = "error";
	public static final String ERROR_DESCRIPTION_CLAIM = "error_description";
	
}
