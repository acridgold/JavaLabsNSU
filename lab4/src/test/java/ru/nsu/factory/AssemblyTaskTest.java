package ru.nsu.factory;

import org.junit.jupiter.api.Test;
import ru.nsu.factory.factory.AssemblyTask;
import ru.nsu.factory.model.*;
import ru.nsu.factory.storage.Storage;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class AssemblyTaskTest {

    @Test
    void assemblesAutoFromParts() throws InterruptedException {
        Storage<Body>      bodies      = new Storage<>(5);
        Storage<Motor>     motors      = new Storage<>(5);
        Storage<Accessory> accessories = new Storage<>(5);
        Storage<Auto>      autos       = new Storage<>(5);

        bodies.put(new Body());
        motors.put(new Motor());
        accessories.put(new Accessory());

        AtomicInteger counter = new AtomicInteger(0);
        new AssemblyTask(bodies, motors, accessories, autos, counter::incrementAndGet).execute();

        assertEquals(1, autos.getCount());
        assertEquals(1, counter.get());
        assertEquals(0, bodies.getCount());
        assertEquals(0, motors.getCount());
        assertEquals(0, accessories.getCount());
    }

    @Test
    void assembledAutoContainsCorrectParts() throws InterruptedException {
        Storage<Body>      bodies      = new Storage<>(5);
        Storage<Motor>     motors      = new Storage<>(5);
        Storage<Accessory> accessories = new Storage<>(5);
        Storage<Auto>      autos       = new Storage<>(5);

        Body      body      = new Body();
        Motor     motor     = new Motor();
        Accessory accessory = new Accessory();

        bodies.put(body);
        motors.put(motor);
        accessories.put(accessory);

        new AssemblyTask(bodies, motors, accessories, autos, () -> {}).execute();

        Auto auto = autos.take();
        assertSame(body,      auto.getBody());
        assertSame(motor,     auto.getMotor());
        assertSame(accessory, auto.getAccessory());
    }

    @Test
    void autoHasUniqueId() throws InterruptedException {
        Storage<Body>      bodies      = new Storage<>(5);
        Storage<Motor>     motors      = new Storage<>(5);
        Storage<Accessory> accessories = new Storage<>(5);
        Storage<Auto>      autos       = new Storage<>(5);

        for (int i = 0; i < 3; i++) {
            bodies.put(new Body());
            motors.put(new Motor());
            accessories.put(new Accessory());
            new AssemblyTask(bodies, motors, accessories, autos, () -> {}).execute();
        }

        Auto a1 = autos.take();
        Auto a2 = autos.take();
        Auto a3 = autos.take();

        assertNotEquals(a1.getId(), a2.getId());
        assertNotEquals(a2.getId(), a3.getId());
    }
}
