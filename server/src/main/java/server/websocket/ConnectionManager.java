package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    // store a map of sessions (key is gameID)
    public final ConcurrentHashMap<Integer, Set<Session>> connections = new ConcurrentHashMap<>();

    public void add(int gameID, Session session) {
        connections.computeIfAbsent(gameID, k -> ConcurrentHashMap.newKeySet());
        connections.get(gameID).add(session);
    }

    public void remove(int gameID, Session session) {
        Set<Session> sessionset = connections.get(gameID);
        if (sessionset != null) {
            sessionset.remove(session);
            if (sessionset.isEmpty()) {
                connections.remove(gameID);
            }
        }
    }

    public void broadcast(Session excludeSession, ServerMessage serverMessage, int gameID) throws IOException {
        Gson Serializer = new Gson();
        String msg = Serializer.toJson(serverMessage);
        Set<Session> sessions = connections.get(gameID);
        for (Session c : sessions) {
            if (c.isOpen()) {
                if (!c.equals(excludeSession)) {
                    c.getRemote().sendString(msg);
                }
            }
        }
    }
}