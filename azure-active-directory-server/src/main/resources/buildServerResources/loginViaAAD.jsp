<%@ page import="org.jetbrains.teamcity.aad.AADConstants" %>
<%@ include file="/include-internal.jsp"%>


<c:set var="aadPath"><%=AADConstants.LOGIN_PATH%></c:set>
<div style="margin-top: 1em;">
    <a href="<c:url value='${aadPath}'/>">
        <img src="<c:url value='${teamcityPluginResourcesPath}' />img/ms-symbollockup_signin_dark.svg" alt="Sign in with Microsoft" />
    </a>
</div>
