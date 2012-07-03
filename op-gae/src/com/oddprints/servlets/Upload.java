/*******************************************************************************
 * Copyright 2011 Matt Burns
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.oddprints.servlets;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService.OutputEncoding;
import com.oddprints.Environment;
import com.oddprints.PMF;
import com.oddprints.PrintSize;
import com.oddprints.dao.Basket;
import com.oddprints.image.TransformSettings.Orientation;
import com.oddprints.image.TransformSettings.Zooming;
import com.oddprints.util.ImageBlobStore;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.view.Viewable;
import com.sun.jersey.core.util.Base64;

@Path("/upload")
public class Upload {

    @POST
    public Response post(@FormParam("imageData") String imageData,
            @FormParam("frameSize") String frameSize,
            @FormParam("printWidth") int printWidth,
            @FormParam("printHeight") int printHeight,
            @Context HttpServletRequest req) throws IOException,
            URISyntaxException {

        String rawImageData = imageData.replaceFirst("data:image/jpeg;base64,",
                "");

        byte[] bytes = Base64.decode(rawImageData);
        BlobKey blobKey = ImageBlobStore.INSTANCE.writeImageData(bytes);

        return addToBasket(frameSize, printWidth, printHeight, req, blobKey,
                bytes.length);
    }

    private Response addToBasket(String frameSize, int printWidth,
            int printHeight, HttpServletRequest req, BlobKey blobKey,
            long blobSize) {
        PersistenceManager pm = PMF.get().getPersistenceManager();

        Basket basket = Basket.fromSession(req, pm);
        if (basket == null) {
            Environment env = null;
            try {
                env = Environment.getDefault();
            } catch (NullPointerException npe) {
                return Response.status(Status.INTERNAL_SERVER_ERROR).build();
            }
            basket = new Basket(env);
            pm.makePersistent(basket);
            String basketKeyString = KeyFactory.keyToString(basket.getId());
            req.getSession().setAttribute("basketKeyString", basketKeyString);
        }

        PrintSize printSize = PrintSize.toPrintSize(printWidth, printHeight);
        basket.addItem(blobKey, blobSize, frameSize, printSize);// ,

        pm.makePersistent(basket);
        pm.close();
        return Response.ok().build();
    }

    @GET
    @Path("/basic")
    public Viewable viewBasic(@Context HttpServletRequest req) {
        req.getSession().setAttribute("basicMode", Boolean.TRUE);
        return new Viewable("/upload-basic");
    }

    @POST
    @Path("/basic")
    public Response postBasic(@FormParam("dpi") int dpi,
            @FormParam("frameWidthInInches") double frameWidthInInches,
            @FormParam("frameHeightInInches") double frameHeightInInches,
            @FormParam("zooming") Zooming zooming,
            @FormParam("orientation") Orientation orientation,
            @FormParam("outputEncoding") OutputEncoding outputEncoding,
            @FormParam("quality") int quality,
            @FormParam("frameSize") String frameSize,
            @FormParam("printWidth") int printWidth,
            @FormParam("printHeight") int printHeight,
            @Context HttpServletRequest req,
            @Context HttpServletResponse response) throws IOException {

        String blobKeyString = (String) req.getSession().getAttribute(
                "blobKeyString");
        long blobSize = Long.parseLong((String) req.getSession().getAttribute(
                "blobSize"));
        ImageTransformer it = new ImageTransformer();
        Image image = it.generateOddPrint(blobKeyString, blobSize, dpi,
                frameWidthInInches, frameHeightInInches, zooming, orientation,
                outputEncoding, quality);

        byte[] bytes = image.getImageData();
        BlobKey oddPrintBlobKey = ImageBlobStore.INSTANCE.writeImageData(bytes);
        req.getSession().setAttribute("basicMode", Boolean.TRUE);
        return addToBasket(frameSize, printWidth, printHeight, req,
                oddPrintBlobKey, bytes.length);
    }
}
