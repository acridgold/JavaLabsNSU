package ru.nsu.factory.logger;

import ru.nsu.factory.model.Auto;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SaleLogger implements Closeable {
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final BufferedWriter writer;
    private final boolean enabled;

    public SaleLogger(String filePath, boolean enabled) throws IOException {
        this.enabled = enabled;
        if (enabled) {
            writer = new BufferedWriter(new FileWriter(filePath, true));
        } else {
            writer = null;
        }
    }

    public synchronized void logSale(int dealerNumber, Auto auto) {
        if (!enabled) return;
        String line = String.format("%s: Dealer %d: Auto %d (Body: %d, Motor: %d, Accessory: %d)",
                LocalDateTime.now().format(FMT),
                dealerNumber,
                auto.getId(),
                auto.getBody().getId(),
                auto.getMotor().getId(),
                auto.getAccessory().getId()
        );
        try {
            assert writer != null;
            writer.write(line);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            System.err.println("SaleLogger write error: " + e.getMessage());
        }
    }

    @Override
    public void close() throws IOException {
        if (writer != null) writer.close();
    }
}
