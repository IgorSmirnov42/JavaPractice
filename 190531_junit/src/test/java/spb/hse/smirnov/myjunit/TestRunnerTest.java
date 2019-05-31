package spb.hse.smirnov.myjunit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spb.hse.smirnov.myjunit.classes.*;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

public class TestRunnerTest {

    TestRunner runner;

    @BeforeEach
    void initRunner() {
        runner = new TestRunner();
    }

    @Test
    void shouldDetectBadAnnotatedClasses() throws WrongAnnotationException, DefaultConstructorException {
        assertTrue(runner.hasWrongAnnotatedMethods(TestWithParameters.class));
        assertFalse(runner.isTestClass(ClassWithoutTestMethods.class));
        assertThrows(WrongAnnotationException.class, () -> runner.addTestClass(TestWithParameters.class));
        runner.addTestClass(ClassWithoutTestMethods.class);
        assertEquals(0, runner.getTestClasses().size());
    }

    @Test
    void shouldRejectClassesWithoutDefaultConstructors() {
        assertThrows(DefaultConstructorException.class,
                () -> runner.addTestClass(ClassWithoutDefaultConstructor.class));
    }

    @Test
    void shouldAcceptNormalClasses() throws WrongAnnotationException, DefaultConstructorException {
        runner.addTestClass(ClassWithOneTest.class);
        assertEquals(1, runner.getTestClasses().size());
        runner.addTestClass(FullyAnnotatedClass.class);
    }

    @Test
    void shouldRunTests() throws WrongAnnotationException, DefaultConstructorException, InterruptedException, ExecutionException, RunningTestsException {
        runner.addTestClass(FullyAnnotatedClass.class);
        assertEquals(TestRunner.ExecutionResult.SUCCESS,
                runner.runTests().get(FullyAnnotatedClass.class).get(0).getTaskResult().getResult());
        runner.addTestClass(FailingTest.class);
        assertEquals(TestRunner.ExecutionResult.FAILED,
                runner.runTests().get(FailingTest.class).get(0).getTaskResult().getResult());
        runner.addTestClass(IgnoredTest.class);
        assertEquals(TestRunner.ExecutionResult.IGNORED,
                runner.runTests().get(IgnoredTest.class).get(0).getTaskResult().getResult());
    }

    @Test
    void shouldThrowOnErrorInConstructor() throws WrongAnnotationException, DefaultConstructorException {
        runner.addTestClass(ErrorInConstructor.class);
        assertThrows(ExecutionException.class, () -> runner.runTests());
    }

    @Test
    void shouldThrowOnErrorInPreparing() throws WrongAnnotationException, DefaultConstructorException {
        runner.addTestClass(ErrorInBefore.class);
        assertThrows(ExecutionException.class, () -> runner.runTests());
    }

    @Test
    void expectedShouldWork() throws InterruptedException, ExecutionException, RunningTestsException, WrongAnnotationException, DefaultConstructorException {
        runner.addTestClass(ExpectedTests.class);
        var res = runner.runTests().get(ExpectedTests.class);
        assertEquals(TestRunner.ExecutionResult.SUCCESS, res.get(0).getTaskResult().getResult());
        assertEquals(TestRunner.ExecutionResult.SUCCESS, res.get(1).getTaskResult().getResult());

        runner.addTestClass(AnotherExpected.class);
        assertEquals(TestRunner.ExecutionResult.FAILED,
                runner.runTests().get(AnotherExpected.class).get(0).getTaskResult().getResult());
    }
}
