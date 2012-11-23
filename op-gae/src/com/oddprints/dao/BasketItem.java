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
package com.oddprints.dao;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.Transform;
import com.oddprints.PrintSize;
import com.oddprints.dao.Basket.State;
import com.oddprints.util.ImageBlobStore;
import com.oddprints.util.StringUtils;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class BasketItem {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key id;

    @Persistent
    private Basket basket;

    @Persistent
    private String secret;

    @Persistent
    private BlobKey blobImage;

    @Persistent
    private long blobSize;

    @Persistent
    private String frameSize;

    @Persistent
    private PrintSize printSize;

    @Persistent
    private int quantity;

    public BasketItem(Basket basket, BlobKey blobImage, long blobSize,
            String frameSize, PrintSize printSize, int quantity) {
        super();
        this.basket = basket;
        this.secret = UUID.randomUUID().toString();
        this.blobImage = blobImage;
        this.blobSize = blobSize;
        this.frameSize = frameSize;
        this.printSize = printSize;
        this.quantity = quantity;
    }

    public Key getId() {
        return id;
    }

    public Basket getBasket() {
        return basket;
    }

    public String getSecret() {
        return secret;
    }

    void deleteBlob() {
        BlobstoreService blobStoreService = BlobstoreServiceFactory
                .getBlobstoreService();
        blobStoreService.delete(blobImage);
    }

    public Image getImage() {
        return ImageBlobStore.INSTANCE.getImage(getBlobImage(), blobSize);
    }

    public Image getThumbImage() {
        ImagesService imagesService = ImagesServiceFactory.getImagesService();
        Transform resize = ImagesServiceFactory.makeResize(100, 100);
        com.google.appengine.api.images.Image newImage = imagesService
                .applyTransform(resize, getImage());

        byte[] imageThumbData = newImage.getImageData();
        return ImagesServiceFactory.makeImage(imageThumbData);
    }

    public BlobKey getBlobImage() {
        return blobImage;
    }

    public long getBlobSize() {
        return blobSize;
    }

    public String getFrameSize() {
        return frameSize;
    }

    public PrintSize getPrintSize() {
        return printSize;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getPriceString() {
        return StringUtils.formatMoney(printSize.getPrice() * quantity);
    }

    public String getUnitPriceStringNoSymbol() {
        return StringUtils.formatMoneyNoSymbol(printSize.getPrice());
    }

    public void setQuantity(int quantity) {
        if (basket.getState() != State.draft) {
            throw new RuntimeException("cant edit draft");
        }
        this.quantity = quantity;
    }

    enum ImageSize {
        full, thumb;
    }

    public URL getFullImageUrl() {
        return getImageUrl(ImageSize.full);
    }

    public URL getThumbImageUrl() {
        return getImageUrl(ImageSize.thumb);
    }

    private URL getImageUrl(ImageSize size) {
        String hostUrl;
        String environment = System
                .getProperty("com.google.appengine.runtime.environment");
        if ("Production".equals(environment)) {
            String applicationId = System
                    .getProperty("com.google.appengine.application.id");
            String version = System
                    .getProperty("com.google.appengine.application.version");
            hostUrl = "http://" + version + "." + applicationId
                    + ".appspot.com";
        } else {
            hostUrl = "http://localhost:8888";
        }

        try {
            return new URL(hostUrl + "/image/"
                    + (size == ImageSize.thumb ? "thumb/" : "") + getSecret()
                    + "/" + KeyFactory.keyToString(id));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return printSize + " print for " + frameSize + " frame. (" + quantity
                + " " + (quantity != 1 ? "copies" : "copy") + " @ "
                + StringUtils.formatMoney(printSize.getPrice()) + " each)";
    }

}
