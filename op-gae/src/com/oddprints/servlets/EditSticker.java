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

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.fileupload.FileUploadException;

import com.sun.jersey.api.view.Viewable;

@Path("/editsticker")
public class EditSticker {

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Viewable view(@Context HttpServletRequest req) {
        Edit edit = new Edit();
        req.setAttribute("stickerMode", Boolean.TRUE);
        // forward to normal servlet
        return edit.view(req);
    }

    @GET
    @Path("/basic")
    @Produces(MediaType.TEXT_HTML)
    public Viewable viewBasic(@Context HttpServletRequest req) {
        Edit edit = new Edit();
        req.setAttribute("stickerMode", Boolean.TRUE);
        // forward to normal servlet
        return edit.viewBasic(req);
    }

    @POST
    @Path("/basic")
    @Produces(MediaType.TEXT_HTML)
    public Viewable doPost(@Context HttpServletRequest req)
            throws FileUploadException, IOException, URISyntaxException {
        Edit edit = new Edit();
        req.setAttribute("stickerMode", Boolean.TRUE);
        // forward to normal servlet
        return edit.doPost(req);
    }

    @GET
    @Path("/basic/sample")
    @Produces(MediaType.TEXT_HTML)
    public Viewable loadBasicSample(@Context HttpServletRequest req)
            throws FileUploadException, IOException, URISyntaxException {
        Edit edit = new Edit();
        req.setAttribute("stickerMode", Boolean.TRUE);
        // forward to normal servlet
        return edit.loadBasicSample(req);
    }
}
