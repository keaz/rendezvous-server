package com.kzone.p2p.event;

import java.util.UUID;

public record ClientUpdatedNotification(UUID clientId, String clientName, String status) implements Notification {

}
