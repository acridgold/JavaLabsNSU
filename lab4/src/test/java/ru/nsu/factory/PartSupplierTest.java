package ru.nsu.factory;

import org.junit.jupiter.api.Test;
import ru.nsu.factory.model.Body;
import ru.nsu.factory.storage.Storage;
import ru.nsu.factory.supplier.PartSupplier;

import static org.junit.jupiter.api.Assertions.*;

class PartSupplierTest {

    @Test
    void supplierPutsPartsIntoStorage() throws InterruptedException {
        Storage<Body> storage = new Storage<>(10);
        PartSupplier<Body> supplier = new PartSupplier<>("Test", storage, Body::new, 50);

        Thread t = new Thread(supplier);
        t.start();

        // Ждём пока появится хотя бы 2 детали
        long deadline = System.currentTimeMillis() + 2000;
        while (storage.getCount() < 2 && System.currentTimeMillis() < deadline) {
            Thread.sleep(20);
        }

        supplier.stop();
        t.interrupt();
        t.join(500);

        assertTrue(storage.getCount() >= 2);
    }

    @Test
    void stopHaltsProduction() throws InterruptedException {
        Storage<Body> storage = new Storage<>(100);
        PartSupplier<Body> supplier = new PartSupplier<>("Test", storage, Body::new, 50);

        Thread t = new Thread(supplier);
        t.start();
        Thread.sleep(200);

        supplier.stop();
        t.interrupt();
        t.join(500);

        int countAfterStop = storage.getCount();
        Thread.sleep(200);
        // После остановки количество не должно расти
        assertEquals(countAfterStop, storage.getCount());
    }

    @Test
    void setDelayChangesProductionSpeed() throws InterruptedException {
        Storage<Body> fast = new Storage<>(100);
        Storage<Body> slow = new Storage<>(100);

        PartSupplier<Body> fastSupplier = new PartSupplier<>("Fast", fast, Body::new, 30);
        PartSupplier<Body> slowSupplier = new PartSupplier<>("Slow", slow, Body::new, 300);

        Thread t1 = new Thread(fastSupplier);
        Thread t2 = new Thread(slowSupplier);
        t1.start();
        t2.start();

        Thread.sleep(500);

        fastSupplier.stop(); t1.interrupt(); t1.join(300);
        slowSupplier.stop(); t2.interrupt(); t2.join(300);

        assertTrue(fast.getCount() > slow.getCount());
    }
}
