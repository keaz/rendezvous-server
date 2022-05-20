package com.kzone.server;

import com.kzone.client.event.ClientEvent;
import com.kzone.server.handler.ServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.log4j.Log4j2;

/**
 * Hello world!
 */
@Log4j2
public class Server {

    static final int PORT = 8007;

    public static void main(String[] args) throws Exception {
        var bossGroup = new NioEventLoopGroup(1);
        var workerGroup = new NioEventLoopGroup();

        try {
            var bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline p = ch.pipeline();
//                    p.addLast(new LoggingHandler(LogLevel.DEBUG));
                    p.addLast("decoder", new ObjectDecoder(ClassResolvers.cacheDisabled(ClientEvent.class.getClassLoader())));
                    p.addLast("encoder", new ObjectEncoder());
                    p.addLast("handler", new ServerHandler());
                }
            });
            bootstrap.option(ChannelOption.SO_BACKLOG, 128);
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);

            // Start the server.
            var channel = bootstrap.bind(PORT).sync();
            log.debug("Netty Server started.");
            // Wait until the server socket is closed.
            channel.channel().closeFuture().sync();
        } finally {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}

