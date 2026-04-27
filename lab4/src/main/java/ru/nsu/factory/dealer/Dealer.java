package ru.nsu.factory.dealer;

import ru.nsu.factory.logger.SaleLogger;
import ru.nsu.factory.model.Auto;
import ru.nsu.factory.storage.Storage;

public class Dealer implements Runnable {
    private final int number;
    private final Storage<Auto> autoStorage;
    private final SaleLogger saleLogger;
    private volatile int delayMs;
    private volatile boolean running = true;

    public Dealer(int number, Storage<Auto> autoStorage, SaleLogger saleLogger, int delayMs) {
        this.number = number;
        this.autoStorage = autoStorage;
        this.saleLogger = saleLogger;
        this.delayMs = delayMs;
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(delayMs);

                Auto auto = autoStorage.take();

                synchronized (autoStorage) {
                    autoStorage.notifyAll();
                }

                saleLogger.logSale(number, auto);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void setDelayMs(int delayMs) {
        this.delayMs = delayMs;
    }

    public int getDelayMs() {
        return delayMs;
    }

    public void stop() {
        running = false;
    }

    public int getNumber() {
        return number;
    }
}
