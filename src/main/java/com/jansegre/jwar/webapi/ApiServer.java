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

import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;

public class ApiServer extends SocketIOServer {

    private final RoomManager roomManager;
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ApiSocket apiSocket;
    private final int apiPort = 4242;

    public ApiServer(Configuration configuration) {
        this(configuration, true);
    }

    public ApiServer(Configuration configuration, boolean useSocketApi) {
        super(configuration);
        super.setPipelineFactory(new ApiInitializer());
        roomManager = new RoomManager(new HashMap<String, Room>(), this);

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
                client.sendJsonObject(roomManager.roomMap.keySet());
                //client.sendJsonObject(new StateObject(jogo, true));
            }
        });
        apiServer.addJsonObjectListener(CommandObject.class, roomManager);

        if (useSocketApi) {
            apiSocket = new ApiSocket(roomManager);
        } else {
            apiSocket = null;
        }
    }

    @Override
    public void start() {
        // Reference: http://netty.io/wiki/user-guide-for-4.x.html
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();

                            // Add the text line codec combination first,
                            pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
                            // the encoder and decoder are static as these are sharable
                            pipeline.addLast("decoder", new StringDecoder());
                            pipeline.addLast("encoder", new StringEncoder());

                            // and then business logic.
                            pipeline.addLast("handler", apiSocket);

                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(apiPort).syncUninterruptibly();
            log.info("ApiSocket started at port: {}", apiPort);

            // Also start parent
            super.start();

            // Wait until the server socket is closed.
            f.channel().closeFuture().syncUninterruptibly();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
