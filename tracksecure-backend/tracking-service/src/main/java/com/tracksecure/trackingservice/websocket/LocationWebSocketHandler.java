package com.tracksecure.trackingservice.websocket;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracksecure.trackingservice.dto.LocationResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@Component
@RequiredArgsConstructor
public class LocationWebSocketHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper;
    private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private final Map<String, CopyOnWriteArraySet<WebSocketSession>> deviceSubscriptions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        log.info("WebSocket connection established: {}", session.getId());

        // Send welcome message
        Map<String, Object> welcome = Map.of(
                "type", "connected",
                "message", "Connected to location tracking service",
                "sessionId", session.getId()
        );
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(welcome)));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            String payload = message.getPayload();
            log.debug("Received WebSocket message: {}", payload);

            // Parse subscription message
            @SuppressWarnings("unchecked")
            Map<String, Object> msg = objectMapper.readValue(payload, Map.class);

            String action = (String) msg.get("action");
            String deviceId = (String) msg.get("deviceId");

            if ("subscribe".equals(action) && deviceId != null) {
                subscribeToDevice(session, deviceId);
            } else if ("unsubscribe".equals(action) && deviceId != null) {
                unsubscribeFromDevice(session, deviceId);
            }

        } catch (Exception e) {
            log.error("Error handling WebSocket message: {}", e.getMessage(), e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);

        // Remove from all device subscriptions
        deviceSubscriptions.values().forEach(subs -> subs.remove(session));

        log.info("WebSocket connection closed: {} - Status: {}", session.getId(), status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket transport error: {}", exception.getMessage(), exception);
        session.close(CloseStatus.SERVER_ERROR);
    }


    private void subscribeToDevice(WebSocketSession session, String deviceId) throws IOException {
        deviceSubscriptions.computeIfAbsent(deviceId, k -> new CopyOnWriteArraySet<>())
                .add(session);

        log.info("Session {} subscribed to device: {}", session.getId(), deviceId);

        Map<String, Object> response = Map.of(
                "type", "subscribed",
                "deviceId", deviceId,
                "message", "Subscribed to device updates"
        );
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
    }

    private void unsubscribeFromDevice(WebSocketSession session, String deviceId) throws IOException {
        CopyOnWriteArraySet<WebSocketSession> subs = deviceSubscriptions.get(deviceId);
        if (subs != null) {
            subs.remove(session);
            log.info("Session {} unsubscribed from device: {}", session.getId(), deviceId);
        }

        Map<String, Object> response = Map.of(
                "type", "unsubscribed",
                "deviceId", deviceId,
                "message", "Unsubscribed from device updates"
        );
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
    }

    public void broadcastLocationUpdate(LocationResponseDTO location) {
        String deviceId = location.getDeviceId();

        try {
            Map<String, Object> update = Map.of(
                    "type", "location_update",
                    "data", location
            );
            String message = objectMapper.writeValueAsString(update);
            TextMessage textMessage = new TextMessage(message);

            // Send to device-specific subscribers
            CopyOnWriteArraySet<WebSocketSession> deviceSubs = deviceSubscriptions.get(deviceId);
            if (deviceSubs != null) {
                deviceSubs.forEach(session -> {
                    if (session.isOpen()) {
                        try {
                            session.sendMessage(textMessage);
                        } catch (IOException e) {
                            log.error("Failed to send message to session: {}", session.getId(), e);
                        }
                    }
                });
            }

            // Also broadcast to all connected clients
            sessions.forEach(session -> {
                if (session.isOpen() && !deviceSubs.contains(session)) {
                    try {
                        session.sendMessage(textMessage);
                    } catch (IOException e) {
                        log.error("Failed to broadcast message to session: {}", session.getId(), e);
                    }
                }
            });

        } catch (Exception e) {
            log.error("Error broadcasting location update: {}", e.getMessage(), e);
        }
    }

}
