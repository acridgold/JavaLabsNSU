package ru.nsu.factory.threadpool;

import java.util.ArrayDeque;
import java.util.Deque;

public class ThreadPool {
    private final Deque<Task> queue = new ArrayDeque<>();
    private final Thread[] workers;
    private volatile boolean running = true;

    public ThreadPool(int workerCount) {
        workers = new Thread[workerCount];
        for (int i = 0; i < workerCount; i++) {
            workers[i] = new Thread(this::workerLoop, "Worker-" + i);
            workers[i].start();
        }
    }

    public synchronized void submit(Task task) {
        queue.addLast(task);
        notify();
    }

    public synchronized int getQueueSize() {
        return queue.size();
    }

    private void workerLoop() {
        while (running) {
            Task task;
            synchronized (this) {
                while (queue.isEmpty() && running) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                if (!running) return;
                task = queue.removeFirst();
            }
            try {
                task.execute();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    public synchronized void shutdown() {
        running = false;
        notifyAll();
        for (Thread w : workers) w.interrupt();
    }
}
