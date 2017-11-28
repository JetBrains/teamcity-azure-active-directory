<%@ page import="org.jetbrains.teamcity.aad.AADConstants" %>
<%@ include file="/include-internal.jsp"%>
<%@ taglib prefix="prop" tagdir="/WEB-INF/tags/props"%>
<div><jsp:include page="/admin/allowCreatingNewUsersByLogin.jsp"/></div>
<br/>
<div>
    <prop:checkboxProperty name="allowUserDetailsSync" uncheckedValue="false"/>
    <label width="100%" for="allowUserDetailsSync">Allow synchronization of user details with Azure when logging in</label>
</div>
<br/>
<div>
    <prop:checkboxProperty name="allowMatchingUsersByEmail" uncheckedValue="false"/>
    <label width="100%" for="allowMatchingUsersByEmail">Allow matching users by Email</label>
</div>
<br/>
<div>
    <label width="100%" for="<%=org.jetbrains.teamcity.aad.AADConstants.AUTH_ENDPOINT_SCHEME_PROPERTY_KEY%>">OAuth 2.0 authorization endpoint:</label><br/>
    <prop:textProperty className="longField" name="<%=org.jetbrains.teamcity.aad.AADConstants.AUTH_ENDPOINT_SCHEME_PROPERTY_KEY%>"/><br/>
    <span class="grayNote">Endpoint at which TeamCity server can obtain an authorization token using OAuth 2.0.</span>
</div>
<br/>
<div>
    <label width="100%" for="<%=org.jetbrains.teamcity.aad.AADConstants.CLIENT_ID_SCHEME_PROPERTY_KEY%>">Application ID:</label><br/>
    <prop:textProperty className="longField" name="<%=org.jetbrains.teamcity.aad.AADConstants.CLIENT_ID_SCHEME_PROPERTY_KEY%>"/><br/>
    <span class="grayNote">The unique Azure Active Directory application identifier of this TeamCity server.</span>
</div>