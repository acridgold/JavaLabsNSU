package ru.nsu.factory.factory;

import ru.nsu.factory.model.*;
import ru.nsu.factory.storage.Storage;
import ru.nsu.factory.threadpool.ThreadPool;

public class StockController implements Runnable {
    private final Storage<Auto> autoStorage;
    private final Storage<Body> bodyStorage;
    private final Storage<Motor> motorStorage;
    private final Storage<Accessory> accessoryStorage;
    private final ThreadPool threadPool;
    private final Runnable onAutoProduced;
    private volatile boolean running = true;

    public StockController(
            Storage<Auto> autoStorage,
            Storage<Body> bodyStorage,
            Storage<Motor> motorStorage,
            Storage<Accessory> accessoryStorage,
            ThreadPool threadPool,
            Runnable onAutoProduced
    ) {
        this.autoStorage = autoStorage;
        this.bodyStorage = bodyStorage;
        this.motorStorage = motorStorage;
        this.accessoryStorage = accessoryStorage;
        this.threadPool = threadPool;
        this.onAutoProduced = onAutoProduced;
    }

    @Override
    public void run() {
        while (running) {
            synchronized (autoStorage) {
                while (autoStorage.getCount() == autoStorage.getCapacity() && running) {
                    try { autoStorage.wait(); } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
            if (!running) return;

            int free = autoStorage.getCapacity() - autoStorage.getCount()
                     - threadPool.getQueueSize();
            for (int i = 0; i < free; i++) {
                threadPool.submit(new AssemblyTask(
                        bodyStorage, motorStorage, accessoryStorage,
                        autoStorage, onAutoProduced
                ));
            }
        }
    }

    public void stop() { running = false; }
}
