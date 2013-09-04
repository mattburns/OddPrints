<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page isELIgnored="false" %>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>

<c:set var="cache_version" scope="application" value='4'/>

<head>
    <title>OddPrints${param.titleText}</title>
    <meta content="text/html; charset=UTF-8" http-equiv="content-type" />
    <meta property="og:title" content="OddPrints"/>
    <meta property="og:type" content="website"/>
    <meta property="og:url" content="http://www.oddprints.com"/>
    <meta property="og:image" content="http://www.oddprints.com/images/icon128.png"/>
    <meta property="og:site_name" content="OddPrints"/>
    <meta property="fb:admins" content="565140267, 565740296"/>
    <meta property="og:description" content="${param.descriptionText}"/>
          
    <meta name="Description" content="${param.descriptionText}" />
          
    <meta name="viewport" content="width=device-width, initial-scale=1"> 
    <link type="text/css" rel="stylesheet" href="/css/jquery.mobile-1.2.0.min.css" />
    <link type="text/css" rel="stylesheet" href="/css/oddprints.css?v=${cache_version}" />
    <link type="text/css" rel="stylesheet" href="/css/jquery.miniColors.css" />

</head>

<c:set var="html_year" scope="application" value='<%= new java.text.SimpleDateFormat("yyyy").format(new java.util.Date()) %>'/>
