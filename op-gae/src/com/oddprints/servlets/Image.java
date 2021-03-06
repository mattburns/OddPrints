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
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.KeyFactory;
import com.oddprints.PMF;
import com.oddprints.dao.Basket;
import com.oddprints.dao.BasketItem;
import com.oddprints.util.ImageBlobStore;
import com.sun.jersey.api.client.ClientResponse.Status;

@Path("/image")
public class Image {

    @GET
    @Path("/{secret}/{key}")
    @Produces("image/jpeg")
    public Response getFullImage(@PathParam("secret") String secret,
            @PathParam("key") String key, @Context HttpServletResponse response)
            throws IOException {

        PersistenceManager pm = PMF.get().getPersistenceManager();
        BasketItem item = pm.getObjectById(BasketItem.class,
                KeyFactory.stringToKey(key));

        if (!secret.equalsIgnoreCase(item.getSecret())) {
            return Response.status(Status.UNAUTHORIZED).build();
        }

        return Response.ok(item.getImage().getImageData()).build();
    }

    @GET
    @Path("/original")
    @Produces("image/jpeg")
    public Response getOriginalImage(@Context HttpServletRequest req)
            throws IOException, URISyntaxException {
        String blobKeyString = (String) req.getSession().getAttribute(
                "blobKeyString");
        String blobSizeString = (String) req.getSession().getAttribute(
                "blobSize");
        if (blobKeyString == null || blobSizeString == null) {
            return Response.serverError().build();
        }
        long blobSize = Long.parseLong(blobSizeString);

        byte[] bytes = ImageBlobStore.INSTANCE.readImageData(new BlobKey(
                blobKeyString), blobSize);
        return Response.ok(bytes).build();
    }

    @GET
    @Path("/thumb/{secret}/{key}")
    @Produces("image/jpeg")
    public Response getThumbImage(@PathParam("secret") String secret,
            @PathParam("key") String key) {

        PersistenceManager pm = PMF.get().getPersistenceManager();
        BasketItem item = pm.getObjectById(BasketItem.class,
                KeyFactory.stringToKey(key));

        if (!secret.equalsIgnoreCase(item.getSecret())) {
            return Response.status(Status.UNAUTHORIZED).build();
        }

        return Response.ok(item.getThumbImage().getImageData()).build();
    }

    @GET
    @Path("/basket/{index}")
    @Produces("image/jpeg")
    public Response getBasketImage(@PathParam("index") int index,
            @Context HttpServletRequest req,
            @Context HttpServletResponse response) throws IOException {

        PersistenceManager pm = PMF.get().getPersistenceManager();
        Basket basket = Basket.fromSession(req, pm);

        BasketItem item = basket.getItems().get(index);
        return Response.ok(item.getImage().getImageData()).build();

    }

    @GET
    @Path("/basket/thumb/{index}")
    @Produces("image/jpeg")
    public Response getBasketThumbImage(@PathParam("index") int index,
            @Context HttpServletRequest req) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Basket basket = Basket.fromSession(req, pm);
        BasketItem item = basket.getItems().get(index);
        return Response.ok(item.getThumbImage().getImageData()).build();
    }
}
