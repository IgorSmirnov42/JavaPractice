package ru.spbhse.smirnov.threadpool;

import java.util.function.Function;

/** Interface to work with tasks from thread pool */
public interface LightFuture<T> {
    /** Checks if answer for task is already calculated */
    boolean isReady();

    /**
     * Returns answer for a task
     * If task is not calculated yet, blocks current thread
     * @throws LightExecutionException if execution was interrupted with exception
     */
    T get() throws LightExecutionException, InterruptedException;

    /**
     * Applies given function to the result of this task
     * May be called several times
     * Does not block thread if task is not calculated
     */
    <S> LightFuture<S> thenApply(Function<? super T, S> functionToApply);
}
