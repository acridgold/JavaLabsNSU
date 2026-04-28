package ru.nsu.factory.storage;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

public class Storage<T> {
    private final Queue<T> items = new ArrayDeque<>();
    private final int capacity;

    private final AtomicInteger totalProduced = new AtomicInteger(0);

    public Storage(int capacity) {
        this.capacity = capacity;
    }

    public synchronized void put(T item) throws InterruptedException {
        while (items.size() >= capacity) {
            wait();
        }
        items.add(item);
        totalProduced.incrementAndGet();
        notifyAll();
    }

    public synchronized T take() throws InterruptedException {
        while (items.isEmpty()) {
            wait();
        }
        T item = items.poll();
        notifyAll();
        return item;
    }

    public synchronized int getCount() {
        return items.size();
    }

    public int getTotalProduced() {
        return totalProduced.get();
    }

    public int getCapacity() {
        return capacity;
    }
}
