/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package br.eb.ime.jwar;

import br.eb.ime.jwar.webapi.ApiServer;
import com.corundumstudio.socketio.Configuration;

public class WebApplication {

    public static final int defaultPort = 8080;

    public static void main(String[] args) throws Exception {
        int port;

        // Get the port
        String envPort = System.getenv("PORT");
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else if (envPort != null) {
            port = Integer.valueOf(envPort);
        } else {
            port = defaultPort;
        }

        // run the server forever
        Configuration config = new Configuration();
        config.setPort(port);
        ApiServer apiServer = new ApiServer(config);

        try {
            apiServer.start();
            Thread.sleep(Integer.MAX_VALUE);
        } finally {
            apiServer.stop();
        }
    }
}