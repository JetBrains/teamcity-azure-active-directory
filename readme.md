# TeamCity Microsoft Entra ID Integration

[![Plugin build](https://teamcity.jetbrains.com/app/rest/builds/buildType:(id:TeamCityPluginsByJetBrains_AzureSupport_AzureActiveDirectoryPlugi),branch:master/statusIcon.svg)](https://teamcity.jetbrains.com/viewType.html?buildTypeId=TeamCityPluginsByJetBrains_AzureSupport_AzureActiveDirectoryPlugi&guest=1)
[![official JetBrains project](http://jb.gg/badges/official.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

TeamCity plugin which supports authentication via Microsoft Entra ID(previously Azure Active Directory).

The plugin is compatible with TeamCity server 10.0+

## Installation

[Download the plugin](https://plugins.jetbrains.com/plugin/9083-azure-active-directory) and install it as [an additional TeamCity plugin](http://confluence.jetbrains.com/display/TCDL/Installing+Additional+Plugins#InstallingAdditionalPlugins-InstallingTeamCityplugins).

## Configuration

### Configuring Microsoft Entra ID

1. [Register an application in Microsoft Entra ID](https://learn.microsoft.com/en-us/entra/identity-platform/quickstart-register-app). In the **Redirect URI (optional)** section, set:
    - **Platform:** `Web`
    - **Redirect URI:** `%TEAMCITY_URL%/aadAuth.html` (where `%TEAMCITY_URL%` is the base URL of your TeamCity server)
2. Once the application is registered, open its **Authentication** tab. Under **Implicit grant and hybrid flows**, select **ID tokens (used for implicit and hybrid flows)**.
3. *(Optional)* Add more redirect URIs in the application settings | **Authentication** | **Web**, if needed.

### Configuring TeamCity server

1. Add the **Microsoft Entra ID** authentication module in TeamCity [Authentication settings](https://www.jetbrains.com/help/teamcity/configuring-authentication-settings.html).
2. Enter the **Application (client) ID** from the application page in Microsoft Entra admin center.
3. Copy the **OAuth 2.0 authorization endpoint (v1)** URL from **Endpoints** on the App registrations page in the Microsoft Entra admin center, and paste it into the **Endpoint URL** field.

> [!NOTE]  
> The **OAuth 2.0 authorization endpoint (v2)** is [currently unsupported](https://youtrack.jetbrains.com/issue/TW-66221).

After completing these steps, the **Log in using Microsoft Entra ID** link will appear on the TeamCity login page.

## Known issues

### Authentication fails with HTTP 403: Origin https://login.microsoftonline.com

To fix that, specify the [internal property](https://confluence.jetbrains.com/display/TCDL/Configuring+TeamCity+Server+Startup+Properties) `rest.cors.origins=https://login.microsoftonline.com`

### Authentication fails with HTTP 403: Origin null

> 403 Forbidden: Responding with 403 status code due to failed CSRF check: request's "Origin" header value "null" does not match Host/X-Forwarded-Host header values or server's CORS-trusted hosts, consider adding "Origin: %TEAMCITY_URL%" header.

Modern browsers can set `Origin: null` and `Upgrade-Insecure-Requests` headers while replying from HTTPS Microsoft Entra ID endpoint to the HTTP URL of the TeamCity server due to [security reasons](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Upgrade-Insecure-Requests).

Possible solutions:
* Use HTTPS for TeamCity server and specify it in the Entra ID application settings
* For testing, you can specify the [internal property](https://confluence.jetbrains.com/display/TCDL/Configuring+TeamCity+Server+Startup+Properties) `rest.cors.origins=null` (**insecure, don't use it in production**)

## How it works?

This plugin uses the [OAuth 2.0 OpenID Connect](https://docs.microsoft.com/en-us/azure/active-directory/develop/active-directory-protocols-openid-connect-code) authentication protocol and works as follows:
- receives the UID, e-mail, username of the AD user the from the specified Microsoft Entra ID
- looks for an existing TeamCity user for the received UID; and if found, authenticates this TeamCity user
- if allowed by the scheme options, tries to find a TeamCity user using the given e-mail and marks it by UID
- if the user was found and user details synchronization is enabled, it will update the user data
- if the user is not found and user creation is allowed, a new TeamCity user is created; the e-mail is set as the username for the newly created TeamCity user.

## Plugin development

### Building plugin from sources

This project uses gradle as the build system. You can easily open it in [IntelliJ IDEA](https://www.jetbrains.com/idea/help/importing-project-from-gradle-model.html) or [Eclipse](http://gradle.org/eclipse/).
Issue the `build` command from the root project to build your plugin. The resulting package will be placed in the `distributions` directory.

### Contributing

Please follow [IntelliJ Coding Guidelines](http://www.jetbrains.org/display/IJOS/IntelliJ+Coding+Guidelines).

## Feedback

Please submit your questions/bugs/feature requests in the [issues](https://github.com/JetBrains/teamcity-azure-active-directory/issues).

Note: This is not a bundled plugin, please do not use the TeamCity official channels to provide feedback for this plugin.
