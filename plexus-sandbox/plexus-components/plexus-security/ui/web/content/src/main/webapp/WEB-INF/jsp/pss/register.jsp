<%--
  ~ Copyright 2005-2006 The Apache Software Foundation.
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~      http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>

<%@ taglib prefix="ww" uri="/webwork" %>

<html>
<head>
  <title>Registration Page</title>
  <ww:head/>
</head>

<body>

<%@ include file="/WEB-INF/jsp/pss/formValidationResults.jspf" %>

<h2>Register for an Account</h2>
   
<ww:form action="register" namespace="/security" theme="xhtml"
         id="registerForm" method="post" name="register" cssClass="security register">     
  <%@ include file="/WEB-INF/jsp/pss/userCredentials.jspf" %>
  <ww:submit type="submit" value="Register" name="registerButton" />
  <ww:submit type="cancel" value="Cancel"   name="cancelButton"   />
</ww:form>

</body>

</html>
