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
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ApiServer extends SocketIOServer {

    private final Map<String, Room> roomMap;

    private final Logger log = LoggerFactory.getLogger(getClass());

    class RoomList {
        Collection<String> rooms;

        RoomList(Collection<String> rooms) {
            this.rooms = rooms;
        }
    }

    public ApiServer(Configuration configuration) {
        super(configuration);
        super.setPipelineFactory(new ApiInitializer());
        roomMap = new HashMap<>();

        // Criar um jogo, por enquanto igual ao em Application
        //jogo = new Jogo(Arrays.asList(Cor.AZUL, Cor.VERMELHO, Cor.AMARELO, Cor.PRETO, Cor.VERDE, Cor.BRANCO), new RiskSecretMission());

        final SocketIOServer self = this;

        // Servir um chat para facilitar a comunicação da galera
        final SocketIONamespace chatServer = this.addNamespace("/chat");
        chatServer.addJsonObjectListener(ChatObject.class, new DataListener<ChatObject>() {
            @Override
            public void onData(SocketIOClient client, ChatObject msg, AckRequest ackRequest) {
                switch (msg.type) {
                    case JOIN:
                        client.joinRoom(msg.room);
                        self.getRoomOperations(msg.room).sendJsonObject(msg);
                        break;
                    case LEAVE:
                        client.leaveRoom(msg.room);
                        self.getRoomOperations(msg.room).sendJsonObject(msg);
                        break;
                    case MESSAGE:
                        // broadcast messages to all clients on the same room
                        self.getRoomOperations(msg.room).sendJsonObject(msg);
                        break;
                }
                log.info(msg.toString());
            }
        });

        self.addConnectListener(new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient client) {
                log.info(client.getRemoteAddress() + " connected");
                //ChatObject msg = new ChatObject();
                //msg.message = "Alguém se conectou.";
                //msg.type = "connect";
                //chatServer.getBroadcastOperations().sendJsonObject(msg);
            }
        });
        self.addDisconnectListener(new DisconnectListener() {
            @Override
            public void onDisconnect(SocketIOClient client) {
                log.info(client.getRemoteAddress() + " disconnected");
                //ChatObject msg = new ChatObject();
                //msg.message = "Alguém se desconectou.";
                //msg.type = "disconnect";
                //chatServer.getBroadcastOperations().sendJsonObject(msg);
            }
        });

        // Servidor da api em si
        final SocketIONamespace apiServer = this.addNamespace("/api");
        apiServer.addConnectListener(new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient client) {
                // Send the room list
                client.sendJsonObject(roomMap.keySet());
                //client.sendJsonObject(new StateObject(jogo, true));
            }
        });
        apiServer.addJsonObjectListener(CommandObject.class, new RoomManager(roomMap, self));

        //this.get
    }
}
