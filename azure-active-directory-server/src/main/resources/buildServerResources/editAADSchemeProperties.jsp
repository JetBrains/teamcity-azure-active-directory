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
    <label width="100%" for="<%=org.jetbrains.teamcity.aad.AADConstants.AUTH_ENDPOINT_SCHEME_PROPERTY_KEY%>">App OAuth 2.0 authorization endpoint:</label><br/>
    <prop:textProperty style="width: 100%;" name="<%=org.jetbrains.teamcity.aad.AADConstants.AUTH_ENDPOINT_SCHEME_PROPERTY_KEY%>"/><br/>
    <span class="grayNote">Endpoint at which TeamCity server can obtain an authorization token using OAuth 2.0.</span>
</div>
<br/>
<div>
    <label width="100%" for="<%=org.jetbrains.teamcity.aad.AADConstants.CLIENT_ID_SCHEME_PROPERTY_KEY%>">Client ID:</label><br/>
    <prop:textProperty style="width: 100%;" name="<%=org.jetbrains.teamcity.aad.AADConstants.CLIENT_ID_SCHEME_PROPERTY_KEY%>"/><br/>
    <span class="grayNote">The unique Azure Active Directory application identifier of this TeamCity server.</span>
</div>
<br/>
<div>
    <prop:checkboxProperty name="<%=org.jetbrains.teamcity.aad.AADConstants.DISABLE_LOGIN_FORM%>" uncheckedValue="false"/>
    <label width="100%" for="<%=org.jetbrains.teamcity.aad.AADConstants.DISABLE_LOGIN_FORM%>">Hide login form</label><br/>
    <span class="grayNote">Hide user/password login form on Teamcity login page.</span>
</div>