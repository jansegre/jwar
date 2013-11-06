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

import br.eb.ime.jwar.Jogo;
import br.eb.ime.jwar.models.Cor;
import br.eb.ime.jwar.models.templates.RiskSecretMission;
import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;

import java.util.Arrays;

public class ApiServer extends SocketIOServer {

    //TODO: permitir multiplos jogos simultâneos
    Jogo jogo;

    public ApiServer(Configuration config) {
        super(config);

        // Criar um jogo, por enquanto igual ao em Application
        jogo = new Jogo(Arrays.asList(Cor.AZUL, Cor.VERMELHO, Cor.AMARELO, Cor.PRETO), new RiskSecretMission());

        // Servir um chat para facilitar a comunicação da galera
        final SocketIONamespace chatServer = this.addNamespace("/chat");
        chatServer.addJsonObjectListener(ChatObject.class, new DataListener<ChatObject>() {
            @Override
            public void onData(SocketIOClient client, ChatObject msg, AckRequest ackRequest) {
                // broadcast messages to all clients
                msg.type = "message";
                chatServer.getBroadcastOperations().sendJsonObject(msg);
            }
        });
        chatServer.addConnectListener(new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient socketIOClient) {
                ChatObject msg = new ChatObject();
                msg.message = "Alguém se conectou.";
                msg.type = "connect";
                chatServer.getBroadcastOperations().sendJsonObject(msg);
            }
        });
        chatServer.addDisconnectListener(new DisconnectListener() {
            @Override
            public void onDisconnect(SocketIOClient socketIOClient) {
                ChatObject msg = new ChatObject();
                msg.message = "Alguém se desconectou.";
                msg.type = "disconnect";
                chatServer.getBroadcastOperations().sendJsonObject(msg);
            }
        });

        // Servidor da api em si
        final SocketIONamespace apiServer = this.addNamespace("/api");
        apiServer.addConnectListener(new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient client) {
                client.sendJsonObject(new StateObject(jogo, true));
            }
        });
        apiServer.addJsonObjectListener(CommandObject.class, new CommandListener(jogo, apiServer));
    }

    public static ApiServer newConfiguredApiServer() {
        Configuration config = new Configuration();
        //config.setHostname("localhost");
        config.setPort(9092);
        return new ApiServer(config);
    }
}
