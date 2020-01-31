package ru.spbhse.smirnov.threadpool;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Simple implementation of thread pool
 * Allows to create thead pool with fixed number of threads
 */
public class ThreadPoolImpl {
    @NotNull private final TaskQueue queue = new TaskQueue();
    @NotNull private final Thread[] threads;
    private volatile boolean shutdownFlag = false;

    @NotNull private final Object balanceBlock = new Object();
    /** Difference between accepted for execution number of tasks and finished tasks */
    private volatile int currentBalance = 0;

    /**
     * Creates thread pool with {@code numberOfThreads} threads and immediately starts it
     * @param numberOfThreads positive number
     * @throws IllegalArgumentException if {@code numberOfThreads} is not positive
     */
    public ThreadPoolImpl(int numberOfThreads) {
        if (numberOfThreads <= 0) {
            throw new IllegalArgumentException("Number of threads must be positive");
        }
        threads = new Thread[numberOfThreads];
        for (int threadId = 0; threadId < numberOfThreads; ++threadId) {
            threads[threadId] = new Thread(() -> {
                while (!Thread.interrupted()) {
                    try {
                        Task<?> task = queue.getTask();
                        task.execute();
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            });
            threads[threadId].start();
        }
    }

    /**
     * Adds new task to a pool
     * @param supplier task to execute
     * @throws IllegalStateException if method was called after shutting pool down
     * @return object of LightFuture interface to work with task
     */
    @NotNull
    public <T> LightFuture<T> submit(@NotNull Supplier<T> supplier) {
        synchronized (balanceBlock) {
            if (shutdownFlag) {
                throw new IllegalStateException("Submit must not be called after shutdown");
            }
            ++currentBalance;
        }
        var task = new Task<>(supplier);
        queue.putTask(task);
        return task;
    }

    /**
     * Completes thread pool execution
     *
     * All tasks that are already in thread pool will be finished, new submissions are not allowed.
     * Blocks current thread until all threads are not finished
     */
    /* I don't know, may be it is better to create a new thread inside
        and not to block current one...
    */
    public void shutdown() throws InterruptedException {
        synchronized (balanceBlock) {
            shutdownFlag = true;
            while (currentBalance != 0) {
                balanceBlock.wait();
            }
        }
        for (Thread thread : threads) {
            thread.interrupt();
        }
    }

    /** Implementation of LightFuture interface. Used by ThreadPoolImpl */
    class Task<T> implements LightFuture<T> {
        @NotNull private final ArrayDeque<Task<?>> tasksToApply = new ArrayDeque<>();

        @Nullable private LightExecutionException exception = null;
        private Supplier<T> task;
        @Nullable private T result = null;
        private volatile boolean ready = false;
        private final Object lock = new Object();

        /** {@inheritDoc} */
        @Override
        public boolean isReady() {
            return ready;
        }

        /** {@inheritDoc} */
        @Override
        @Nullable
        public T get() throws LightExecutionException, InterruptedException {
            if (!ready) {
                synchronized (lock) {
                    while (!ready) {
                        lock.wait();
                    }
                }
            }
            if (exception != null) {
                throw exception;
            }
            return result;
        }

        /** {@inheritDoc} */
        @Override
        @NotNull
        public <F> LightFuture<F> thenApply(@NotNull Function<? super T, F> functionToApply) {
            synchronized (balanceBlock) {
                if (shutdownFlag) {
                    throw new IllegalStateException("thenApply must not be called after shutdown");
                }
                ++currentBalance;
            }
            synchronized (tasksToApply) {
                var task = new Task<>(() -> functionToApply.apply(result));
                if (ready) {
                    queue.putTask(task);
                    if (exception != null) {
                        task.exception = new LightExecutionException(
                                "Exception during previous task execution", exception);
                    }
                } else {
                    tasksToApply.addFirst(task);
                }
                return task;
            }
        }

        /** Creates Task object. Doesn't start it's execution */
        public Task(@NotNull Supplier<T> task) {
            this.task = task;
        }

        /** Starts and finishes execution of current task */
        private void execute() {
            if (exception == null) {
                try {
                    result = task.get();
                } catch (Exception mainException) {
                    exception = new LightExecutionException("Exception during execution",
                            mainException);
                }
            }
            task = null;
            ready = true;
            synchronized (lock) {
                lock.notifyAll();
            }

            synchronized (tasksToApply) {
                for (Task<?> task : tasksToApply) {
                    if (exception != null) {
                        task.exception = new LightExecutionException(
                                "Exception during previous task execution", exception);
                    }
                    queue.putTask(task);
                }
            }

            synchronized (balanceBlock) {
                --currentBalance;
                if (shutdownFlag && currentBalance == 0) {
                    balanceBlock.notify();
                }
            }
        }
    }
}
