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
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page isELIgnored="false" %>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>

<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:og="http://ogp.me/ns#"
      xmlns:fb="http://www.facebook.com/2008/fbml"
      xmlns:c="http://java.sun.com/jsp/jstl/core">
      
<jsp:include page="/WEB-INF/jsp/parts/html-head.jsp">
    <jsp:param name="titleText" value=" - Admin" />
    <jsp:param name="descriptionText" value="Admin page." />
</jsp:include>
<body>

<div data-role="page" id="page-admin">

    <jsp:include page="/WEB-INF/jsp/parts/page-header.jsp" />

    <div data-role="content">
    
        <h1>Settings</h1>
            
        <div>
            <dl>
                <c:forEach var="setting" items="${it.settings}">
                    <dt>${setting.key}</dt> <dd>${setting.value} (<a href="/admin/settings/delete/${setting.key}" data-ajax="false">delete</a>)</dd>
                </c:forEach>
            </dl>
        </div>
        
        <h3>Add new setting</h3>
        <div data-role="fieldcontain" class="ui-hide-label">
            <label for="key">Key:</label>
            <input type="text" name="key" id="key" value="" placeholder="Key"/>
        </div>
        <div data-role="fieldcontain" class="ui-hide-label">
            <label for="value">Value:</label>
            <input type="text" name="value" id="value" value="" placeholder="Value"/>
        </div>
        
        <a id="add-setting-link" data-ajax="false" href=""></a>
        
        <h3>Initialize the dev database</h3>
        Use the file in your dropbox (maybe <a data-ajax="false" href="file:///media/data/Dropbox/OddPrints-Keys-and-DB-init.html">here</a>).
    </div>    
    
    <jsp:include page="/WEB-INF/jsp/parts/page-footer.jsp" />

</div>

<script type="text/javascript">
$(document).ready(function() {  
    $("input").bind( "change", function(event, ui) {
        var url = "";
        var key = $("#key").val();
        var value = $("#value").val();
        
        if (key.length > 0 && value.length > 0) {
            url = "/admin/settings/put/" + key + "/" + value;
        }
        
        $("#add-setting-link").attr("href", url);
        $("#add-setting-link").html(url);
    });
});
</script>
</body>
</html>
