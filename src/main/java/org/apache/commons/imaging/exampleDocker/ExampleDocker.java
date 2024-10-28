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

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class ExampleDocker {
    public static void main( String[] args ) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", new MyHandler());
        server.createContext("/metadata", new ExampleDockerHandler());
        server.createContext("/image", new ExampleDockerImageHandler());
        server.setExecutor(null);
        server.start();
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "  <head>\n" +
                    "    <meta charset=\"UTF-8\" />\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n" +
                    "    <title>Metadata visualizer</title>\n" +
                    "    <style>\n" +
                    "      :root {\n" +
                    "        font-family: Inter, system-ui, Avenir, Helvetica, Arial, sans-serif;\n" +
                    "        line-height: 1.5;\n" +
                    "        font-weight: 400;\n" +
                    "        font-synthesis: none;\n" +
                    "        text-rendering: optimizeLegibility;\n" +
                    "        -webkit-font-smoothing: antialiased;\n" +
                    "        -moz-osx-font-smoothing: grayscale;\n" +
                    "      }\n" +
                    "      * {\n" +
                    "        margin: 0;\n" +
                    "        padding: 0;\n" +
                    "      }\n" +
                    "      body {\n" +
                    "        height: 100vh;\n" +
                    "        display: flex;\n" +
                    "        align-items: center;\n" +
                    "        justify-content: center;\n" +
                    "        background-color: #fdf6ff;\n" +
                    "      }\n" +
                    "      #wrapper {\n" +
                    "        min-width: 80%;\n" +
                    "        display: flex;\n" +
                    "        flex-direction: column;\n" +
                    "        gap: 40px;\n" +
                    "      }\n" +
                    "      #title-container {\n" +
                    "        display: flex;\n" +
                    "        flex-direction: column;\n" +
                    "        align-self: center;\n" +
                    "        margin-bottom: 3.25rem;\n" +
                    "        text-align: center;\n" +
                    "      }\n" +
                    "      h1 {\n" +
                    "        text-align: center;\n" +
                    "      }\n" +
                    "      h3 {\n" +
                    "        text-align: center;\n" +
                    "        font-weight: 200;\n" +
                    "      }\n" +
                    "      #data-container {\n" +
                    "        min-width: 80%;\n" +
                    "        display: flex;\n" +
                    "        flex-direction: row;\n" +
                    "        justify-content: center;\n" +
                    "        gap: 20px;\n" +
                    "      }\n" +
                    "      #metadata-container {\n" +
                    "        border: 1px solid #d8d6db;\n" +
                    "        border-radius: 8px;\n" +
                    "        padding: 20px 20px;\n" +
                    "        min-width: 360px;\n" +
                    "        display: flex;\n" +
                    "        flex-direction: column;\n" +
                    "        gap: 4px;\n" +
                    "      }\n" +
                    "      #image-container {\n" +
                    "        width: 400px;\n" +
                    "        height: 400px;\n" +
                    "        border-radius: 8px;\n" +
                    "        border: 1px solid #d8d6db;\n" +
                    "        border-radius: 8px;\n" +
                    "      }\n" +
                    "      button {\n" +
                    "        align-self: center;\n" +
                    "        padding: 12px 24px;\n" +
                    "        border: 1px solid #004db9;\n" +
                    "        border-radius: 8px;\n" +
                    "        background-color: #15198a;\n" +
                    "        font-weight: 500;\n" +
                    "        color: white;\n" +
                    "        cursor: pointer;\n" +
                    "      }\n" +
                    "      button:hover {\n" +
                    "        transition: all 0.2s;\n" +
                    "        background-color: #004db9;\n" +
                    "      }\n" +
                    "      img {\n" +
                    "        width: 100%;\n" +
                    "        height: 100%;\n" +
                    "        object-fit: fill;\n" +
                    "      }\n" +
                    "      span {\n" +
                    "        font-weight: 600;\n" +
                    "      }\n" +
                    "    </style>\n" +
                    "  </head>\n" +
                    "  <body>\n" +
                    "    <div id=\"wrapper\">\n" +
                    "      <div id=\"title-container\">\n" +
                    "        <h1>Image metadata visualizer</h1>\n" +
                    "        <h3>With Apache Commons Imaging</h3>\n" +
                    "      </div>\n" +
                    "      <div id=\"data-container\">\n" +
                    "        <img id=\"image-container\" src=\"https://placehold.co/400x400?text=placeholder\" />\n" +
                    "        <div id=\"metadata-container\"></div>\n" +
                    "      </div>\n" +
                    "      <button id=\"get-image-button\">Get image</button>\n" +
                    "    </div>\n" +
                    "  </body>\n" +
                    "\n" +
                    "  <script>\n" +
                    "    document.getElementById(\"get-image-button\").addEventListener(\"click\", async () => {\n" +
                    "      try {\n" +
                    "        const response = await fetch(\"http://localhost:8080/metadata\");\n" +
                    "        if (!response.ok) {\n" +
                    "          throw new Error(`Response status: ${response.status}`);\n" +
                    "        }\n" +
                    "        const json = await response.json();\n" +
                    "\n" +
                    "        const metadataContainer = document.getElementById(\"metadata-container\");\n" +
                    "        metadataContainer.innerHTML = \"\";\n" +
                    "\n" +
                    "        for (const m in json) {\n" +
                    "          const value = json[m];\n" +
                    "          const mContainer = document.createElement(\"div\");\n" +
                    "          mContainer.innerHTML = `<span>${m}:</span> ${value}`;\n" +
                    "          metadataContainer.appendChild(mContainer);\n" +
                    "        }\n" +
                    "      } catch (error) {\n" +
                    "        console.log(error);\n" +
                    "      }\n" +
                    "      try {\n" +
                    "        const response = await fetch(\"http://localhost:8080/image\");\n" +
                    "        if (!response.ok) {\n" +
                    "          throw new Error(`Response status: ${response.status}`);\n" +
                    "        }\n" +
                    "        const blob = await response.blob();\n" +
                    "        const imageUrl = URL.createObjectURL(blob);\n" +
                    "\n" +
                    "        document.getElementById(\"image-container\").src = imageUrl;\n" +
                    "      } catch (error) {\n" +
                    "        console.log(error);\n" +
                    "      }\n" +
                    "    });\n" +
                    "  </script>\n" +
                    "</html>";

            t.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes(Charset.forName("UTF-8")));

            os.close();
        }
    }

}
