package ru.nsu.factory.factory;

import ru.nsu.factory.model.Accessory;
import ru.nsu.factory.model.Auto;
import ru.nsu.factory.model.Body;
import ru.nsu.factory.model.Motor;
import ru.nsu.factory.storage.Storage;
import ru.nsu.factory.threadpool.Task;

import java.util.logging.Logger;

public class AssemblyTask implements Task {
    private static final Logger log = Logger.getLogger(AssemblyTask.class.getName());

    private final Storage<Body> bodyStorage;
    private final Storage<Motor> motorStorage;
    private final Storage<Accessory> accessoryStorage;
    private final Storage<Auto> autoStorage;
    private final Runnable onAutoProduced;

    public AssemblyTask(
            Storage<Body> bodyStorage,
            Storage<Motor> motorStorage,
            Storage<Accessory> accessoryStorage,
            Storage<Auto> autoStorage,
            Runnable onAutoProduced
    ) {
        this.bodyStorage = bodyStorage;
        this.motorStorage = motorStorage;
        this.accessoryStorage = accessoryStorage;
        this.autoStorage = autoStorage;
        this.onAutoProduced = onAutoProduced;
    }

    @Override
    public void execute() throws InterruptedException {
        Body body = bodyStorage.take();
        Motor motor = motorStorage.take();
        Accessory accessory = accessoryStorage.take();

        Auto auto = new Auto(body, motor, accessory);

        log.info(Thread.currentThread().getName()
                + " собрал Auto#" + auto.getId()
                + " из: " + body + ", " + motor + ", " + accessory);

        autoStorage.put(auto);
        onAutoProduced.run();
    }
}