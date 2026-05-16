package chat.server;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ServerLogger {

    private final boolean enabled;
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final String logFilePath;
    private PrintWriter fileWriter;

    public ServerLogger(boolean enabled) {
        this(enabled, "server.log");
    }

    public ServerLogger(boolean enabled, String logFilePath) {
        this.enabled = enabled;
        this.logFilePath = logFilePath;
        initFileWriter();
    }

    private void initFileWriter() {
        try {
            fileWriter = new PrintWriter(new FileWriter(logFilePath, true), true);
        } catch (IOException e) {
            System.err.println("Failed to initialize log file: " + e.getMessage());
        }
    }

    public void log(String message) {
        String formatted = "[" + LocalDateTime.now().format(FMT) + "] " + message;

        if (enabled) {
            System.out.println(formatted);
        }

        if (fileWriter != null) {
            fileWriter.println(formatted);
        }
    }

    public void close() {
        if (fileWriter != null) {
            fileWriter.close();
        }
    }
}