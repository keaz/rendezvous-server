package com.kzone.server.handler;

import com.kzone.client.Client;
import com.kzone.client.SessionHolder;
import com.kzone.client.event.ClientEvent;
import com.kzone.client.event.ClientInfo;
import com.kzone.client.event.ClientJoined;
import com.kzone.client.event.ClientLeft;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.log4j.Log4j2;

import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@ChannelHandler.Sharable
public class ServerHandler extends SimpleChannelInboundHandler<ClientEvent> {
    static final List<Channel> channels = new ArrayList<>();

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        log.debug("Client joined - {}", ctx);
        channels.add(ctx.channel());
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ClientEvent msg) throws Exception {
        log.debug("Message received: {}", msg);
        if (msg instanceof ClientJoined clientJoined) {
            final var socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
            log.debug("Socket address {}", socketAddress.getAddress().getHostAddress());
            var newClient = new Client(clientJoined.id(), LocalDateTime.now(), ctx.channel(), clientJoined.port());
            SessionHolder.clientSessionHolder().addClient(newClient);
            final var clientInfoList = SessionHolder.clientSessionHolder().getClients().stream()
                    .map(client -> new ClientInfo(client.getClientId(), client.getClientAddress(), client.getPort())).toList();
            newClient.sendClients(clientInfoList);
        }


//        for (var c : channels) {
//            c.writeAndFlush(msg);
//        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Closing connection for client - {}", ctx, cause);
        final var clientTobeRemoved = SessionHolder.clientSessionHolder().getClient(ctx.channel());
        var clientLeft = new ClientLeft(clientTobeRemoved.getClientId());
        SessionHolder.clientSessionHolder().getClients().forEach(client -> client.notifyClient(clientLeft));
        SessionHolder.clientSessionHolder().removeClient(clientTobeRemoved.getChannel());
        ctx.close();
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        log.info("Closing connection for client - {}", ctx);
        final var clientTobeRemoved = SessionHolder.clientSessionHolder().getClient(ctx.channel());
        var clientLeft = new ClientLeft(clientTobeRemoved.getClientId());
        SessionHolder.clientSessionHolder().getClients().forEach(client -> client.notifyClient(clientLeft));
        SessionHolder.clientSessionHolder().removeClient(clientTobeRemoved.getChannel());
        super.channelUnregistered(ctx);
    }
}
