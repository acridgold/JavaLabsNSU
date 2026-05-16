package chat.serialized;

import chat.common.ChatMessage;
import chat.server.ClientSession;
import chat.server.ServerLogger;
import chat.server.UserRegistry;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandlerSerialize implements Runnable {

    private final Socket socket;
    private final UserRegistry registry;
    private final ChatServerSerialize server;
    private final ServerLogger logger;

    private ObjectOutputStream out;
    private ClientSession session;

    public ClientHandlerSerialize(Socket socket, UserRegistry registry,
                                  ChatServerSerialize server, ServerLogger logger) {
        this.socket = socket;
        this.registry = registry;
        this.server = server;
        this.logger = logger;
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            while (!socket.isClosed()) {
                ChatMessage cmd = SerializeProtocol.receive(in);
                handleCommand(cmd);
            }
        } catch (IOException e) {
            logger.log("Клиент отключился: " +
                    (session != null ? session.getName() : socket.getInetAddress()));
        } finally {
            disconnect();
        }
    }

    private void handleCommand(ChatMessage cmd) throws IOException {
        logger.log("<- " + cmd);
        switch (cmd.getType()) {
            case "login" -> handleLogin(cmd);
            case "list" -> handleList(cmd);
            case "message" -> handleMessage(cmd);
            case "logout" -> handleLogout(cmd);
            default -> sendError("Неизвестная команда: " + cmd.getType());
        }
    }

    private void handleLogin(ChatMessage cmd) throws IOException {
        String name = cmd.getName();

        if (name == null || name.isBlank()) {
            sendError("Имя не может быть пустым");
            return;
        }
        if (registry.isNameTaken(name)) {
            sendError("Имя уже занято: " + name);
            return;
        }

        String clientType = cmd.getMessage() != null ? cmd.getMessage() : "Unknown";
        session = new ClientSession(name, clientType);
        session.setOut(out);
        registry.add(session);
        logger.log("Вошёл: " + name + " (" + clientType + ")");

        for (ChatMessage h : server.getHistory()) {
            SerializeProtocol.send(out, h);
        }

        ChatMessage success = new ChatMessage("success");
        success.setSession(session.getSessionId());
        SerializeProtocol.send(out, success);

        ChatMessage event = new ChatMessage("event");
        event.setEvent("userlogin");
        event.setName(name);
        server.broadcast(event, session.getSessionId());
    }

    private void handleList(ChatMessage cmd) throws IOException {
        if (checkAuth(cmd)) return;

        StringBuilder sb = new StringBuilder();
        for (ClientSession s : registry.getAll()) {
            sb.append(s.getName()).append(",");
        }

        ChatMessage success = new ChatMessage("success");
        success.setMessage(sb.toString());
        SerializeProtocol.send(out, success);
    }

    private void handleMessage(ChatMessage cmd) throws IOException {
        if (checkAuth(cmd)) return;

        String text = cmd.getMessage();
        if (text == null || text.isBlank()) {
            sendError("Пустое сообщение");
            return;
        }

        ChatMessage event = new ChatMessage("event");
        event.setEvent("message");
        event.setName(session.getName());
        event.setMessage(text);

        server.addToHistory(event);
        server.broadcast(event, null);

        SerializeProtocol.send(out, new ChatMessage("success"));
        logger.log(session.getName() + ": " + text);
    }

    private void handleLogout(ChatMessage cmd) throws IOException {
        if (checkAuth(cmd)) return;
        SerializeProtocol.send(out, new ChatMessage("success"));
        disconnect();
    }

    private boolean checkAuth(ChatMessage cmd) throws IOException {
        if (session == null) {
            sendError("Не авторизован");
            return true;
        }
        if (!session.getSessionId().equals(cmd.getSession())) {
            sendError("Неверный session id");
            return true;
        }
        return false;
    }

    private void sendError(String reason) throws IOException {
        ChatMessage error = new ChatMessage("error");
        error.setMessage(reason);
        SerializeProtocol.send(out, error);
    }

    private void disconnect() {
        if (session != null) {
            String name = session.getName();
            registry.remove(session.getSessionId());

            ChatMessage event = new ChatMessage("event");
            event.setEvent("userlogout");
            event.setName(name);
            try {
                server.broadcast(event, null);
            } catch (IOException ignored) {
            }

            logger.log("Вышел: " + name);
            session = null;
        }
        try {
            socket.close();
        } catch (IOException ignored) {
        }
    }
}
