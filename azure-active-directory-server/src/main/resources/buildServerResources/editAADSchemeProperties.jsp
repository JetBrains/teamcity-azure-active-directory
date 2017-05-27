<%@ page import="org.jetbrains.teamcity.aad.AADConstants" %>
<%@ include file="/include-internal.jsp"%>
<%@ taglib prefix="prop" tagdir="/WEB-INF/tags/props"%>
<div><jsp:include page="/admin/allowCreatingNewUsersByLogin.jsp"/></div>
<br/>
<div>
    <prop:checkboxProperty name="allowMatchingUsersByEmail" uncheckedValue="false"/>
    <label width="100%" for="allowMatchingUsersByEmail">Allow matching users by Email</label>
</div>
<br/>
<div>
    <label width="100%" for="<%=org.jetbrains.teamcity.aad.AADConstants.AAD_TENANT_ID%>">AD Directory ID:</label><br/>
    <prop:textProperty style="width: 100%;" name="<%=org.jetbrains.teamcity.aad.AADConstants.AAD_TENANT_ID%>"/><br/>
    <span class="grayNote">The Tenant/Directory ID for the selected Azure AD.</span>
</div>
<br/>
<div>
    <label width="100%" for="<%=org.jetbrains.teamcity.aad.AADConstants.CLIENT_ID_SCHEME_PROPERTY_KEY%>">Client ID:</label><br/>
    <prop:textProperty style="width: 100%;" name="<%=org.jetbrains.teamcity.aad.AADConstants.CLIENT_ID_SCHEME_PROPERTY_KEY%>"/><br/>
    <span class="grayNote">The unique Azure Active Directory application identifier of this TeamCity server.</span>
</div>