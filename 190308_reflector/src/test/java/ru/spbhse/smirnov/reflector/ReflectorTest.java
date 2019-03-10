package ru.spbhse.smirnov.reflector;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.spbhse.smirnov.reflector.testClasses.*;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static ru.spbhse.smirnov.reflector.Reflector.*;

class ReflectorTest {

    private Class<?> someClass;

    private static Writer equalWriter = new Writer() {
        @Override
        public void write(@NotNull char[] cbuf, int off, int len) throws IOException {
            fail();
        }

        @Override
        public void flush() throws IOException {
        }

        @Override
        public void close() throws IOException {
        }
    };

    @BeforeEach
    void init() throws IOException {
        toTestFileWriter = new FileWriter("src/test/resources/test.output");
    }

    @AfterEach
    void closeFile() throws IOException {
        toTestFileWriter.close();
    }

    private Writer toTestFileWriter;

    ReflectorTest() throws IOException {
    }

    @Test
    void oneMethodClassPrintTest() throws IOException {
        printStructure(OneMethodClass.class);
        assertTrue(filesAreEqual("src/test/resources/1.out", "SomeClass.java"));
    }

    @Test
    void finalFieldPrintTest() throws IOException {
        printStructure(ClassWithFinalField.class);
        assertTrue(filesAreEqual("src/test/resources/2.out", "SomeClass.java"));
    }

    @Test
    void genericClassPrintTest() throws IOException {
        printStructure(GenericClass.class);
        assertTrue(filesAreEqual("src/test/resources/3.out", "SomeClass.java"));
    }

    @Test
    void innerClassPrintTest() throws IOException {
        printStructure(ClassWithInnerClass.class);
        assertTrue(filesAreEqual("src/test/resources/4.out", "SomeClass.java"));
    }

    @Test
    void genericFieldsShouldBeEqual() throws IOException {
        diffFields(ClassWithGenericField1.class, ClassWithGenericField2.class, equalWriter);
    }

    @Test
    void classShouldBeEqualToItself() throws IOException {
        diffFields(JustBigClass.class, JustBigClass.class, equalWriter);
        diffMethods(JustBigClass.class, JustBigClass.class, equalWriter);
    }

    @Test
    void compilingTotalTest() throws IOException, ClassNotFoundException {
        printStructure(JustBigClass.class);
        compileFile();
        equalAsDiff(someClass, JustBigClass.class);
    }

    @Test
    void modificatorsMustBeImportant() throws IOException {
        class A {
            int a;
            private void f() {}
        }

        class B {
            private int a;
            void f() {}
        }

        diffFields(A.class, B.class, toTestFileWriter);
        diffMethods(A.class, B.class, toTestFileWriter);
        toTestFileWriter.flush();
        assertTrue(filesAreEqual("src/test/resources/test.output", "src/test/resources/5.out"));
    }

    @Test
    void namesShouldBeImportant() throws IOException {
        class A {
            int a;
            void g() {}
        }

        class B {
            int b;
            void f() {}
        }

        diffFields(A.class, B.class, toTestFileWriter);
        diffMethods(A.class, B.class, toTestFileWriter);
        toTestFileWriter.flush();
        assertTrue(filesAreEqual("src/test/resources/6.out", "src/test/resources/test.output"));
    }

    @Test
    void returnValueShouldBeImportant() throws IOException {
        class A {
            void f() {}
        }

        class B {
            int f() {
                return 1;
            }
        }

        diffMethods(A.class, B.class, toTestFileWriter);
        toTestFileWriter.flush();
        assertTrue(filesAreEqual("src/test/resources/7.out", "src/test/resources/test.output"));
    }

    @Test
    void genericMethodsAreEqualInStrangeWay() throws IOException {
        class A {
            private <T> T justGenericMethod() {
                return null;
            }
        }

        class B {
            private <E> E justGenericMethod() {
                return null;
            }
        }

        diffMethods(A.class, B.class, toTestFileWriter);
        toTestFileWriter.flush();
        assertTrue(filesAreEqual("src/test/resources/8.out", "src/test/resources/test.output"));
    }

    @Test
    void functionParameterNamesDoesNotMatter() throws IOException {
        class A {
            int a;
            void f(int a, int b) {}
        }

        class B {
            int a;
            void f(int b, int c) {}
        }

        diffMethods(A.class, B.class, equalWriter);
    }

    private void equalAsDiff(Class<?> a, Class<?> b) throws IOException {
        equalMethods(a, b);
        equalFields(a, b);
    }

    private void equalMethods(Class<?> a, Class<?> b) throws IOException {
        diffMethods(a, b, equalWriter);
    }

    private void equalFields(Class<?> a, Class<?> b) throws IOException {
        diffFields(a, b, equalWriter);
    }

    private void compileFile() throws MalformedURLException, ClassNotFoundException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        compiler.run(null, null, null, new File("SomeClass.java").getPath());
        var classLoader = URLClassLoader.newInstance(new URL[] {
                new File("").toURI().toURL()
        });
        someClass = Class.forName("SomeClass", false, classLoader);
    }

    private boolean filesAreEqual(String path1, String path2) throws IOException {
        var firstFile = Files.lines(Paths.get(path1)).collect(Collectors.toList());
        var secondFile = Files.lines(Paths.get(path2)).collect(Collectors.toList());
        return firstFile.equals(secondFile);
    }
}