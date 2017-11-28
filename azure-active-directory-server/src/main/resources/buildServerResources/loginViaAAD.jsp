<%@ page import="org.jetbrains.teamcity.aad.AADConstants" %>
<%@ include file="/include-internal.jsp"%>
<c:set var="aadPath"><%=AADConstants.LOGIN_PATH%></c:set>
<div><a href="<c:url value='${aadPath}'/>">Log in using Azure Active Directory</a></div>