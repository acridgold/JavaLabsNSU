package chat.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserRegistry {

    private final Map<String, ClientSession> sessions = new ConcurrentHashMap<>();

    public void add(ClientSession session) {
        sessions.put(session.getSessionId(), session);
    }

    public void remove(String sessionId) {
        sessions.remove(sessionId);
    }

    public ClientSession findBySession(String sessionId) {
        return sessions.get(sessionId);
    }

    public boolean isNameTaken(String name) {
        return sessions.values().stream().anyMatch(s -> s.getName().equals(name));
    }

    public List<ClientSession> getAll() {
        return new ArrayList<>(sessions.values());
    }
}
