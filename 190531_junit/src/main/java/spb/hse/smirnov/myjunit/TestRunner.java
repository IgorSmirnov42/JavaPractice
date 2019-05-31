package spb.hse.smirnov.myjunit;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TestRunner {

    public static final String NO_IGNORE = "TestRunner.NO_IGNORE";
    @NotNull private List<TestClass> testClasses = new ArrayList<>();

    public static void main(@NotNull String[] args) throws InterruptedException, MalformedURLException, ClassNotFoundException {
        if (args.length != 1) {
            throw new IllegalArgumentException("Should take one and only one argument: path");
        }
        var runner = new TestRunner();
        String path = args[0];
        var file = new File(path);
        if (!file.exists()) {
            throw new IllegalArgumentException("Path does not exist");
        }
        if (path.endsWith(".class")) {
            try {
                runner.addTestClass(loadClass(file));
            } catch (WrongAnnotationException e) {
                System.out.println("It seems, you use annotations in a wrong way");
                e.printStackTrace();
            } catch (DefaultConstructorException e) {
                System.out.println("Test class must have default constructor without parameters");
                e.printStackTrace();
            }
        } else if (path.endsWith(".jar")) {
            for (var clazz : loadClassesFromJar(file)) {
                try {
                    runner.addTestClass(clazz);
                } catch (WrongAnnotationException e) {
                    System.out.println("It seems, you use annotations in a wrong way");
                    e.printStackTrace();
                } catch (DefaultConstructorException e) {
                    System.out.println("Test class must have default constructor without parameters");
                    e.printStackTrace();
                }
            }
        } else {
            throw new IllegalArgumentException("Unexpected path. Should be .class or .jar file");
        }

        Map<Class<?>, List<RunTestResult>> runnerResult;
        try {
            runnerResult = runner.runTests();
        } catch (ExecutionException e) {
            System.out.println("Error while executing tests.");
            e.getCause().printStackTrace();
            return;
        } catch (RunningTestsException e) {
            System.out.println("Error while executing tests.");
            e.printStackTrace();
            return;
        }

        int failed = 0;
        int ok = 0;
        int ignored = 0;

        for (Class<?> clazz : runnerResult.keySet()) {
            System.out.println("============");
            System.out.println("Tests from " + clazz.getName());
            List<RunTestResult> testsResults = runnerResult.get(clazz);
            for (var testResult : testsResults) {
                String resultToPrint = testResult.methodName + " ";
                if (testResult.taskResult.result == ExecutionResult.FAILED) {
                    resultToPrint += "FAILED. Message: " + testResult.taskResult.message;
                    ++failed;
                } else if (testResult.taskResult.result == ExecutionResult.SUCCESS) {
                    resultToPrint += "OK. Time: " + testResult.taskResult.message;
                    ++ok;
                } else if (testResult.taskResult.result == ExecutionResult.IGNORED) {
                    resultToPrint += "IGNORED. Comment: " + testResult.taskResult.message;
                    ++ignored;
                }
                System.out.println(resultToPrint);
            }
            System.out.println("============");
        }
        System.out.println("Tests: " + (ok + failed + ignored) + ", OK: " +
                ok + ", Failed: " + failed + ", Ignored: " + ignored);
    }

    @NotNull
    private static List<Class<?>> loadClassesFromJar(@NotNull File jarFile) {
        // TODO
        return null;
    }

    @NotNull
    private static Class<?> loadClass(@NotNull File classFile) throws MalformedURLException, ClassNotFoundException {
        //System.out.println(classFile.getName().substring(0, classFile.getName().length() - ".class".length()));
        System.out.println(classFile.toPath().getParent().toUri().toURL());
        var classLoader = URLClassLoader.newInstance(new URL[]{classFile.toPath().getParent().toUri().toURL()});
        return classLoader.loadClass(classFile.getName()
                .substring(0, classFile.getName().length() - ".class".length()));
    }

    private void addTestClass(@NotNull Class<?> clazz) throws DefaultConstructorException, WrongAnnotationException {
        if (!isTestClass(clazz)) {
            return;
        }
        if (hasWrongAnnotatedMethods(clazz)) {
            throw new WrongAnnotationException(
                    "Class " + clazz.getName() + " has methods that are annotated with Test annotations" +
                            " but take parameters");
        }
        TestClass testClass;
        try {
            testClass = new TestClass(clazz.getDeclaredConstructor(), clazz);
        } catch (NoSuchMethodException e) {
            throw new DefaultConstructorException(
                    "Class " + clazz.getName() + " has no default constructor");
        }
        for (var method : clazz.getDeclaredMethods()) {
            method.setAccessible(true);
            if (method.isAnnotationPresent(BeforeClass.class)) {
                testClass.beforeClassMethods.add(method);
            }
            if (method.isAnnotationPresent(Before.class)) {
                testClass.beforeTestMethods.add(method);
            }
            if (method.isAnnotationPresent(Test.class)) {
                testClass.testMethods.add(method);
            }
            if (method.isAnnotationPresent(After.class)) {
                testClass.afterTestMethods.add(method);
            }
            if (method.isAnnotationPresent(AfterClass.class)) {
                testClass.afterClassMethods.add(method);
            }
        }
        testClasses.add(testClass);
    }

    private boolean hasWrongAnnotatedMethods(@NotNull Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredMethods()).parallel()
                .filter(method -> method.isAnnotationPresent(Test.class)
                        || method.isAnnotationPresent(Before.class)
                        || method.isAnnotationPresent(BeforeClass.class)
                        || method.isAnnotationPresent(After.class)
                        || method.isAnnotationPresent(AfterClass.class))
                .anyMatch(method -> method.getParameterCount() != 0);
    }

    private boolean isTestClass(@NotNull Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredMethods()).parallel()
                .anyMatch(method -> method.isAnnotationPresent(Test.class));
    }

    private Map<Class<?>, List<RunTestResult>> runTests() throws ExecutionException, InterruptedException, RunningTestsException {
        var resultingMap = new HashMap<Class<?>, List<RunTestResult>>();
        int nThreads = Runtime.getRuntime().availableProcessors();
        var threadPool = Executors.newFixedThreadPool(nThreads);

        for (var testClass : testClasses) {
            var classInstance = new HashMap<Long, Object>();
            var futures = new ArrayList<Future<TaskResult>>();

            for (Method test : testClass.testMethods) {
               var task = new Callable<TaskResult>() {
                   @Override
                   public TaskResult call() throws RunningTestsException {
                       if (!test.getAnnotation(Test.class).ignore().equals(NO_IGNORE)) {
                           return new TaskResult(ExecutionResult.IGNORED,
                                   test.getAnnotation(Test.class).ignore());
                       }
                       Object instance;
                       long threadId = Thread.currentThread().getId();
                       synchronized (classInstance) {
                           if (classInstance.containsKey(threadId)) {
                               instance = classInstance.get(threadId);
                           } else {
                               // Creating new instance
                               try {
                                   testClass.classDefaultConstructor.setAccessible(true);
                                   instance = testClass.classDefaultConstructor.newInstance();
                               } catch (Throwable e) {
                                   var exception = new RunningTestsException(
                                           "Error while creating instance of " + testClass.className);
                                   exception.addSuppressed(e);
                                   throw exception;
                               }

                               runPreparingMethods(testClass.beforeClassMethods, "@BeforeClass",
                                       instance, testClass.className);

                               classInstance.put(threadId, instance);
                           }
                       }

                       runPreparingMethods(testClass.beforeTestMethods, "@Before",
                               instance, testClass.className);

                       // Running test
                       TaskResult result = null;
                       long startInvocationTime = System.currentTimeMillis();
                       try {
                           test.invoke(instance);
                       } catch (Throwable e) {
                           var cause = e.getCause();
                           var expected = test.getAnnotation(Test.class).expected();
                           if (!expected.isInstance(cause.getClass())) {
                               result = new TaskResult(ExecutionResult.FAILED, cause.getMessage());
                           }
                       }
                       if (result == null) {
                           long time = System.currentTimeMillis() - startInvocationTime;
                           result = new TaskResult(ExecutionResult.SUCCESS, time + "ms");
                       }

                       runPreparingMethods(testClass.afterTestMethods, "@After",
                               instance, testClass.className);

                       return result;
                   }
               };
               futures.add(threadPool.submit(task));
            }

            var resultingList = new ArrayList<RunTestResult>();
            int methodId = 0;
            for (var future : futures) {
                TaskResult result = future.get();
                String methodName = testClass.testMethods.get(methodId++).getName();
                resultingList.add(new RunTestResult(methodName, result));
            }

            for (var threadId : classInstance.keySet()) {
                runPreparingMethods(testClass.afterClassMethods, "@AfterClass",
                        classInstance.get(threadId), testClass.className);
            }
            resultingMap.put(testClass.clazz, resultingList);
        }
        threadPool.shutdown();
        return resultingMap;
    }

    private void runPreparingMethods(@NotNull List<Method> methods,
                                     @NotNull String annotationName,
                                     @NotNull Object instance,
                                     @NotNull String className) throws RunningTestsException {
        try {
            for (var method : methods) {
                method.setAccessible(true);
                method.invoke(instance);
            }
        } catch (Throwable e) {
            var exception = new RunningTestsException(
                    "Exception while invocation " + annotationName + " methods in " + className);
            exception.addSuppressed(e);
            throw exception;
        }
    }

    private static class TestClass {
        @NotNull private List<Method> beforeClassMethods = new ArrayList<>();
        @NotNull private List<Method> beforeTestMethods = new ArrayList<>();
        @NotNull private List<Method> testMethods = new ArrayList<>();
        @NotNull private List<Method> afterTestMethods = new ArrayList<>();
        @NotNull private List<Method> afterClassMethods = new ArrayList<>();
        @NotNull private Constructor classDefaultConstructor;
        @NotNull private String className;
        @NotNull private Class<?> clazz;

        private TestClass(@NotNull Constructor classDefaultConstructor, @NotNull Class<?> clazz) {
            this.classDefaultConstructor = classDefaultConstructor;
            className = clazz.getName();
            this.clazz = clazz;
        }
    }

    private static class RunTestResult {
        @NotNull private String methodName;
        @NotNull private TaskResult taskResult;

        private RunTestResult(@NotNull String methodName, @NotNull TaskResult taskResult) {
            this.methodName = methodName;
            this.taskResult = taskResult;
        }
    }

    private static class TaskResult {
        @NotNull private ExecutionResult result;
        @Nullable private String message;

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (other == null || getClass() != other.getClass()) {
                return false;
            }
            TaskResult that = (TaskResult) other;
            return result == that.result;
        }

        private TaskResult(@NotNull ExecutionResult result, @Nullable String message) {
            this.result = result;
            this.message = message;
        }
    }

    private enum ExecutionResult {
        SUCCESS,
        IGNORED,
        FAILED
    }
}
