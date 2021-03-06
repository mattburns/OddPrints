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
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
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
import com.google.common.collect.Maps;
import com.oddprints.PMF;
import com.oddprints.dao.ApplicationSetting;
import com.oddprints.dao.ApplicationSetting.Settings;
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

        BlobKey blobKey = ImageBlobStore.INSTANCE.writeImageData(bytes);
        req.getSession().setAttribute("blobKeyString", blobKey.getKeyString());
        req.getSession().setAttribute("blobSize", bytes.length + "");
        req.getSession().setAttribute("basicMode", Boolean.TRUE);

        return viewBasic(req);
    }

    @GET
    @Path("/basic/sample")
    @Produces(MediaType.TEXT_HTML)
    public Viewable loadBasicSample(@Context HttpServletRequest req)
            throws FileUploadException, IOException, URISyntaxException {

        return viewSampleImage(req, Settings.SAMPLE_PHOTO_BLOB_KEY,
                Settings.SAMPLE_PHOTO_BLOB_SIZE, new URL(
                        "http://www.oddprints.com/images/sample.jpg"));

    }

    Viewable viewSampleImage(HttpServletRequest req, Settings blobKeySetting,
            Settings blobSizeSetting, URL image) throws MalformedURLException,
            IOException {
        String blobKeyString = ApplicationSetting.getSetting(blobKeySetting);
        if (blobKeyString == null) {

            InputStream imgStream = image.openStream();

            byte[] bytes = IOUtils.toByteArray(imgStream);

            BlobKey blobKey = ImageBlobStore.INSTANCE.writeImageData(bytes);
            blobKeyString = blobKey.getKeyString();
            ApplicationSetting.putSetting(blobKeySetting, blobKeyString);
            ApplicationSetting.putSetting(blobSizeSetting, "" + bytes.length);
        }
        String blobSize = ApplicationSetting.getSetting(blobSizeSetting);

        req.getSession().setAttribute("blobKeyString", blobKeyString);
        req.getSession().setAttribute("blobSize", blobSize);
        req.getSession().setAttribute("basicMode", Boolean.TRUE);

        return viewBasic(req);
    }
}
