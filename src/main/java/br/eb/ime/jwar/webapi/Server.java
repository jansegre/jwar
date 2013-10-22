package br.eb.ime.jwar.webapi;

import com.corundumstudio.socketio.*;
//import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.*;

public class Server {
    public static void main(String[] args) throws InterruptedException {

        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(9092);

        final SocketIOServer server = new SocketIOServer(config);

        // Servir um chat para facilitar a comunicação da galera
        final SocketIONamespace chatServer = server.addNamespace("/chat");
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

        server.start();
        Thread.sleep(Integer.MAX_VALUE);
        server.stop();
    }
}
