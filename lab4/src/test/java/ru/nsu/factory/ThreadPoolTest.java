package ru.nsu.factory;

import org.junit.jupiter.api.Test;
import ru.nsu.factory.threadpool.ThreadPool;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class ThreadPoolTest {

    @Test
    void submittedTasksAreExecuted() throws InterruptedException {
        ThreadPool pool = new ThreadPool(2);
        int taskCount = 5;
        CountDownLatch latch = new CountDownLatch(taskCount);

        for (int i = 0; i < taskCount; i++) {
            pool.submit(latch::countDown);
        }

        assertTrue(latch.await(2, TimeUnit.SECONDS));
        pool.shutdown();
    }

    @Test
    void allTasksExecutedExactlyOnce() throws InterruptedException {
        ThreadPool pool = new ThreadPool(3);
        int taskCount = 10;
        AtomicInteger counter = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(taskCount);

        for (int i = 0; i < taskCount; i++) {
            pool.submit(() -> {
                counter.incrementAndGet();
                latch.countDown();
            });
        }

        latch.await(2, TimeUnit.SECONDS);
        assertEquals(taskCount, counter.get());
        pool.shutdown();
    }

    @Test
    void queueSizeDecreasesAsTasksComplete() throws InterruptedException {
        ThreadPool pool = new ThreadPool(1);
        CountDownLatch blockLatch = new CountDownLatch(1);

        // Блокируем единственного рабочего
        pool.submit(blockLatch::await);

        // Добавляем ещё задачи — они осядут в очереди
        pool.submit(() -> {});
        pool.submit(() -> {});
        Thread.sleep(50);
        assertEquals(2, pool.getQueueSize());

        // Отпускаем рабочего
        blockLatch.countDown();
        Thread.sleep(100);
        assertEquals(0, pool.getQueueSize());
        pool.shutdown();
    }

    @Test
    void shutdownStopsWorkers() throws InterruptedException {
        ThreadPool pool = new ThreadPool(2);
        pool.shutdown();

        // После shutdown новые задачи не должны зависать — просто не выполнятся
        AtomicInteger counter = new AtomicInteger(0);
        pool.submit(counter::incrementAndGet);
        Thread.sleep(100);
        // Нет гарантии выполнения после shutdown — просто не должно зависнуть
    }
}
