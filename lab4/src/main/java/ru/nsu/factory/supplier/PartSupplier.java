package ru.nsu.factory.supplier;

import ru.nsu.factory.model.Part;
import ru.nsu.factory.storage.Storage;

import java.util.function.Supplier;
import java.util.logging.Logger;

public class PartSupplier<T extends Part> implements Runnable {
    private static final Logger log = Logger.getLogger(PartSupplier.class.getName());

    private final String name;
    private final Storage<T> storage;
    private final Supplier<T> factory;
    private volatile int delayMs;
    private volatile boolean running = true;

    public PartSupplier(String name, Storage<T> storage, Supplier<T> factory, int delayMs) {
        this.name = name;
        this.storage = storage;
        this.factory = factory;
        this.delayMs = delayMs;
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(delayMs);

                T part = factory.get();
                log.info(name + " создал деталь: " + part);

                storage.put(part);
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

    public String getName() {
        return name;
    }
}
