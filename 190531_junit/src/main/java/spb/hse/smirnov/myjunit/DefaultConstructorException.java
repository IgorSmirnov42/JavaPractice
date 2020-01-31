package spb.hse.smirnov.myjunit;

/** Exception throwing in TestRunner if class has some problem with its constructor without parameters */
public class DefaultConstructorException extends Exception {
    public DefaultConstructorException(String message) {
        super(message);
    }
}
