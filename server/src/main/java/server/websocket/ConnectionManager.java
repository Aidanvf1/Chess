package server.websocket;

import com.google.gson.Gson;
import io.javalin.websocket.WsContext;
import websocket.messages.ServerMessage;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private final Gson gson = new Gson();
    private final Map<Integer, Set<WsContext>> gameConnections = new ConcurrentHashMap<>();

    public void add(int gameID, WsContext ctx) {
        gameConnections.computeIfAbsent(gameID, id -> ConcurrentHashMap.newKeySet()).add(ctx);
    }

    public void remove(WsContext ctx) {
        for (Set<WsContext> connections : gameConnections.values()) {
            connections.remove(ctx);
        }
    }

    public void send(WsContext ctx, ServerMessage message) {
        ctx.send(gson.toJson(message));
    }

    public void broadcastToAll(int gameID, ServerMessage message) {
        for (WsContext ctx : connectionsIn(gameID)) {
            sendOrDrop(ctx, message);
        }
    }

    public void broadcastToOthers(int gameID, ServerMessage message, WsContext excluded) {
        for (WsContext ctx : connectionsIn(gameID)) {
            if (!ctx.equals(excluded)) {
                sendOrDrop(ctx, message);
            }
        }
    }

    private Set<WsContext> connectionsIn(int gameID) {
        return gameConnections.getOrDefault(gameID, Set.of());
    }

    private void sendOrDrop(WsContext ctx, ServerMessage message) {
        try {
            send(ctx, message);
        } catch (RuntimeException ex) {
            remove(ctx);
        }
    }
}