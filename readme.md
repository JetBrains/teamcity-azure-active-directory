
TeamCity plugin which supports authentication via Microsoft Azure Active Directory.

Plugin compatible with TeamCity server 8.1+, 9.0+

# Installation

[Download latest plugin version](https://teamcity.jetbrains.com/repository/download/TeamCityPluginsByJetBrains_AzureSupport_AzureActiveDirectoryPlugi/lastSuccessful/azure-active-directory.zip) and [install it as ususal](http://confluence.jetbrains.com/display/TCD9/Installing+Additional+Plugins#InstallingAdditionalPlugins-InstallingTeamCityplugins)

# Configuration

## Configuring Azure Active Directory

[Register](https://msdn.microsoft.com/en-us/library/azure/dn132599.aspx#BKMK_Adding) your TeamCity server as an application in your Azure Active Directory.

Set 'SIGN-ON URL' to {TEAMCITY_URL}/login.html

Add 'REPLY URL' {TEAMCITY_URL}/overview.html

Also please check for 'CLIENT ID', 'OAUTH 2.0 AUTHORIZATION ENDPOINT' in 'App Endpoints' section.

## Configuring TeamCity server

Add 'Microsoft Azure Active Directory' HTTP authentication module to your [authentication configuration](http://confluence.jetbrains.com/display/TCD9/Configuring+Authentication+Settings).

Specify valid 'App OAuth 2.0 authorization endpoint' and 'Client ID' retrieved from Azure Portal.

Use 'Log in using Azure Active Directory' link available on Login page to login via your Azure Active Directory account.

# Plugin development

## Building plugin from sources

Issue 'mvn package' command from the root project to build your plugin. Resulting package <artifactId>.zip will be placed in 'target' directory.

To install the plugin, put zip archive to 'plugins' dir under TeamCity data directory and restart the server.

[Build on public CI server](https://teamcity.jetbrains.com/viewType.html?buildTypeId=TeamCityPluginsByJetBrains_AzureSupport_AzureActiveDirectoryPlugi)

# Feedback

Please submit your questions/bugs/feature requests [here](https://github.com/ekoshkin/teamcity-azure-active-directory/issues)

 
