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

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.KeyFactory;
import com.oddprints.Environment;
import com.oddprints.PMF;
import com.oddprints.PrintSize;
import com.oddprints.dao.Basket;
import com.oddprints.util.ImageBlobStore;
import com.sun.jersey.api.client.ClientResponse.Status;
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

        String rawImageData = imageData.replaceFirst("data:image/jpeg;base64,",
                "");

        byte[] bytes = Base64.decode(rawImageData);
        BlobKey blobKey = ImageBlobStore.INSTANCE.writeImageData(bytes);
        PrintSize printSize = PrintSize.toPrintSize(printWidth, printHeight);
        basket.addItem(blobKey, bytes.length, frameSize, printSize);// ,

        pm.makePersistent(basket);
        pm.close();
        return Response.ok().build();
    }

    @POST
    @Path("/original")
    @Produces("text/html")
    public Response doPost(@Context HttpServletRequest req)
            throws FileUploadException, IOException {
        // Get the image representation
        ServletFileUpload upload = new ServletFileUpload();
        FileItemIterator iter = upload.getItemIterator(req);
        FileItemStream imageItem = iter.next();
        InputStream imgStream = imageItem.openStream();

        byte[] bytes = IOUtils.toByteArray(imgStream);
        BlobKey blobKey = ImageBlobStore.INSTANCE.writeImageData(bytes);

        return Response.ok(
                "image should be at: /image/original/" + blobKey.getKeyString()
                        + "/" + bytes.length).build();
    }

}
