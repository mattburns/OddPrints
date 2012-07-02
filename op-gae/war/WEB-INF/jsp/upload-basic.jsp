<%--
Copyright 2011 Matt Burns

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
--%>

<!DOCTYPE html>
<%@page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page isELIgnored="false" %>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>

<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:og="http://ogp.me/ns#"
      xmlns:fb="http://www.facebook.com/2008/fbml"
      xmlns:c="http://java.sun.com/jsp/jstl/core">
      
<jsp:include page="/WEB-INF/jsp/parts/html-head.jsp" />
<body>

<div data-role="page" id="page-upload">

    <jsp:include page="/WEB-INF/jsp/parts/page-header.jsp" />

    <div data-role="content">
        <div id="choose-a-file">
            <h2>Upload a photo</h2>
            
            <form action="/edit/basic" data-ajax="false" method="POST" id="file-form" enctype="multipart/form-data">
                <input type="file" id="file-chooser" name="myFile" >
            </form>
            <p>Or just play with the <a id="sample-photo-link" data-ajax="false" href="/edit/basic/sample">sample photo</a>.</p>
        </div>
    </div>
       
    <jsp:include page="/WEB-INF/jsp/parts/page-footer.jsp" />
</div>

<script type="text/javascript">
$(document).ready(function() {
    $("#file-chooser").change(function() {
        $.mobile.showPageLoadingMsg();
        $("#file-form").submit();
    });
});
</script>
</body>
</html>