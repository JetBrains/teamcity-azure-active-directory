<%@ page import="org.jetbrains.teamcity.aad.AADSchemePropertiesKeys" %>
<%@ include file="/include-internal.jsp"%>
<%@ taglib prefix="prop" tagdir="/WEB-INF/tags/props"%>
<%--<div><jsp:include page="/admin/allowCreatingNewUsersByLogin.jsp"/></div>--%>
<br/>
<div>
    <label width="100%" for="<%=org.jetbrains.teamcity.aad.AADSchemePropertiesKeys.AUTH_ENDPOINT_KEY%>">App OAuth 2.0 authorization endpoint:</label><br/>
    <prop:textProperty style="width: 100%;" name="<%=org.jetbrains.teamcity.aad.AADSchemePropertiesKeys.AUTH_ENDPOINT_KEY%>"/><br/>
    <span class="grayNote">Endpoint at which TeamCity server can obtain an authorization token using OAuth 2.0.</span>
</div>
<br/>
<div>
    <label width="100%" for="<%=org.jetbrains.teamcity.aad.AADSchemePropertiesKeys.CLIENT_ID_KEY%>">Client ID:</label><br/>
    <prop:textProperty style="width: 100%;" name="<%=org.jetbrains.teamcity.aad.AADSchemePropertiesKeys.CLIENT_ID_KEY%>"/><br/>
    <span class="grayNote">The unique Azure Active Directory application identifier of this TeamCity server.</span>
</div>