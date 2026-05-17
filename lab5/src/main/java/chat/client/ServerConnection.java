package chat.client;

import chat.common.ChatMessage;
import chat.common.JsonProtocol;

import javax.net.ssl.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.function.Consumer;

public class ServerConnection {

    private SSLSocket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String sessionId;

    private volatile boolean closing = false;

    private Consumer<ChatMessage> onMessage;
    private Consumer<String> onError;

    public void setOnMessage(Consumer<ChatMessage> h) {
        this.onMessage = h;
    }

    public void setOnError(Consumer<String> h) {
        this.onError = h;
    }

    public boolean connect(String host, int port, String name, String clientType) {
        try {
            socket = createSSLSocket(host, port);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            ChatMessage login = new ChatMessage("login");
            login.setName(name);
            login.setMessage(clientType);
            JsonProtocol.send(out, login);

            while (true) {
                ChatMessage resp = JsonProtocol.receive(in);
                if ("success".equals(resp.getType())) {
                    sessionId = resp.getSession();
                    break;
                } else if ("error".equals(resp.getType())) {
                    notifyError(resp.getMessage());
                    socket.close();
                    return false;
                } else {
                    notifyMessage(resp);
                }
            }

            startReaderThread();
            return true;

        } catch (Exception e) {
            notifyError("Ошибка подключения: " + e.getMessage());
            return false;
        }
    }

    // Принимаем любой сертификат
    private SSLSocket createSSLSocket(String host, int port) throws Exception {
        TrustManager[] trustAll = new TrustManager[]{
                new X509TrustManager() {
                    public void checkClientTrusted(X509Certificate[] c, String a) {
                    }

                    public void checkServerTrusted(X509Certificate[] c, String a) {
                    }

                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
        };

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAll, null);

        SSLSocketFactory factory = sslContext.getSocketFactory();
        return (SSLSocket) factory.createSocket(host, port);
    }

    public void sendMessage(String text) {
        ChatMessage cmd = new ChatMessage("message");
        cmd.setMessage(text);
        cmd.setSession(sessionId);
        send(cmd);
    }

    public void requestUserList() {
        ChatMessage cmd = new ChatMessage("list");
        cmd.setSession(sessionId);
        send(cmd);
    }

    public void logout() {
        closing = true;
        ChatMessage cmd = new ChatMessage("logout");
        cmd.setSession(sessionId);
        send(cmd);
        try {
            if (socket != null){ socket.close();
                System.err.println("\n[SYSTEM] Сокет клиента успешно закрыт...");
            }
        } catch (IOException ignored) {}
    }

    private void startReaderThread() {
        Thread reader = new Thread(() -> {
            try {
                while (!socket.isClosed()) {
                    notifyMessage(JsonProtocol.receive(in));
                }
            } catch (IOException e) {
                if (!closing) notifyError("Соединение потеряно");
                try {
                    socket.close();
                } catch (IOException ignored) {}

            }
        });
        reader.setDaemon(true);
        reader.start();
    }

    private void send(ChatMessage cmd) {
        try {
            JsonProtocol.send(out, cmd);
        } catch (IOException e) {
            notifyError("Ошибка отправки: " + e.getMessage());
        }
    }

    private void notifyMessage(ChatMessage msg) {
        if (onMessage != null) onMessage.accept(msg);
    }

    private void notifyError(String err) {
        if (onError != null) onError.accept(err);
    }
}