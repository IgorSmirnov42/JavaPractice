package spb.hse.smirnov.myjunit;

/** Exception throwing in TestRunner if some unexpected Exception
 *          was thrown during execution of preparing methods
 */
public class RunningTestsException extends Exception {
    public RunningTestsException(String message) {
        super(message);
    }
}
