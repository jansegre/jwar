package br.eb.ime.jwar;

import com.corundumstudio.socketio.listener.*;
import com.corundumstudio.socketio.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebApplication {

    static class ChatObject {

        private String userName;
        private String message;

        public ChatObject() {
        }

        public ChatObject(String userName, String message) {
            super();
            this.userName = userName;
            this.message = message;
        }

        public String getUserName() {
            return userName;
        }
        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getMessage() {
            return message;
        }
        public void setMessage(String message) {
            this.message = message;
        }

    }

    public static void main(String[] args) throws InterruptedException {
        final Logger logger = LoggerFactory.getLogger(WebApplication.class);
        logger.info("Hello World!!");

        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(9092);

        final SocketIOServer server = new SocketIOServer(config);

        /*server.addEventListener("chatevent", ChatObject.class, new DataListener<ChatObject>() {
            @Override
            public void onData(SocketIOClient client, ChatObject data, AckRequest ackRequest) {
                logger.info(data.getMessage());
                server.getBroadcastOperations().sendEvent("chatevent", data);
            }
        });*/

        final SocketIONamespace chat1namespace = server.addNamespace("/chat1");
        chat1namespace.addJsonObjectListener(ChatObject.class, new DataListener<ChatObject>() {
            @Override
            public void onData(SocketIOClient client, ChatObject data, AckRequest ackRequest) {
                logger.info(data.getMessage());
                // broadcast messages to all clients
                chat1namespace.getBroadcastOperations().sendJsonObject(data);
            }
        });

        final SocketIONamespace chat2namespace = server.addNamespace("/chat2");

        chat2namespace.addJsonObjectListener(ChatObject.class, new DataListener<ChatObject>() {
            @Override
            public void onData(SocketIOClient client, ChatObject data, AckRequest ackRequest) {
                logger.info(data.getMessage());
                // broadcast messages to all clients
                chat2namespace.getBroadcastOperations().sendJsonObject(data);
            }
        });

        /*server.addMessageListener(new DataListener<String>() {
            @Override
            public void onData(SocketIOClient socketIOClient, String s, AckRequest ackRequest) {
                logger.info(s);
            }
        });*/

        server.start();

        Thread.sleep(Integer.MAX_VALUE);

        server.stop();
    }

}


