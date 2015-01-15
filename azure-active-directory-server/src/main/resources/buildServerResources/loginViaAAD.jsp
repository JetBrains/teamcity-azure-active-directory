<%@ page import="org.jetbrains.teamcity.aad.LoginViaAADController" %>
<%@ include file="/include-internal.jsp"%>
<c:set var="aadPath"><%=LoginViaAADController.LOGIN_PATH%></c:set>
<div><a href="<c:url value='${aadPath}'/>">Log in using Azure Active Directory</a></div>