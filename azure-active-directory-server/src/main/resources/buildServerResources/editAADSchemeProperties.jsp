<%@ page import="org.jetbrains.teamcity.aad.AADConstants" %>
<%@ include file="/include-internal.jsp"%>
<%@ taglib prefix="prop" tagdir="/WEB-INF/tags/props"%>
<div><jsp:include page="/admin/allowCreatingNewUsersByLogin.jsp"/></div>
<br/>
<div>
    <prop:checkboxProperty name="allowMatchingUsersByEmail" uncheckedValue="false"/>
    <label width="100%" for="<%=org.jetbrains.teamcity.aad.AADConstants.ALLOW_MATCHING_USERS_BY_EMAIL%>">Allow matching users by Email</label>
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
<br/>
<div>
    <prop:checkboxProperty name="<%=org.jetbrains.teamcity.aad.AADConstants.ENABLE_TOKEN_AUTHENTICATION%>" uncheckedValue="false"/>
    <label width="100%" for="<%=org.jetbrains.teamcity.aad.AADConstants.ENABLE_TOKEN_AUTHENTICATION%>">Enable token authentication</label><br/>
    <span class="grayNote">Enable bearer token authentication on HTTP requests for API calls</span>
</div>
<br/>
<div>
    <label width="100%" for="<%=org.jetbrains.teamcity.aad.AADConstants.ID_CLAIM_TOKEN_AUTHENTICATION%>">Claim:</label><br/>
    <prop:selectProperty name="<%=org.jetbrains.teamcity.aad.AADConstants.ID_CLAIM_TOKEN_AUTHENTICATION%>">
    	<prop:option value="<%=org.jetbrains.teamcity.aad.ClaimsConstants.APPID_CLAIM%>">Application ID</prop:option>
    	<prop:option value="<%=org.jetbrains.teamcity.aad.ClaimsConstants.OID_CLAIM%>">Object Id</prop:option>
    </prop:selectProperty>
    </br>
    <span class="grayNote">The claim that will be used for matching the bearer token with a TeamCity user's email field.</span>
</div>

<script type="text/javascript">
    (function(){
    
    	var checkbox = document.getElementsByName("prop:<%=org.jetbrains.teamcity.aad.AADConstants.ENABLE_TOKEN_AUTHENTICATION%>")[0];
    	checkbox.onchange = function() {
    		document.getElementsByName("prop:<%=org.jetbrains.teamcity.aad.AADConstants.ID_CLAIM_TOKEN_AUTHENTICATION%>")[0].disabled = !checkbox.checked;
    	}
    	checkbox.onchange();
    	
    })();
</script>
