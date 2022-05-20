package com.kzone.client;

import io.netty.channel.Channel;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SessionHolder {

    private static final SessionHolder SINGLETON = new SessionHolder();
    private final Map<Channel, Client> clientMap = new HashMap<>();

    public static SessionHolder clientSessionHolder() {
        return SINGLETON;
    }

    private SessionHolder() {
    }

    public void addClient(Client client) {
        clientMap.put(client.getChannel(), client);
    }


    public Client getClient(Channel channel) {
        return clientMap.get(channel);
    }

    public Collection<Client> getClients() {
        return clientMap.values();
    }

    public void removeClient(Channel channel) {
        clientMap.remove(channel);
    }

    public int getActiveClientCount() {
        return (int) getClients().stream().filter(Client::isActive).count();
    }

}
