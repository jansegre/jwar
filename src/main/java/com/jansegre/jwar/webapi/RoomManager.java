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

package com.jansegre.jwar.webapi;

import com.jansegre.jwar.Jogo;
import com.jansegre.jwar.JogoTextual;
import com.jansegre.jwar.models.Cor;
import com.jansegre.jwar.models.templates.RiskSecretMission;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;

import java.util.Map;

public class RoomManager extends JogoTextual implements DataListener<CommandObject> {

    private SocketIOServer server;
    Map<String, Room> roomMap;

    public RoomManager(Map<String, Room> roomMap, SocketIOServer server) {
        this.roomMap = roomMap;
        this.server = server;
        this.out = "";
    }

    @Override
    public void onData(SocketIOClient client, CommandObject cmd, AckRequest ackSender) {
        switch (cmd.type) {
            case CREATE:
                String roomName = Long.toHexString(Double.doubleToLongBits(Math.random()));
                createRoom(roomName);
                cmd.room = roomName;
                client.sendJsonObject(cmd);
                break;

            case JOIN:
                if (!roomMap.containsKey(cmd.room)) {
                    respond(client, "Jogo não encontrado.");
                    break;
                }

                loadRoom(cmd.room);
                // welcome packet
                client.sendJsonObject(new StateObject(jogo, true));
                client.joinRoom(cmd.room);
                break;

            case COMMAND:
                if (!roomMap.containsKey(cmd.room)) {
                    respond(client, "Jogo não encontrado.");
                    break;
                }

                loadRoom(cmd.room);
                System.out.println("command received on room " + cmd.room + ": " + cmd.command);

                feedCommand(cmd.command);
                flushToRoom(cmd.room);

                // update everyone's state
                server.getRoomOperations(cmd.room).sendJsonObject(new StateObject(jogo));
                break;
        }
    }

    private void createRoom(String roomName) {
        Room room = new Room();
        room.jogo = new Jogo(new RiskSecretMission(), Cor.values());
        roomMap.put(roomName, room);
    }

    private void respond(SocketIOClient client, String response) {
        CommandObject resp = new CommandObject();
        resp.output = response;
        client.sendJsonObject(resp);
    }

    private void loadRoom(String roomName) {
        Room room = roomMap.get(roomName);
        if (room.jogo == null)
            createRoom(roomName);
        jogo = room.jogo;
    }

    private String out;

    @Override
    public void flush() {
        //CommandObject cmd = new CommandObject();
        //cmd.output = out;
        //server.getBroadcastOperations().sendJsonObject(cmd);
        //out = "";
    }

    public void flushToRoom(String roomName) {
        CommandObject cmd = new CommandObject();
        cmd.output = out;
        server.getRoomOperations(roomName).sendJsonObject(cmd);
        out = "";
    }

    public void flushToClient(SocketIOClient client) {
        CommandObject cmd = new CommandObject();
        cmd.output = out;
        client.sendJsonObject(cmd);
        out = "";
    }

    @Override
    public void error(String out) {
        if (out != null)
            this.out += out + "\n";
    }

    @Override
    public void output(String out) {
        if (out != null)
            this.out += out + "\n";
    }
}
