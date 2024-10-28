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
package org.apache.commons.imaging.exampleDocker;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;

public class ExampleDockerTagHandler {

    public void setExifGPSTag(final File jpegImageFile, final File dst) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(dst);
             OutputStream os = new BufferedOutputStream(fos)) {
            TiffOutputSet outputSet = null;

            final ImageMetadata metadata = Imaging.getMetadata(jpegImageFile);
            final JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
            if (null != jpegMetadata) {
                final TiffImageMetadata exif = jpegMetadata.getExif();

                if (null != exif) {
                    outputSet = exif.getOutputSet();
                }
            }

            if (null == outputSet) {
                outputSet = new TiffOutputSet();
            }

            outputSet.setGpsInDegrees(-74.0, 40 + 43 / 60.0);

            new ExifRewriter().updateExifMetadataLossless(jpegImageFile, os, outputSet);
        }
    }
}
