<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page isELIgnored="false" %>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>

    <div id="fb-root"></div>
    <script>(function(d, s, id) {
      var js, fjs = d.getElementsByTagName(s)[0];
      if (d.getElementById(id)) return;
      js = d.createElement(s); js.id = id;
      js.src = "//connect.facebook.net/en_GB/all.js#xfbml=1&appId=154500891318156";
      fjs.parentNode.insertBefore(js, fjs);
    }(document, 'script', 'facebook-jssdk'));</script>

    <div data-role="header">
        <h1><a href="/" class="logo" data-ajax="false">
        <span class="oddprints-logo-a">Odd</span><span class="oddprints-logo-b">Prints</span></a></h1>
    </div>
    
    <div class="fb-like-box" data-href="https://www.facebook.com/OddPrints" data-width="250" data-height="570" data-colorscheme="light" data-show-faces="true" data-header="true" data-stream="true" data-show-border="true"></div>
