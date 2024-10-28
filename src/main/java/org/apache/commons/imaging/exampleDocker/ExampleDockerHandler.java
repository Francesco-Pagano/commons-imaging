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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.common.RationalNumber;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.constants.GpsTagConstants;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ExampleDockerHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {

            URL url = new URL("https://thispersondoesnotexist.com/");
            BufferedImage img = ImageIO.read(url);
            File file = new File("src/main/java/org/apache/commons/imaging/exampleDocker/ExampleDockerImage_withoutMetadata.jpg");
            ImageIO.write(img, "jpg", file);

            File file2 = new File("src/main/java/org/apache/commons/imaging/exampleDocker/ExampleDockerImage_withMetadata.jpg");

            ExampleDockerTagHandler setTag = new ExampleDockerTagHandler();
            setTag.setExifGPSTag(file, file2);
            metadataExample(file2);

            String json = metadataExample(file2);
            t.getResponseHeaders().set("Content-Type", "application/json");
            t.sendResponseHeaders(200, json.length());
            OutputStream os = t.getResponseBody();
            os.write(json.getBytes(Charset.forName("UTF-8")));
            os.close();
        }

    public static String metadataExample(final File file) throws IOException {
        final ImageMetadata metadata = Imaging.getMetadata(file);
        String msg = "";

        if (metadata instanceof JpegImageMetadata) {
            final JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;

            System.out.println("file: " + file.getPath());

            printTagValue(jpegMetadata, TiffTagConstants.TIFF_TAG_XRESOLUTION);
            printTagValue(jpegMetadata, TiffTagConstants.TIFF_TAG_DATE_TIME);
            printTagValue(jpegMetadata, ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL);
            printTagValue(jpegMetadata, ExifTagConstants.EXIF_TAG_DATE_TIME_DIGITIZED);
            printTagValue(jpegMetadata, ExifTagConstants.EXIF_TAG_ISO);
            printTagValue(jpegMetadata, ExifTagConstants.EXIF_TAG_SHUTTER_SPEED_VALUE);
            printTagValue(jpegMetadata, ExifTagConstants.EXIF_TAG_APERTURE_VALUE);
            printTagValue(jpegMetadata, ExifTagConstants.EXIF_TAG_BRIGHTNESS_VALUE);
            printTagValue(jpegMetadata, GpsTagConstants.GPS_TAG_GPS_LATITUDE_REF);
            printTagValue(jpegMetadata, GpsTagConstants.GPS_TAG_GPS_LATITUDE);
            printTagValue(jpegMetadata, GpsTagConstants.GPS_TAG_GPS_LONGITUDE_REF);
            printTagValue(jpegMetadata, GpsTagConstants.GPS_TAG_GPS_LONGITUDE);

            System.out.println();

            final TiffImageMetadata exifMetadata = jpegMetadata.getExif();
            if (null != exifMetadata) {
                final TiffImageMetadata.GpsInfo gpsInfo = exifMetadata.getGpsInfo();
                if (null != gpsInfo) {
                    final String gpsDescription = gpsInfo.toString();
                    final double longitude = gpsInfo.getLongitudeAsDegreesEast();
                    final double latitude = gpsInfo.getLatitudeAsDegreesNorth();

                    System.out.println("    " + "GPS Description: " + gpsDescription);
                    System.out.println("    " + "GPS Longitude (Degrees East): " + longitude);
                    System.out.println("    " + "GPS Latitude (Degrees North): " + latitude);
                }
            }

            final TiffField gpsLatitudeRefField = jpegMetadata.findExifValueWithExactMatch(GpsTagConstants.GPS_TAG_GPS_LATITUDE_REF);
            final TiffField gpsLatitudeField = jpegMetadata.findExifValueWithExactMatch(GpsTagConstants.GPS_TAG_GPS_LATITUDE);
            final TiffField gpsLongitudeRefField = jpegMetadata.findExifValueWithExactMatch(GpsTagConstants.GPS_TAG_GPS_LONGITUDE_REF);
            final TiffField gpsLongitudeField = jpegMetadata.findExifValueWithExactMatch(GpsTagConstants.GPS_TAG_GPS_LONGITUDE);
            if (gpsLatitudeRefField != null && gpsLatitudeField != null && gpsLongitudeRefField != null && gpsLongitudeField != null) {

                final String gpsLatitudeRef = (String) gpsLatitudeRefField.getValue();
                final RationalNumber[] gpsLatitude = (RationalNumber[]) gpsLatitudeField.getValue();
                final String gpsLongitudeRef = (String) gpsLongitudeRefField.getValue();
                final RationalNumber[] gpsLongitude = (RationalNumber[]) gpsLongitudeField.getValue();

                final RationalNumber gpsLatitudeDegrees = gpsLatitude[0];
                final RationalNumber gpsLatitudeMinutes = gpsLatitude[1];
                final RationalNumber gpsLatitudeSeconds = gpsLatitude[2];

                final RationalNumber gpsLongitudeDegrees = gpsLongitude[0];
                final RationalNumber gpsLongitudeMinutes = gpsLongitude[1];
                final RationalNumber gpsLongitudeSeconds = gpsLongitude[2];

                msg = "{";
                msg += "\"latitudine\": " + gpsLatitudeDegrees.toDisplayString() + ",";
                msg += "\"longitudine\": " + gpsLongitudeDegrees.toDisplayString();
                msg += "}";

                System.out.println("    " + "GPS Latitude: " + gpsLatitudeDegrees.toDisplayString() + " degrees, " + gpsLatitudeMinutes.toDisplayString()
                        + " minutes, " + gpsLatitudeSeconds.toDisplayString() + " seconds " + gpsLatitudeRef);
                System.out.println("    " + "GPS Longitude: " + gpsLongitudeDegrees.toDisplayString() + " degrees, " + gpsLongitudeMinutes.toDisplayString()
                        + " minutes, " + gpsLongitudeSeconds.toDisplayString() + " seconds " + gpsLongitudeRef);
            }

            System.out.println();

            final List<ImageMetadata.ImageMetadataItem> items = jpegMetadata.getItems();
            for (final ImageMetadata.ImageMetadataItem item : items) {
                System.out.println("    " + "item: " + item);
            }

        }
        return msg;
    }

    private static void printTagValue(final JpegImageMetadata jpegMetadata, final TagInfo tagInfo) {
        final TiffField field = jpegMetadata.findExifValueWithExactMatch(tagInfo);
        if (field == null) {
            System.out.println(tagInfo.name + ": " + "Not Found.");
        } else {
            System.out.println(tagInfo.name + ": " + field.getValueDescription());
        }
    }

}
