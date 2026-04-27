package ru.nsu.factory.threadpool;

public interface Task {
    void execute() throws InterruptedException;
}
