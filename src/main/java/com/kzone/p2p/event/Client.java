package com.kzone.p2p.event;


import lombok.extern.log4j.Log4j2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.UUID;


@Log4j2
public class Client {

    private static final int CONNECTED = 0;
    private static final int ACTIVE = 1;

    private final UUID clientId;
    private final String clientAddress;
    private String clientName;
    private Date joinedTime;
    private int state = CONNECTED;

    private final SocketChannel channel;

    public Client(UUID clientId, String clientAddress, SocketChannel channel, Date joinedTime) {
        this.clientId = clientId;
        this.clientAddress = clientAddress;
        this.channel = channel;
        this.joinedTime = joinedTime;
    }

    public UUID getClientId() {
        return clientId;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
        this.state = ACTIVE;
    }

    public void notifyClient(Notification clientNotification) {
        log.info("Sending notification {} to client {}", clientNotification, clientId);
        ObjectOutputStream oos = null;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            for (int i = 0; i < 4; i++) {
                outputStream.write(0);
            }
            oos = new ObjectOutputStream(outputStream);
            oos.writeObject(clientNotification);

            final ByteBuffer wrap = ByteBuffer.wrap(outputStream.toByteArray());

            log.debug("Size of the notification message {}", outputStream.size());
            wrap.putInt(0, outputStream.size() - 4);
            channel.write(wrap);
        } catch (IOException ioException) {
            log.error("Failed to notify client {}", clientId);
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    log.error("Failed to close ObjectOutputStream ", e);
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Client{" +
                "clientId=" + clientId +
                ", clientAddress='" + clientAddress + '\'' +
                ", clientName='" + clientName + '\'' +
                ", joinedTime=" + joinedTime +
                ", channel=" + channel +
                '}';
    }

    public boolean isActive(){
        return state == ACTIVE;
    }


}
