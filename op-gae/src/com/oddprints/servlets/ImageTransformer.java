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
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.images.Composite;
import com.google.appengine.api.images.Composite.Anchor;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesService.OutputEncoding;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.OutputSettings;
import com.google.appengine.api.images.Transform;
import com.google.appengine.api.utils.SystemProperty;
import com.google.appengine.api.utils.SystemProperty.Environment.Value;
import com.google.common.collect.Lists;
import com.oddprints.image.TransformSettings;
import com.oddprints.image.TransformSettings.Orientation;
import com.oddprints.image.TransformSettings.Zooming;
import com.oddprints.image.Transformer;
import com.oddprints.util.ImageBlobStore;

@Path("/transformer")
public class ImageTransformer {

    @GET
    @Path("/{dpi}/{frameWidthInInches}/{frameHeightInInches}/{zooming}/{orientation}/{outputEncoding}/{quality}")
    @Produces("image/jpeg")
    public Response get(@PathParam("dpi") int dpi,
            @PathParam("frameWidthInInches") double frameWidthInInches,
            @PathParam("frameHeightInInches") double frameHeightInInches,
            @PathParam("zooming") Zooming zooming,
            @PathParam("orientation") Orientation orientation,
            @PathParam("outputEncoding") OutputEncoding outputEncoding,
            @PathParam("quality") int quality, @Context HttpServletRequest req)
            throws IOException {

        String blobKeyString = (String) req.getSession().getAttribute(
                "blobKeyString");
        String blobSizeString = (String) req.getSession().getAttribute(
                "blobSize");
        if (blobKeyString == null || blobSizeString == null) {
            return Response.serverError().build();
        }
        long blobSize = Long.parseLong(blobSizeString);

        Image finalImage = generateOddPrint(blobKeyString, blobSize, dpi,
                frameWidthInInches, frameHeightInInches, zooming, orientation,
                outputEncoding, quality);
        return Response.ok(finalImage.getImageData()).build();
    }

    Image generateOddPrint(String blobKeyString, long blobSize, int dpi,
            double frameWidthInInches, double frameHeightInInches,
            Zooming zooming, Orientation orientation,
            OutputEncoding outputEncoding, int quality) {

        if (SystemProperty.environment.value() == Value.Development) {
            outputEncoding = OutputEncoding.PNG;
        }

        Image image = ImageBlobStore.INSTANCE.getImage(new BlobKey(
                blobKeyString), blobSize);

        ImagesService is = ImagesServiceFactory.getImagesService();

        Transformer t = new Transformer();
        TransformSettings settings = t.calculateSettings(image, dpi,
                frameWidthInInches, frameHeightInInches, zooming, orientation);

        if (zooming == Zooming.CROP) {
            double xTrim = (double) settings.getSourceX() / image.getWidth();
            double yTrim = (double) settings.getSourceY() / image.getHeight();
            Transform crop = ImagesServiceFactory.makeCrop(xTrim, yTrim,
                    1 - xTrim, 1 - yTrim);
            image = is.applyTransform(crop, image);
        }

        Transform resize = ImagesServiceFactory
                .makeResize(settings.getDestinationWidth(),
                        settings.getDestinationHeight());
        Image shrunkImage = is.applyTransform(resize, image);

        Composite composite = ImagesServiceFactory.makeComposite(shrunkImage,
                settings.getDestinationX(), settings.getDestinationY(), 1f,
                Anchor.TOP_LEFT);

        List<Composite> composites = Lists.newArrayList(composite);

        OutputSettings outputSettings = new OutputSettings(outputEncoding);
        outputSettings.setQuality(quality);
        Image finalImage = is.composite(composites, settings.getCanvasWidth(),
                settings.getCanvasHeight(), 0xffddddddL, outputSettings);
        return finalImage;
    }
}
