package spb.hse.smirnov.myjunit;

/** Exception throwing in TestRunner if some method in class was annotated but has unacceptable signature */
public class WrongAnnotationException extends Exception {
    public WrongAnnotationException(String message) {
        super(message);
    }
}
