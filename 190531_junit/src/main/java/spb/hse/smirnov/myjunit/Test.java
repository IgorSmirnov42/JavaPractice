package spb.hse.smirnov.myjunit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Annotation to mark method as method that should be tested */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Test {
    /**
     * Exception that may (but may not)  interrupt invocation of a test
     * If it did, test considered to be passed
     * If it didn't and no other exception was thrown during invocation, test considered to be passed too
     * If method is annotated with some preparing annotation, {@code expected()} will have no effect
     *          not in invocation time
     */
    Class<? extends Throwable> expected() default NoException.class;
    /**
     * Reason why the test should not be executed
     * If it is equal to {@code TestRunner.NO_IGNORE}, test will be executed
     */
    String ignore() default TestRunner.NO_IGNORE;
}

/** Helping class to indicate if there is no expected exception */
class NoException extends Throwable {
}
