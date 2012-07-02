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
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.Transform;
import com.google.common.collect.Maps;
import com.oddprints.PMF;
import com.oddprints.dao.Basket;
import com.oddprints.util.ImageBlobStore;
import com.sun.jersey.api.view.Viewable;

@Path("/edit")
public class Edit {

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Viewable view(@Context HttpServletRequest req) {
        PersistenceManager pm = PMF.get().getPersistenceManager();

        Basket basket = Basket.fromSession(req, pm);

        Map<String, Object> it = Maps.newHashMap();
        it.put("basket", basket);

        req.getSession().setAttribute("basicMode", Boolean.FALSE);
        return new Viewable("/edit", it);
    }

    @GET
    @Path("/basic")
    @Produces(MediaType.TEXT_HTML)
    public Viewable viewBasic(@Context HttpServletRequest req) {
        PersistenceManager pm = PMF.get().getPersistenceManager();

        Basket basket = Basket.fromSession(req, pm);

        Map<String, Object> it = Maps.newHashMap();
        it.put("basket", basket);

        String blobSizeString = (String) req.getSession().getAttribute(
                "blobSize");
        if (blobSizeString == null) {
            return new Error().get("No image found");
        }

        req.getSession().setAttribute("basicMode", Boolean.TRUE);
        return new Viewable("/edit-basic", it);
    }

    @POST
    @Path("/basic")
    @Produces(MediaType.TEXT_HTML)
    public Viewable doPost(@Context HttpServletRequest req)
            throws FileUploadException, IOException, URISyntaxException {
        // Get the image representation
        ServletFileUpload upload = new ServletFileUpload();
        FileItemIterator iter = upload.getItemIterator(req);
        FileItemStream imageItem = iter.next();
        InputStream imgStream = imageItem.openStream();

        byte[] bytes = IOUtils.toByteArray(imgStream);

        // bytes = shrink(bytes);
        BlobKey blobKey = ImageBlobStore.INSTANCE.writeImageData(bytes);

        req.getSession().setAttribute("blobKeyString", blobKey.getKeyString());
        req.getSession().setAttribute("blobSize", bytes.length + "");
        req.getSession().setAttribute("basicMode", Boolean.TRUE);

        return viewBasic(req);
    }

    // FIXME: investigate usefulness of shrinking (effect on quality?
    // On production only top part of image remains. why?
    private byte[] shrink(byte[] fileData) {
        Image image = ImagesServiceFactory.makeImage(fileData);
        if (image.getWidth() > 3000 || image.getHeight() > 3000) {
            Transform resize = ImagesServiceFactory.makeResize(3000, 3000);
            image = ImagesServiceFactory.getImagesService().applyTransform(
                    resize, image);

        }

        return image.getImageData();
    }

}
