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
    <jsp:param name="titleText" value=" - Help" />
    <jsp:param name="descriptionText" value="Answers to frequently asked questions and a place to ask questions you may have about OddPrints." />
</jsp:include>
<body>

<div data-role="page" id="page-help">

    <jsp:include page="/WEB-INF/jsp/parts/page-header.jsp" />

    <div data-role="content">
       
        <h2>Is it free?</h2>
        <p>Yes! This service is totally free. Even the 
        <a data-ajax="false" href="https://github.com/mattburns/OddPrints">source code</a>
        for this site is free. How awesome is that?</p>

        <h2>Can I order prints from you?</h2>
        <p>Sure thing. Instead of downloading your image, just click the link below the 
        download button to order prints from us. A 6"x4" print costs just £0.50 plus worldwide shipping at £2.99
        regardless of order size. Simple.</p>
        
        <h2>How long will my prints take to arrive?</h2>
        <p>
        <ul>
	        <li>UK: 1-2 days</li>
	        <li>Western Europe: 3-4 days</li>
	        <li>Eastern Europe: 5-6 days</li>
	        <li>Rest of world: 7 days (though major cities are likely to be faster)</li>
        </ul>
        </p>

        <h2>Are prints ordered from OddPrints pre-cut?</h2>
        <p>No, but it's really easy and helps keep the price low.</p>

        <h2>How is OddPrints so fast?</h2>
        <p>When you select an image, it is manipulated entirely within your web browser using javascript.
        You barely need an internet connection at all. Of course, if you choose to order prints with us, then the images
        have to be sent to our servers so that we can print them.</p>
        <h2>Further help</h2>
        <p>Still need help? Just leave a comment below and we will get back to you.</p>
        
        <div id="disqus_thread"></div>
        <script type="text/javascript">
            var disqus_shortname = 'oddprints';
        
            (function() {
                var dsq = document.createElement('script'); dsq.type = 'text/javascript'; dsq.async = true;
                dsq.src = 'http://' + disqus_shortname + '.disqus.com/embed.js';
                (document.getElementsByTagName('head')[0] || document.getElementsByTagName('body')[0]).appendChild(dsq);
            })();
        </script>
        <noscript>Please enable JavaScript to view the <a href="http://disqus.com/?ref_noscript">comments powered by Disqus.</a></noscript>
        <a href="http://disqus.com" class="dsq-brlink">comments powered by <span class="logo-disqus">Disqus</span></a>
    </div>
    
    <jsp:include page="/WEB-INF/jsp/parts/page-footer.jsp" />

</div>

</body>
</html>
