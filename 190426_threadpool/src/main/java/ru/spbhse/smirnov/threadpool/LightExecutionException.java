package ru.spbhse.smirnov.threadpool;

/**
 * Exception thrown by thread pool if execution of task from thread pool
 * was interrupted with exception
 */
public class LightExecutionException extends Exception {
    public LightExecutionException(String message, Throwable exception) {
        super(message, exception);
    }
}
