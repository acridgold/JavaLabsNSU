package ru.nsu.factory.model;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class Part {
    private static final AtomicInteger counter = new AtomicInteger(0);
    private final int id;

    protected Part() {
        this.id = counter.incrementAndGet();
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "#" + id;
    }
}
