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
package br.eb.ime.jwar.webapi;

import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.listener.*;

public class ApiServer extends SocketIOServer {

    public ApiServer(Configuration config) {
        super(config);

        // Servir um chat para facilitar a comunicação da galera
        final SocketIONamespace chatServer = this.addNamespace("/chat");
        chatServer.addJsonObjectListener(ChatObject.class, new DataListener<ChatObject>() {
            @Override
            public void onData(SocketIOClient client, ChatObject msg, AckRequest ackRequest) {
                // broadcast messages to all clients
                msg.setType("message");
                chatServer.getBroadcastOperations().sendJsonObject(msg);
            }
        });
        chatServer.addConnectListener(new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient socketIOClient) {
                ChatObject msg = new ChatObject();
                msg.setMessage("Alguém se conectou.");
                msg.setType("connect");
                chatServer.getBroadcastOperations().sendJsonObject(msg);
            }
        });
        chatServer.addDisconnectListener(new DisconnectListener() {
            @Override
            public void onDisconnect(SocketIOClient socketIOClient) {
                ChatObject msg = new ChatObject();
                msg.setMessage("Alguém se desconectou.");
                msg.setType("disconnect");
                chatServer.getBroadcastOperations().sendJsonObject(msg);
            }
        });
    }

    public static ApiServer newConfiguredApiServer() {
        Configuration config = new Configuration();
        //config.setHostname("localhost");
        config.setPort(9092);
        return new ApiServer(config);
    }
}
