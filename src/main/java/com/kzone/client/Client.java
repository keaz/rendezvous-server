package com.kzone.client;


import com.kzone.client.event.ClientEvent;
import com.kzone.client.event.ClientInfo;
import io.netty.channel.Channel;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Log4j2
public class Client implements Serializable {

    private static final int CONNECTED = 0;
    private static final int ACTIVE = 1;

    private final String clientId;
    private final LocalDateTime joinedTime;
    private final int port;
    private int state = CONNECTED;

    private final Channel channel;
    private final InetSocketAddress inetSocketAddress;

    public Client(String clientId, LocalDateTime joinedTime, Channel channel, int port) {
        this.clientId = clientId;
        this.joinedTime = joinedTime;
        this.channel = channel;
        this.inetSocketAddress = (InetSocketAddress)channel.remoteAddress();
        this.port = port;
    }

    public String getClientId() {
        return clientId;
    }

    public int getPort() {
        return port;
    }

    public String getClientAddress() {
        return inetSocketAddress.getAddress().getHostAddress();
    }


    public Channel getChannel() {
        return channel;
    }

    public void notifyClient(ClientEvent clientClientEvent) {
        log.info("Sending notification {} to client {}", clientClientEvent, clientId);
        channel.writeAndFlush(clientClientEvent);
    }

    public void sendClients(List<ClientInfo> clients) {
        log.info("Sending already connected client {}  to client {}", clients, clientId);
        channel.writeAndFlush(clients);
    }

    @Override
    public String toString() {
        return "Client{" +
                "clientId='" + clientId + '\'' +
                ", joinedTime=" + joinedTime +
                ", port=" + port +
                ", state=" + state +
                ", channel=" + channel +
                ", inetSocketAddress=" + inetSocketAddress +
                '}';
    }

    public boolean isActive() {
        return state == ACTIVE;
    }

}
