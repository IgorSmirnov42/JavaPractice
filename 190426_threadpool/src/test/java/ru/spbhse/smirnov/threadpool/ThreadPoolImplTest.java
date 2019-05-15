package ru.spbhse.smirnov.threadpool;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class ThreadPoolImplTest {
    private ThreadPoolImpl tpool4;
    private Supplier<Integer> task2 = () -> 2;
    // On my computer works ~ 0.16 sec
    private Supplier<Integer> middleTask = () -> {
        Integer res = 223;
        for (int i = 0; i < 20000000; ++i) {
            res += res - 21 * res ^ (res + 42);
        }
        return res;
    };
    private final int answerForMiddleTask = -2;
    private Function<Integer, Integer> sqr = integer -> integer * integer;
    private Function<Integer, Integer> thr = integer -> integer * integer * integer;

    @BeforeEach
    void init() {
        tpool4 = new ThreadPoolImpl(4);
    }

    @Test
    void shouldExecuteSimpleTasks() throws LightExecutionException, InterruptedException {
        final int tasks = 5;
        LightFuture<?>[] future = new LightFuture[tasks];
        for (int i = 0; i < tasks; ++i) {
            future[i] = tpool4.submit(middleTask);
        }
        for (var task : future) {
            assertEquals(answerForMiddleTask, task.get());
        }
    }

    @Test
    void shouldExecuteManySimpleTasks() throws LightExecutionException, InterruptedException {
        final int tasks = 100;
        LightFuture<?>[] future = new LightFuture[tasks];
        for (int i = 0; i < tasks; ++i) {
            future[i] = tpool4.submit(middleTask);
        }
        for (var task : future) {
            assertEquals(answerForMiddleTask, task.get());
        }
    }

    @Test
    void shouldFinishAllTasksInQueueAfterShutdown() throws LightExecutionException, InterruptedException {
        final int tasks = 20;
        LightFuture<?>[] future = new LightFuture[tasks];
        for (int i = 0; i < tasks; ++i) {
            future[i] = tpool4.submit(middleTask);
        }
        tpool4.shutdown();
        for (var task : future) {
            assertTrue(task.isReady());
            assertEquals(answerForMiddleTask, task.get());
        }
    }

    @Test
    @SuppressWarnings("all")
    void shouldThrowLightFutureException() throws LightExecutionException, InterruptedException {
        assertThrows(LightExecutionException.class, () -> tpool4.submit(() -> {
            int x = 2 / 0;
            return x;
        }).get());
    }

    @Test
    void thenApplySimpleTest() throws LightExecutionException, InterruptedException {
        assertEquals(4, tpool4.submit(task2).thenApply(sqr).get());
    }

    @Test
    void thenApplyCanBeAppliedManyTimes() throws LightExecutionException, InterruptedException {
        var task = tpool4.submit(task2);
        assertEquals(4, task.thenApply(sqr).get());
        assertEquals(8, task.thenApply(thr).get());
    }

    @Test
    void thenApplyCanBeAppliedToLongTask() throws LightExecutionException, InterruptedException {
        var task = tpool4.submit(middleTask);
        assertEquals(sqr.apply(answerForMiddleTask), task.thenApply(sqr).get());
    }

    @Test
    void shouldExecuteEvenThenAppliedAfterShutdown() throws InterruptedException, LightExecutionException {
        final int tasks = 20;
        ArrayList<LightFuture<Integer>> future = new ArrayList<>();
        for (int i = 0; i < tasks; ++i) {
            future.add(tpool4.submit(middleTask));
        }
        for (int i = 0; i < tasks; ++i) {
            future.add(future.get(i).thenApply(sqr));
        }
        assertFalse(future.get(future.size() - 1).isReady());
        tpool4.shutdown();
        for (int i = 0; i < tasks; ++i) {
            assertTrue(future.get(i).isReady());
            assertEquals(answerForMiddleTask, future.get(i).get());
        }
        for (int i = tasks; i < future.size(); ++i) {
            assertTrue(future.get(i).isReady());
            assertEquals(sqr.apply(answerForMiddleTask), future.get(i).get());
        }
    }

    @Test
    void shouldThrowOnSubmitAfterShutdown() throws InterruptedException {
        tpool4.shutdown();
        assertThrows(IllegalStateException.class, () -> tpool4.submit(task2));
    }

    @Test
    @SuppressWarnings("all")
    void shouldThrowOnThenApplyIfPreviousThrow() throws InterruptedException, LightExecutionException {
        var task = tpool4.submit(() -> {
            int x = 2 / 0;
            return x;
        });

        // Waiting to be done
        middleTask.get();
        assertThrows(LightExecutionException.class, () -> task.thenApply(sqr).get());


        // With filled queue
        final int times = 20;
        for (int i = 0; i < times; ++i) {
            tpool4.submit(middleTask);
        }
        assertThrows(LightExecutionException.class, () -> tpool4.submit(() -> {
            int x = 2 / 0;
            return x;
        }).thenApply(sqr).get());
    }

    @Test
    void shouldThrowOnNonPositiveNumberOfThreads() {
        assertThrows(IllegalArgumentException.class, () -> new ThreadPoolImpl(0));
        assertThrows(IllegalArgumentException.class, () -> new ThreadPoolImpl(-42));
    }

    @Test
    void reallyManyThreads() throws InterruptedException {
        reallyNThreads(5);
        reallyNThreads(1);
        reallyNThreads(7);
        reallyNThreads(11);
    }

    void reallyNThreads(int n) throws InterruptedException {
        Set<Thread> threads = new HashSet<>();
        var pool = new ThreadPoolImpl(n);
        for (int i = 0; i < n * 1000; ++i) {
            pool.submit((Supplier<Void>) () -> {
                synchronized (threads) {
                    threads.add(Thread.currentThread());
                }
                return null;
            });
        }
        pool.shutdown();
        assertEquals(n, threads.size());
    }

    @Test
    void shouldThrowOnNulls() {
        assertThrows(IllegalArgumentException.class, () -> tpool4.submit(null));
        assertThrows(IllegalArgumentException.class, () -> tpool4.submit(task2).thenApply(null));
    }
}