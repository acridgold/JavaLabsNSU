package chat.serialized;

import chat.common.ChatMessage;
import chat.server.ClientSession;
import chat.server.ServerConfig;
import chat.server.ServerLogger;
import chat.server.UserRegistry;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ChatServerSerialize {

    private static final List<Thread> clientThreads = new ArrayList<>();
    private volatile ServerSocket serverSocket;
    private final ServerConfig config;
    private final UserRegistry registry;
    private final ServerLogger logger;

    private final LinkedList<ChatMessage> history = new LinkedList<>();

    public ChatServerSerialize(ServerConfig config) {
        this.config = config;
        this.registry = new UserRegistry();
        this.logger = new ServerLogger(config.isLogging());
    }

    public void start() throws Exception {
        try (ServerSocket socket = createSSLServerSocket()) {
            this.serverSocket = socket;
            logger.log("TLS-сервер (сериализация) запущен на порту " + config.getPort());

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    var client = serverSocket.accept();
                    new Thread(new ClientHandlerSerialize(client, registry, this, logger)).start();
                } catch (IOException e) {
                    if (serverSocket.isClosed()) {
                        logger.log("Сервер успешно остановлен.");
                        break;
                    }
                    throw e;
                }
            }
        }
    }

    public void stop() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            logger.log("Ошибка при закрытии: " + e.getMessage());
        }
    }

    private ServerSocket createSSLServerSocket() throws Exception {
        KeyStore ks = KeyStore.getInstance("JKS");

        try (InputStream is = ChatServerSerialize.class.getClassLoader().getResourceAsStream(config.getKeystorePath())) {
            if (is == null) {
                try (FileInputStream fis = new FileInputStream(config.getKeystorePath())) {
                    ks.load(fis, config.getKeystorePassword().toCharArray());
                }
            } else {
                ks.load(is, config.getKeystorePassword().toCharArray());
            }
        }

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks, config.getKeystorePassword().toCharArray());

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), null, null);

        SSLServerSocketFactory factory = sslContext.getServerSocketFactory();
        return factory.createServerSocket(config.getPort());
    }

    public synchronized void broadcast(ChatMessage msg, String exclude) throws IOException {
        for (ClientSession s : registry.getAll()) {
            if (s.getSessionId().equals(exclude)) continue;
            if (s.getOut() == null) continue;
            try {
                logger.log("-> " + msg);
                SerializeProtocol.send((ObjectOutputStream) s.getOut(), msg);
            } catch (IOException e) {
                logger.log("Ошибка рассылки: " + s.getName());
            }
        }
    }

    public synchronized void addToHistory(ChatMessage msg) {
        history.addLast(msg);
        while (history.size() > config.getHistorySize()) history.removeFirst();
    }

    public synchronized List<ChatMessage> getHistory() {
        return new ArrayList<>(history);
    }

    public static void main(String[] args) {
        String configFile = args.length > 0 ? args[0] : "server.properties";
        ServerConfig config = new ServerConfig(configFile);
        ChatServerSerialize server = new ChatServerSerialize(config);

        Thread mainThread = Thread.currentThread();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("\n[SYSTEM] Остановка...");
            server.stop();
            mainThread.interrupt();
        }));

        try {
            server.start();
        } catch (Exception e) {
            System.err.println("Ошибка запуска: " + e.getMessage());
            e.printStackTrace();
        }
    }
}