package com.kzone.client.event;

public record ClientJoined(String id, int port) implements ClientEvent {
}
