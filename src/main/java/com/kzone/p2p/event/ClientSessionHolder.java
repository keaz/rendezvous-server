package com.kzone.p2p.event;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class ClientSessionHolder {

    private static final ClientSessionHolder SINGLETON = new ClientSessionHolder();
    private final Map<UUID, Client> clientMap = new HashMap<>();

    public static ClientSessionHolder clientSessionHolder() {
        return SINGLETON;
    }

    private ClientSessionHolder() {
    }

    public void addAnonymousClient(Client client) {
        clientMap.put(client.getClientId(), client);
    }

    public void updateClientInfo(UUID clientId, ClientUpdatedNotification clientInfoRequest) {
        final var client = clientMap.get(clientId);
        client.setClientName(clientInfoRequest.clientName());
    }

    public Client getClient(UUID uuid) {
        return clientMap.get(uuid);
    }

    public Collection<Client> getClients() {
        return clientMap.values();
    }

    public void removeClient(UUID uuid) {
        clientMap.remove(uuid);
    }

    public int getActiveClientCount() {
        return (int) getClients().stream().filter(Client::isActive).count();
    }

}
