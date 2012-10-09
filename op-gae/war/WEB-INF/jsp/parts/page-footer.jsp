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
                    <a href="/upload/basic" data-ajax="false">basic</a> | turbo
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
</div>

<script type="text/javascript">
$(document).ready(function() {
    if (isSupportedBrowser()) {
        $("#mode-chooser").show();
    } else {
        // don't give unsupported browsers the choice
        $("#mode-chooser").hide();
    }
});

  var _gaq = _gaq || [];
  _gaq.push(['_setAccount', 'UA-32205253-1']);
  _gaq.push(['_setDomainName', 'oddprints.com']);
  _gaq.push(['_trackPageview']);

  (function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();
</script>
