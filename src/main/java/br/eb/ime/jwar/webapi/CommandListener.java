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
import br.eb.ime.jwar.JogoTextual;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.listener.DataListener;

public class CommandListener extends JogoTextual implements DataListener<CommandObject> {

    private SocketIONamespace server;

    public CommandListener(Jogo jogo, SocketIONamespace server) {
        super(jogo);
        this.server = server;
    }

    @Override
    public void onData(SocketIOClient client, CommandObject data, AckRequest ackSender) {
        //TODO: check if sender is the correct player

        System.out.println("command received: " + data.command);
        feedCommand(data.command);

        // update everyone's state
        server.getBroadcastOperations().sendJsonObject(new StateObject(jogo));
    }

    private String out;

    @Override
    public void flush() {
        CommandObject cmd = new CommandObject();
        cmd.output = out;
        server.getBroadcastOperations().sendJsonObject(cmd);
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
