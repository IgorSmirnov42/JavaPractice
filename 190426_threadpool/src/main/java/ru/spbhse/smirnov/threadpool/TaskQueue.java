package ru.spbhse.smirnov.threadpool;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;

/**
 * Simple implementation of queue for multithreaded case
 * Based on {@code ArrayDeque}
 */
/* Access is package private because used only in ThreadPoolImpl */
class TaskQueue {

    private ArrayDeque<ThreadPoolImpl.Task<?>> queue = new ArrayDeque<>();

    /** Adds task in a queue */
    synchronized void putTask(@NotNull ThreadPoolImpl.Task<?> task) {
        queue.addFirst(task);
        if (queue.size() == 1) {
            notifyAll();
        }
    }

    /** Gets task from queue. If queue is empty, blocks thread until gets task */
    @NotNull
    @SuppressWarnings("all") // hide null warning which is impossible
    synchronized ThreadPoolImpl.Task<?> getTask() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        return queue.pollLast();
    }
}
