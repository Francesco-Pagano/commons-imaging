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

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ExampleDockerImageHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange t) throws IOException {

        File file = new File("src/main/java/org/apache/commons/imaging/exampleDocker/ExampleDockerImage_withMetadata.jpg");
        t.getResponseHeaders().set("Content-Type", "image/jpg");
        t.sendResponseHeaders(200, file.length());
        OutputStream outputStream=t.getResponseBody();

        FileInputStream fs = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int count;
        while ((count = fs.read(buffer)) != -1) {
            outputStream.write(buffer, 0, count);
        }
        outputStream.close();
        fs.close();
    }
}
