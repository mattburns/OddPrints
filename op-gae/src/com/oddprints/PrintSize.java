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
package com.oddprints;

import java.util.List;

import uk.co.mattburns.pwinty.Photo;

import com.google.common.collect.Lists;

public enum PrintSize {
    _2x4(50), _4x6(50), _5x7(140), _8x10(300), _8x12(300), _4x18(400);

    private final int height;
    private final int width;
    private final int price;
    private final Photo.Type pwintyType;

    private PrintSize(int price) {
        // Parse the name of this enum to get dimensions
        String[] heightAndWidth = this.name().replaceFirst("_", "").split("x");

        height = Integer.parseInt(heightAndWidth[0]);
        width = Integer.parseInt(heightAndWidth[1]);
        this.price = price;
        if (this.name().equals("_2x4")) {
            pwintyType = Photo.Type.sticker;
        } else {
            // Enum name must match pwinty enum name
            pwintyType = Photo.Type.valueOf(this.name());
        }
    }

    public static List<PrintSize> printableSizes() {
        List<PrintSize> list = Lists.newArrayList(values());
        list.remove(_2x4);
        return list;
    }

    public static PrintSize toPrintSize(int width, int height) {
        for (PrintSize ps : values()) {
            if ((width == ps.width && height == ps.height)
                    || (height == ps.width && width == ps.height)) {
                return ps;
            }
        }
        return null;
    }

    public int getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return height + "\"×" + width + "\"";
    }

    public String getDisplayString() {
        return toString();
    }

    public Photo.Type toPwintyType() {
        return pwintyType;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
