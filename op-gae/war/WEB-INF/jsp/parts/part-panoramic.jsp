<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page isELIgnored="false" %>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>

<link href="http://fonts.googleapis.com/css?family=Alfa+Slab+One" rel="stylesheet" type="text/css">

<p>OddPrints now supports printing of panoramic photos.
Prints are offered at <strong>18"×4"</strong>,
the perfect ratio for iPhone photos, at just <strong>${panoPrice}</strong>.</p>

<a href="/upload/basicpano" data-ajax="false">
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
		    <img class="mild-shadow" src="/images/pano.jpg"/>
	    </div>
	</div>
</a>
<p>Of course you can also print panoramas created by any method.
Perhaps you have some taken with an <a href="http://www.youtube.com/watch?v=txwdD11sW1s" data-ajax="false">Android
        smartphone</a>, or maybe you have a panoramic image created with 
        photostitch software.</p>
        
<p>Securely packaged in a tube, OddPrints will ship
your panoramic print <strong>worldwide</strong>.</p>

            
