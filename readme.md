# TeamCity Azure Active Directory Integration

[![Plugin build](https://teamcity.jetbrains.com/app/rest/builds/buildType:(id:TeamCityPluginsByJetBrains_DotnetCLI_20172),branch:master/statusIcon.svg)](https://teamcity.jetbrains.com/viewType.html?buildTypeId=TeamCityPluginsByJetBrains_DotnetCLI_20172&guest=1)
[![official JetBrains project](http://jb.gg/badges/official.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

TeamCity plugin which supports authentication via Microsoft Azure Active Directory.

The plugin is compatible with TeamCity server 8.1+, 9.0+

## Installation

[Download plugin](https://plugins.jetbrains.com/plugin/9083-azure-active-directory) and install it as [an additional TeamCity plugin](http://confluence.jetbrains.com/display/TCDL/Installing+Additional+Plugins#InstallingAdditionalPlugins-InstallingTeamCityplugins).

## Configuration

### Configuring Azure Active Directory

[Register](https://msdn.microsoft.com/en-us/library/azure/dn132599.aspx#BKMK_Adding) your TeamCity server as an application in your Azure Active Directory.

Set 'SIGN-ON URL' to `{TEAMCITY_URL}/login.html`

Add 'REPLY URL' `{TEAMCITY_URL}/overview.html`

Also please check for 'CLIENT ID', 'OAUTH 2.0 AUTHORIZATION ENDPOINT' in 'App Endpoints' section.

### Configuring TeamCity server

Add the 'Microsoft Azure Active Directory' HTTP authentication module to your [authentication configuration](http://confluence.jetbrains.com/display/TCDL/Configuring+Authentication+Settings).

Specify valid 'App OAuth 2.0 authorization endpoint' and 'Client ID' retrieved from Azure Portal.

Use the 'Log in using Azure Active Directory' link available on the Login page to log in via your Azure Active Directory account.

## How it works?

This authentication scheme works as following:
- receives the UID, email, username of the AD user the from specified AD
- looks for an existing TeamCity user for the received UID; and if found, authenticates this TeamCity user
- if allowed by the scheme options, tries to find a TeamCity user by the given e-mail
- if the user is not found and user creation is allowed, a new TeamCity user is created; the email is set as the username for the newly created TeamCity user

## Plugin development

### Building plugin from sources

Issue the `mvn package` command from the root project to build your plugin. The resulting package `<artifactId>.zip` will be placed into the 'target' directory. To install the plugin, refer to the _Installation_ section.

### Contributing

Please follow [IntelliJ Coding Guidelines](http://www.jetbrains.org/display/IJOS/IntelliJ+Coding+Guidelines).

## Feedback

Please submit your questions/bugs/feature requests in the [issues](https://github.com/JetBrains/teamcity-azure-active-directory/issues).

Note: This is not a bundled plugin, please do not use the TeamCity official channels to provide feedback for this plugin.
