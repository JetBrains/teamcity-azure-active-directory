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

Register a [new application with the Microsoft identity platform](https://learn.microsoft.com/en-us/entra/identity-platform/quickstart-register-app?tabs=certificate#adding-an-application) for your TeamCity server with the following parameters:

| Parameter        | Value                          |
| -                | -                              |
| Application type | Web                            |
| Redirect URIs    | `%TEAMCITY_URL%/overview.html` |
|                  | `%TEAMCITY_URL%/aadAuth.html`  |

After app registration go to the Authentication tab and select a checkbox `ID tokens (used for implicit and hybrid flows)`
under `Implicit grant and hybrid flows` section

Note: Redirect URIs could be set in the application settings (Authentication\Web).

### Configuring TeamCity server

Add the 'Microsoft Entra ID' HTTP authentication module to your [authentication configuration](http://confluence.jetbrains.com/display/TCDL/Configuring+Authentication+Settings).

Specify valid 'OAuth 2.0 authorization endpoint (v1)' and 'Application ID' retrieved from the Microsoft Entra admin center.

Note: The OAuth 2.0 authorization endpoint URL could be retrieved from the Endpoints available on the App registrations page in the Microsoft Entra admin center. 'OAuth 2.0 authorization endpoint (v2)' is [currently unsupported](https://youtrack.jetbrains.com/issue/TW-66221).

After that the 'Log in using Microsoft Entra ID' link will be available on the Login page.

#### Switching to the new UI
After successful authentication browser will be redirected to '%TEAMCITY_URL%/overview.html' page by default.
This prevents TeamCity from automatic switching to the new UI.

To fix that:
 1. In the application settings in the Microsoft Entra admin center add a new endpoint: `%TEAMCITY_URL%/aadAuth.html` to Redirect URIs list 
 2. Specify the [internal property](https://confluence.jetbrains.com/display/TCDL/Configuring+TeamCity+Server+Startup+Properties) `teamcity.aad.endpoint.type=dedicated`

Property should be set after applying changes to the app in the Microsoft Entra admin center.
No TeamCity server restart is required.

### Known issues

#### Authentication fails with HTTP 403: Origin https://login.microsoftonline.com

To fix that, specify the [internal property](https://confluence.jetbrains.com/display/TCDL/Configuring+TeamCity+Server+Startup+Properties) `rest.cors.origins=https://login.microsoftonline.com`

#### Authentication fails with HTTP 403: Origin null


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
