package spb.hse.smirnov.myjunit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Methods marked with this annotation will be called after all @Test annotated methods in class execution
 * Method can be executed not ones, but with different instances of class
 * If class has no @Test annotated methods, this one wouldn't be executed too
 * Executes after all @After annotated methods
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AfterClass {
}
