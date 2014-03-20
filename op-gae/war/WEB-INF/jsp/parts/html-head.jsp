<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page isELIgnored="false" %>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>

<c:set var="cache_version" scope="application" value='11'/>

<head>
    <title>OddPrints${param.titleText}</title>
    <meta content="text/html; charset=UTF-8" http-equiv="content-type" />
    <meta property="og:title" content="OddPrints"/>
    <meta property="og:type" content="website"/>
    <meta property="og:url" content="http://www.oddprints.com"/>
    <meta property="og:image" content="http://www.oddprints.com/images/sticker.png"/>
    <meta property="og:site_name" content="OddPrints"/>
    <meta property="fb:admins" content="565140267, 565740296"/>
    <meta property="og:description" content="${param.descriptionText}"/>
          
    <meta name="Description" content="${param.descriptionText}" />
          
    <meta name="viewport" content="width=device-width, initial-scale=1.0"> 
    <link type="text/css" rel="stylesheet" href="/css/jquery.mobile-1.2.0.min.css" />
    <link type="text/css" rel="stylesheet" href="/css/oddprints.css?v=${cache_version}" />
    <link type="text/css" rel="stylesheet" href="/css/jquery.miniColors.css" />
    
    <script type="text/javascript" src="/js/jquery-1.8.2.min.js"></script>
    <script type="text/javascript">
        $(document).bind("mobileinit", function () {
            $.mobile.ajaxEnabled = false;
        });
    </script>
    <script type="text/javascript" src="/js/jquery.mobile-1.2.0.min.js"></script>
    <script type="text/javascript" src="/js/jquery-ui-1.9.0.custom.min.js"></script>
    <script type="text/javascript" src="/js/modernizr.custom.11873.js"></script>
    <script type="text/javascript" src="/js/oddprints.js?v=${cache_version}"></script>
    <script type="text/javascript" src="/js/jquery.tinycarousel.min.js"></script>
    <script type="text/javascript" src="/js/jquery.miniColors.min.js"></script>
    <script async="" defer="" src="//survey.g.doubleclick.net/async_survey?site=yz6olxu3jv6xa"></script>
    <script>
     // Google Analytics:
     (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
         (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
         m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
         })(window,document,'script','//www.google-analytics.com/analytics.js','ga');
    
         ga('create', 'UA-32205253-1', 'oddprints.com');
         ga('send', 'pageview');
    </script>
    <meta name="p:domain_verify" content="2d52ecbc5247658b783bb8db091edfbf"/>
</head>

<c:set var="html_year" scope="application" value='<%= new java.text.SimpleDateFormat("yyyy").format(new java.util.Date()) %>'/>
