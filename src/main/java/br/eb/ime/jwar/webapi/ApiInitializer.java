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

import com.corundumstudio.socketio.SocketIOChannelInitializer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;

public class ApiInitializer extends SocketIOChannelInitializer {
    public static final String HTTP_STATIC_HANDLER = "staticHandler";

    @Override
    protected void initChannel(Channel ch) throws Exception {
        super.initChannel(ch);
        ChannelPipeline pipeline = ch.pipeline();
        //pipeline.remove(RESOURCE_HANDLER);
        pipeline.addAfter(HTTP_ENCODER, HTTP_STATIC_HANDLER, new FileServerHandler());
    }
}
