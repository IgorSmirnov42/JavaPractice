package spb.hse.smirnov.myjunit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Test {
    Class<? extends Throwable> expected() default NoException.class;
    String ignore() default TestRunner.NO_IGNORE;
}

class NoException extends Throwable {
}
