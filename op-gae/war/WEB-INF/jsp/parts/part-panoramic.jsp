<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page isELIgnored="false" %>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>

<link href="http://fonts.googleapis.com/css?family=Alfa+Slab+One" rel="stylesheet" type="text/css">

<p>OddPrints now supports printing of panoramic photos.
We offer printing at <strong>18"×4"</strong>,
the perfect ratio for iPhone photos, for just <strong>${panoPrice}</strong>.</p>

<div id="pano-container">
    <div id="pano-sticker">
        <div class="stickercontainer">
            <div class="stickercrop"><div class="sticker"></div></div>
            <div class="foldshadowcrop"><div class="foldshadow"></div></div>
            <div class="foldcrop"><div class="fold"></div></div>
            <div class="sticker-text">${panoPriceSticker}</div>
        </div>
    </div>
    <div id="pano-text">← 18" →</div>
    <div id="pano-img">
	    <img style="box-shadow: 0px 0px 15px #222222;" src="/images/pano.jpg"/>
    </div>
</div>
<p>Of course you can also print panoramas taken with an Android
        smartphone, or perhaps you have a panoramic image created with 
        photostitch software.</p>
        
<p>Securely packaged in a tube, OddPrints will ship
your panoramic print worldwide.</p>

            
