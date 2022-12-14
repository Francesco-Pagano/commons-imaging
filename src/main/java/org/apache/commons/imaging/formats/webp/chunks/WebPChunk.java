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
package org.apache.commons.imaging.formats.webp.chunks;

import org.apache.commons.imaging.ImagingException;
import org.apache.commons.imaging.common.BinaryFileParser;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

/**
 * A WebP image is composed of several chunks. This is the base class for the chunks,
 * used by the parser.
 *
 * @see <a href="https://developers.google.com/speed/webp/docs/riff_container">WebP Container Specification</a>
 *
 * @since 1.0-alpha4
 */
public abstract class WebPChunk extends BinaryFileParser {
    private final int type;
    private final int size;
    protected final byte[] bytes;

    WebPChunk(int type, int size, byte[] bytes) throws ImagingException {
        super(ByteOrder.LITTLE_ENDIAN);

        if (size != bytes.length) {
            throw new IllegalArgumentException("Chunk size must match bytes length");
        }

        this.type = type;
        this.size = size;
        this.bytes = bytes;
    }

    public int getType() {
        return type;
    }

    public String getTypeDescription() {
        return new String(new byte[]{
                (byte) (type & 0xff),
                (byte) ((type >> 8) & 0xff),
                (byte) ((type >> 16) & 0xff),
                (byte) ((type >> 24) & 0xff)}, StandardCharsets.UTF_8);
    }

    public int getPayloadSize() {
        return size;
    }

    public int getChunkSize() {
        // if chunk size is odd, a single padding byte is added
        int padding = (size % 2) != 0 ? 1 : 0;

        // Chunk FourCC (4 bytes) + Chunk Size (4 bytes) + Chunk Payload (n bytes) + Padding
        return 4 + 4 + size + padding;
    }

    public byte[] getBytes() {
        return bytes.clone();
    }

    public void dump(PrintWriter pw, int offset) throws ImagingException, IOException {
        pw.printf("Chunk %s at offset %s, length %d%n", getTypeDescription(), offset >= 0 ? String.valueOf(offset) : "unknown", getChunkSize());
    }
}
