package com.jansegre.jwar.webapi;

import io.netty.buffer.ByteBuf;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Handles a server-side channel.
 */
public class ApiSocket extends ChannelInboundHandlerAdapter {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Map<String, Room> roomMap;

    public ApiSocket(Map<String, Room> roomMap) {
        this.roomMap = roomMap;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // Discard the received data silently.
        //((ByteBuf) msg).release();
        log.info("Message received.");
        ctx.write(msg);
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}
