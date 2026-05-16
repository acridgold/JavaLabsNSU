package chat.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ServerConfig {

    private int port = 8888;
    private boolean logging = true;
    private int historySize = 10;
    private String keystorePath = "server.jks";
    private String keystorePassword = "00000000";

    public ServerConfig(String configFile) {
        Properties props = new Properties();

        try (InputStream is = ServerConfig.class.getClassLoader().getResourceAsStream(configFile)) {
            if (is == null) {
                System.out.println("Конфиг '" + configFile + "' не найден в ресурсах, использую дефолтные значения.");
            } else {
                props.load(is);
                this.port = Integer.parseInt(props.getProperty("port", "8888"));
                this.logging = Boolean.parseBoolean(props.getProperty("logging", "true"));
                this.historySize = Integer.parseInt(props.getProperty("history_size", "10"));
                this.keystorePath = props.getProperty("keystore_path", "server.jks");
                this.keystorePassword = props.getProperty("keystore_password", "00000000");
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Ошибка при загрузке конфигурации: " + e.getMessage());
        }
    }

    public int getPort() {
        return port;
    }

    public boolean isLogging() {
        return logging;
    }

    public int getHistorySize() {
        return historySize;
    }

    public String getKeystorePath() {
        return keystorePath;
    }

    public String getKeystorePassword() {
        return keystorePassword;
    }
}