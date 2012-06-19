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
package com.oddprints.util;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.commons.lang3.ArrayUtils;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesServiceFactory;

public enum ImageBlobStore {

    INSTANCE;

    public byte[] readImageData(BlobKey blobKey, long blobSize) {
        BlobstoreService blobStoreService = BlobstoreServiceFactory
                .getBlobstoreService();
        byte[] allTheBytes = new byte[0];
        long amountLeftToRead = blobSize;
        long startIndex = 0;
        while (amountLeftToRead > 0) {
            long amountToReadNow = Math.min(
                    BlobstoreService.MAX_BLOB_FETCH_SIZE - 1, amountLeftToRead);

            byte[] chunkOfBytes = blobStoreService.fetchData(blobKey,
                    startIndex, startIndex + amountToReadNow);

            allTheBytes = ArrayUtils.addAll(allTheBytes, chunkOfBytes);

            amountLeftToRead -= amountToReadNow;
            startIndex += amountToReadNow;
        }

        return allTheBytes;
    }

    public BlobKey writeImageData(byte[] bytes) throws IOException {
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

    public Image getImage(BlobKey blobKey, long blobSize) {
        return ImagesServiceFactory.makeImage(readImageData(blobKey, blobSize));
    }
}
