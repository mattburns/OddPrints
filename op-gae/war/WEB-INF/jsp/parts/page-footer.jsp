<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page isELIgnored="false" %>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>

<div data-role="content">
    <!-- footer padding -->
    <br/>
    <br/>
</div>

<div data-role="footer" class="ui-bar footer-links"  data-theme="c">
    <div class="center-wrapper">
        <div id="mode-chooser">
            <c:choose>
                <c:when test="${basicMode}">
                    basic | <a href="/edit" data-ajax="false">turbo</a>
                </c:when>
                <c:otherwise>
                    <a class="force-basic" href="/upload/basic" data-ajax="false">basic</a> | turbo
                </c:otherwise>
            </c:choose>
        </div>
        &copy; ${html_year} <a href="/mattburnsltd">matt burns ltd</a> |
        <a href="/terms">terms</a> |
        <a href="/privacy">privacy</a> |
        <a href="/contact">contact</a> |
        <a href="/help">help</a>
        <div class="fb-like" data-href="http://www.oddprints.com" data-send="false" data-layout="button_count" data-width="45" data-show-faces="true"></div>
    </div>
    <div class="footer-right">From the makers of <a class='logo' href='http://www.stolencamerafinder.com'><span class='scf-logo-a'>stolen</span><span class='scf-logo-b'>camera</span><span class='scf-logo-a'>finder</span></a></div>
</div>

<script type="text/javascript">
$(document).ready(function() {
    if (isSupportedBrowser()) {
        $("#mode-chooser").show();
    } else {
        // don't give unsupported browsers the choice
        $("#mode-chooser").hide();
    }

    var basicModeVar = ${not empty basicMode and basicMode};
    if (isSupportedBrowser() && !basicModeVar) {
        $("a[href='/upload/basic']").attr("href", "/edit");
        $("a[href='/upload/basicpano']").attr("href", "/editpano");
        // oops, shouldn't change this one, change back!
        $(".force-basic").attr("href", "/upload/basic");
    } else if (!isFileInputSupported()) {
        $(".get-started").attr("href", "/mobile-safari-error?agent=" + navigator.userAgent);
    } else {
        // stick with basic upload
    }
});
</script>


