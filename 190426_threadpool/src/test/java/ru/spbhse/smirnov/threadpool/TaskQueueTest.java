package ru.spbhse.smirnov.threadpool;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.jodah.concurrentunit.Waiter;

import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;

class TaskQueueTest {

    private TaskQueue queue;
    private ThreadPoolImpl tpool = new ThreadPoolImpl(1); // For creating tasks
    private ThreadPoolImpl.Task<Integer> task1 = tpool.new Task<>(() -> 1);
    private ThreadPoolImpl.Task<Integer> task2 = tpool.new Task<>(() -> 2);
    private ThreadPoolImpl.Task<Integer> task3 = tpool.new Task<>(() -> 3);

    @BeforeEach
    void init() {
        queue = new TaskQueue();
    }

    @Test
    void simpleOneThreadTest() throws InterruptedException {
        queue.putTask(task1);
        queue.putTask(task2);
        assertSame(task1, queue.getTask());
        queue.putTask(task3);
        assertSame(task2, queue.getTask());
        assertSame(task3, queue.getTask());
    }

    @Test
    void severalTasksShouldBeWaited() throws InterruptedException, TimeoutException {
        runManyСonsumersAndProducers(1, 1);
    }

    @Test
    void shouldNotBeProblemsWithSeveralConsumers() throws InterruptedException, TimeoutException {
        runManyСonsumersAndProducers(100, 1);
    }

    @Test
    void shouldNotBeProblemsWithSeveralProducers() throws InterruptedException, TimeoutException {
        runManyСonsumersAndProducers(1, 100);
    }

    @Test
    void shouldNotBeProblemsWithSeveralConsumersAndProductors() throws InterruptedException, TimeoutException {
        runManyСonsumersAndProducers(100, 100);
    }

    void runManyСonsumersAndProducers(int consumersCounter, int productorsCounter) throws InterruptedException, TimeoutException {
        final int times = 100;
        final int tasks = 100;
        assert tasks * consumersCounter % productorsCounter == 0;
        for (int i = 0; i < times; ++i) {
            final var waiter = new Waiter();
            var threads = new Thread[consumersCounter + productorsCounter];
            for (int c = 0; c < consumersCounter; ++c) {
                threads[c] = new Thread(() -> {
                    for (int a = 0; a < tasks; ++a) {
                        try {
                            waiter.assertTrue(task1 == queue.getTask());
                        } catch (InterruptedException e) {}
                    }
                });
                threads[c].start();
            }
            int taskProducer = tasks * consumersCounter / productorsCounter;
            for (int p = consumersCounter; p < consumersCounter + productorsCounter; ++p) {
                threads[p] = new Thread(() -> {
                    for (int a = 0; a < taskProducer; ++a) {
                        queue.putTask(task1);
                    }
                });
                threads[p].start();
            }
            for (Thread t : threads) {
                t.join();
            }
            waiter.resume();
            waiter.await();
        }
    }
}