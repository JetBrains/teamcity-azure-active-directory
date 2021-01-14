<%@ include file="/include-internal.jsp" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<%--
  ~ Copyright 2000-2021 JetBrains s.r.o.
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~ http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>

<jsp:useBean id="params" class="org.jetbrains.teamcity.aad.AADParametersProvider"/>

<style type="text/css">
    .modalDialogBody .content {
        overflow: hidden;
    }
    .runnerFormTable.aadSettings th {
        width: 40%;
        white-space: nowrap;
    }
</style>

<c:set var="help"><a href="https://portal.azure.com/#blade/Microsoft_AAD_IAM/ActiveDirectoryMenuBlade/RegisteredApps"
                     target="_blank" showdiscardchangesmessage="false"><bs:helpIcon iconTitle=""/></a>
</c:set>

<table class="runnerFormTable aadSettings">
    <tbody>
    <tr>
        <td colspan="2">
            <jsp:include page="/admin/allowCreatingNewUsersByLogin.jsp"/>
        </td>
    </tr>
    <tr>
        <td colspan="2">
            <props:checkboxProperty name="${params.allowUserDetailsSync}" uncheckedValue="false"/>
            <label for="${params.allowUserDetailsSync}">
                Allow synchronization of user details with Azure AD when logging in
            </label>
        </td>
    </tr>
    <tr>
        <td colspan="2">
            <props:checkboxProperty name="${params.allowMatchingUsersByEmail}" uncheckedValue="false"/>
            <label for="${params.allowMatchingUsersByEmail}">Allow matching users by e-mail</label>
        </td>
    </tr>
    <tr>
        <th>
            <label for="${params.authEndpoint}">Endpoint URL:<l:star/></label>
        </th>
        <td>
            <props:textProperty name="${params.authEndpoint}" className="longField" />
            <span class="error" id="error_${params.authEndpoint}"></span>
            <span class="grayNote">
                Azure OAuth 2.0 authorization endpoint URL. ${help}
            </span>
        </td>
    </tr>
    <tr>
        <th>
            <label for="${params.applicationId}">Application ID:<l:star/></label>
        </th>
        <td>
            <props:textProperty name="${params.applicationId}" className="longField" />
            <span class="error" id="error_${params.applicationId}"></span>
            <span class="grayNote">
                Azure Active Directory application identifier. ${help}
            </span>
        </td>
    </tr>
    <tr class="advancedSetting">
        <th><label for="${params.authPrompt}">Prompt type:</label></th>
        <td>
            <props:selectProperty name="${params.authPrompt}" enableFilter="true" className="mediumField">
                <props:option value="">&lt;Default&gt;</props:option>
                <props:option value="login">Login</props:option>
                <props:option value="consent">Consent</props:option>
            </props:selectProperty>
            <br/>
            <span class="grayNote">The dialog type displayed while Azure AD authorization.</span>
        </td>
    </tr>
    </tbody>
</table>
