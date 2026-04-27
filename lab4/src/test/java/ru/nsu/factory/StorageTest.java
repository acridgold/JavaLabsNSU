package ru.nsu.factory;

import org.junit.jupiter.api.Test;
import ru.nsu.factory.model.Body;
import ru.nsu.factory.storage.Storage;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class StorageTest {

    @Test
    void putAndTakeReturnsSameItem() throws InterruptedException {
        Storage<Body> storage = new Storage<>(5);
        Body body = new Body();
        storage.put(body);
        assertSame(body, storage.take());
    }

    @Test
    void countReflectsPutAndTake() throws InterruptedException {
        Storage<Body> storage = new Storage<>(5);
        storage.put(new Body());
        storage.put(new Body());
        assertEquals(2, storage.getCount());
        storage.take();
        assertEquals(1, storage.getCount());
    }

    @Test
    void totalProducedIncrementsOnEachPut() throws InterruptedException {
        Storage<Body> storage = new Storage<>(5);
        storage.put(new Body());
        storage.put(new Body());
        storage.put(new Body());
        assertEquals(3, storage.getTotalProduced());
    }

    @Test
    void putBlocksWhenFull() throws InterruptedException {
        Storage<Body> storage = new Storage<>(1);
        storage.put(new Body()); // заполняем

        AtomicBoolean blocked = new AtomicBoolean(false);
        Thread producer = new Thread(() -> {
            try {
                blocked.set(true);
                storage.put(new Body()); // должен заблокироваться
                blocked.set(false);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        Thread.sleep(100); // даём время заблокироваться
        assertTrue(blocked.get());
        assertEquals(1, storage.getCount()); // второй элемент не добавился

        producer.interrupt();
        producer.join(500);
    }

    @Test
    void takeBlocksWhenEmpty() throws InterruptedException {
        Storage<Body> storage = new Storage<>(5);

        AtomicBoolean started = new AtomicBoolean(false);
        Thread consumer = new Thread(() -> {
            try {
                started.set(true);
                storage.take(); // должен заблокироваться
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        consumer.start();
        Thread.sleep(100);
        assertTrue(started.get());
        assertEquals(0, storage.getCount());

        consumer.interrupt();
        consumer.join(500);
    }

    @Test
    void fifoOrder() throws InterruptedException {
        Storage<Body> storage = new Storage<>(5);
        Body first  = new Body();
        Body second = new Body();
        Body third  = new Body();
        storage.put(first);
        storage.put(second);
        storage.put(third);
        assertSame(first,  storage.take());
        assertSame(second, storage.take());
        assertSame(third,  storage.take());
    }
}
