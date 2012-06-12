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
import java.nio.ByteBuffer;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;
import com.oddprints.Environment;
import com.oddprints.PMF;
import com.oddprints.PrintSize;
import com.oddprints.dao.Basket;
import com.sun.jersey.core.util.Base64;

@Path("/upload")
public class Upload {

    @POST
    public Response view(@FormParam("imageData") String imageData,
            @FormParam("frameSize") String frameSize,
            @FormParam("printWidth") int printWidth,
            @FormParam("printHeight") int printHeight,
            @Context HttpServletRequest req) throws IOException {

        PersistenceManager pm = PMF.get().getPersistenceManager();

        Basket basket = Basket.fromSession(req, pm);
        if (basket == null) {
            basket = new Basket(Environment.getDefault());
            pm.makePersistent(basket);
            String basketKeyString = KeyFactory.keyToString(basket.getId());
            req.getSession().setAttribute("basketKeyString", basketKeyString);
        }

        String rawImageData = imageData.replaceFirst("data:image/jpeg;base64,",
                "");

        byte[] image = Base64.decode(rawImageData);
        BlobKey blobKey = writeToBlobstore(image);
        PrintSize printSize = PrintSize.toPrintSize(printWidth, printHeight);
        basket.addItem(blobKey, image.length, frameSize, printSize);// ,

        pm.makePersistent(basket);
        pm.close();
        return Response.ok().build();
    }

    private BlobKey writeToBlobstore(byte[] bytes) throws IOException {
        FileService fileService = FileServiceFactory.getFileService();

        AppEngineFile file = fileService.createNewBlobFile("image/jpeg");
        // This time lock because we intend to finalize
        boolean lock = true;
        FileWriteChannel writeChannel = fileService
                .openWriteChannel(file, lock);

        writeChannel.write(ByteBuffer.wrap(bytes));

        // Now finalize
        writeChannel.closeFinally();

        return fileService.getBlobKey(file);
    }
}
