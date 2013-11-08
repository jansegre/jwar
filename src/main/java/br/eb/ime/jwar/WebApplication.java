/*
 * This file is part of JWar.
 *
 * JWar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.
 *
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