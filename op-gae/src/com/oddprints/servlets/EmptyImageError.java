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

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.oddprints.PMF;
import com.oddprints.dao.Basket;
import com.oddprints.dao.BasketItem;
import com.oddprints.util.EmailSender;
import com.sun.jersey.api.view.Viewable;

@Path("/empty-image-error")
public class EmptyImageError {

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Viewable get(@Context HttpServletRequest req,
            @QueryParam("agent") String agent) {
        PersistenceManager pm = PMF.get().getPersistenceManager();

        Basket basket = Basket.fromSession(req, pm);

        if (basket != null) {
            String message = "EIE: ";
            for (BasketItem item : basket.getItems()) {
                message += item.getBlobSize() + ", ";
                // if (item.getBlobSize() == 0) {
                // basket.delete(item);
                // }
            }
            EmailSender.INSTANCE.sendToAdmin(message, "Empty Image Error - "
                    + agent);
        }

        return new Viewable("/empty-image-error");
    }
}
