package ru.spbhse.smirnov.reflector;

import org.jetbrains.annotations.NotNull;
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

    private Writer toTestFileWriter;

    ReflectorTest() throws IOException {
    }

    @Test
    void oneMethodClassPrintTest() throws IOException {
        printStructure(OneMethodClass.class);
        assertTrue(filesAreEqual("src/test/resources/1.out"));
    }

    @Test
    void finalFieldPrintTest() throws IOException {
        printStructure(ClassWithFinalField.class);
        assertTrue(filesAreEqual("src/test/resources/2.out"));
    }

    @Test
    void genericClassPrintTest() throws IOException {
        printStructure(GenericClass.class);
        assertTrue(filesAreEqual("src/test/resources/3.out"));
    }

    @Test
    void innerClassPrintTest() throws IOException {
        printStructure(ClassWithInnerClass.class);
        assertTrue(filesAreEqual("src/test/resources/4.out"));
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

    private boolean filesAreEqual(String path) throws IOException {
        var firstFile = Files.lines(Paths.get(path)).collect(Collectors.toList());
        var secondFile = Files.lines(Paths.get("SomeClass.java")).collect(Collectors.toList());
        return firstFile.equals(secondFile);
    }
}


