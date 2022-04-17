package com.kzone.server.handler;

import com.kzone.p2p.event.Notification;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.log4j.Log4j2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

@Log4j2
public class ResponseEncoder extends MessageToByteEncoder<Notification> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Notification notification, ByteBuf byteBuf) throws Exception {
        try (var outputStream = new ByteArrayOutputStream(); var oos = new ObjectOutputStream(outputStream)) {
            oos.writeObject(notification);
            var ln = outputStream.toByteArray().length;
            byteBuf.writeBytes(outputStream.toByteArray());
        } catch (IOException exception) {
            log.error("Failed to encode message {}", notification, exception);
        }
    }

}
