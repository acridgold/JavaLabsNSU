package chat.server;

import java.io.OutputStream;
import java.util.UUID;

public class ClientSession {
    private final String sessionId;
    private final String name;
    private final String clientType;
    private OutputStream out;

    public ClientSession(String name, String clientType) {
        this.sessionId = UUID.randomUUID().toString();
        this.name = name;
        this.clientType = clientType;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getName() {
        return name;
    }

    public String getClientType() {
        return clientType;
    }

    public OutputStream getOut() {
        return out;
    }

    public void setOut(OutputStream out) {
        this.out = out;
    }
}