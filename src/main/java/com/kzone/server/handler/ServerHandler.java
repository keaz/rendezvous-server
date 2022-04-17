package com.kzone.server.handler;

import com.kzone.p2p.event.Notification;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@ChannelHandler.Sharable
public class ServerHandler extends SimpleChannelInboundHandler<Notification> {
    static final List<Channel> channels = new ArrayList<>();

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        log.debug("Client joined - {}", ctx);
        channels.add(ctx.channel());
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Notification msg) throws Exception {
        log.debug("Message received: {}", msg);

        for (var c : channels) {
            c.writeAndFlush(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Closing connection for client - {}", ctx, cause);
        ctx.close();
    }
}
