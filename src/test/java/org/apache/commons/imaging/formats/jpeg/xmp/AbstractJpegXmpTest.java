/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.imaging.formats.jpeg.xmp;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.imaging.AbstractImagingTest;

import org.apache.commons.imaging.bytesource.ByteSource;
import org.apache.commons.imaging.formats.jpeg.JpegImageParser;

public abstract class AbstractJpegXmpTest extends AbstractImagingTest {

    private static final ImageFilter HAS_JPEG_XMP_IMAGE_FILTER = AbstractJpegXmpTest::hasJpegXmpData;

    protected static List<File> getImagesWithXmpData() throws IOException {
        return getTestImages(HAS_JPEG_XMP_IMAGE_FILTER);
    }

    protected static boolean hasJpegXmpData(final File file) {
        if (!file.getName().toLowerCase().endsWith(".jpg")) {
            return false;
        }

        try {
            final ByteSource byteSource = ByteSource.file(file);
            return new JpegImageParser().hasXmpSegment(byteSource);
        } catch (final Exception e) {
            return false;
        }
    }

    protected List<File> getImagesWithXmpData(final int max) throws IOException {
        return getTestImages(HAS_JPEG_XMP_IMAGE_FILTER, max);
    }

    protected File getImageWithXmpData() throws IOException {
        return getTestImage(HAS_JPEG_XMP_IMAGE_FILTER);
    }
}
