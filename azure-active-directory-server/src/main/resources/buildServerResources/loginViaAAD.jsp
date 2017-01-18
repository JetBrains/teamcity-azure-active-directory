<%@ page import="org.jetbrains.teamcity.aad.LoginViaAADController" %>
<%@ include file="/include-internal.jsp"%>
<c:set var="aadPath"><%=LoginViaAADController.LOGIN_PATH%></c:set>

<c:choose>
    <c:when test="${AAD_settings.getDisableLoginForm()}">
        <style>
			/*Hide the form before removing it to avoid the flickering during the page load.*/
	        .loginForm {
	            display: none;
	        }
	    </style>
	
	    <script type="text/javascript">
	        (function(){
	        	document.getElementsByTagName("form")[0]
	        			.remove();
	        })();
	    </script>
	    
	    
		<form action="<c:url value='${aadPath}'/>" method="GET">
		    <input class="btn loginButton" style="display: block" type="submit" name="submitLogin"
		           value="Log in using Azure Active Directory">
		</form>
    </c:when>    
    <c:otherwise>
    	<div><a href="<c:url value='${aadPath}'/>">Log in using Azure Active Directory</a></div>
    </c:otherwise>
</c:choose>