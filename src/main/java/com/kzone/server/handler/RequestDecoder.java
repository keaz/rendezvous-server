package com.kzone.server.handler;

import com.kzone.p2p.event.ClientLeft;
import com.kzone.p2p.event.Notification;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import lombok.extern.log4j.Log4j2;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.List;

@Log4j2
public class RequestDecoder extends ReplayingDecoder<Notification> {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> list) throws Exception {

        byte[] lengthBytes = new byte[4];
        in.readBytes(lengthBytes);
        var dataLength = ByteBuffer.wrap(lengthBytes).getInt();
        if (dataLength > 0) {
            final var data = in.readBytes(dataLength);
            final var ois = new ObjectInputStream(new ByteArrayInputStream(data.array()));
            final var not = (Serializable) ois.readObject();
            log.debug("Got Notification {}", not);
            if (not instanceof List notifications) {

                log.info("Client notification {}", notifications);
                list.addAll(notifications);
                return;
            }

            list.add(not);
            if (not instanceof Notification notification) {
                log.info("Got new client join event {}", notification.clientId());
            }

            if (not instanceof ClientLeft removed) {
                log.info("Client removed {}", removed.clientId());
            }
        }

    }
}
