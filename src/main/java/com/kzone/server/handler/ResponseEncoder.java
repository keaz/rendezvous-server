package com.kzone.server.handler;

import com.kzone.client.event.ClientEvent;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.log4j.Log4j2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

@Log4j2
public class ResponseEncoder extends MessageToByteEncoder<ClientEvent> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ClientEvent clientEvent, ByteBuf byteBuf) throws Exception {
        try (var outputStream = new ByteArrayOutputStream(); var oos = new ObjectOutputStream(outputStream)) {
            oos.writeObject(clientEvent);
            var ln = outputStream.toByteArray().length;
            byteBuf.writeBytes(outputStream.toByteArray());
        } catch (IOException exception) {
            log.error("Failed to encode message {}", clientEvent, exception);
        }
    }

}
