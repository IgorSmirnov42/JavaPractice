package ru.spbhse.smirnov.reflector;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Reflector<E>  {

    E hh;
    gg tt;

    private static final class gg<T extends Class> {
        T h;
        boolean gj;

        private class THH {
            int vv = 9;
            public THH(int x) {

            }
        }

        private <T> T tt(Reflector<Throwable> gg) {
            return null;
        }
    }

    public static final int NUMBER_OF_SPACES = 4;

    public static void main(String[] args) throws IOException {
        printStructure(Reflector.class);
        diffClasses(A.class, B.class);
    }

    public static void printStructure(@NotNull Class<?> someClass) {
        var classFile = new File("someClass.java");
        try (var writer = new FileWriter(classFile)) {
            importPackage(someClass, writer);
            recursivelyPrintStructure(someClass, writer, 0, "SomeClass");
        } catch (IOException e) {
            System.out.println("Problem with file!");
            e.printStackTrace();
        }

        try {
            String result = Files.lines(Paths.get("someClass.java"))
                    .map(s -> s.replaceAll(Pattern.quote(someClass.getName()), "someClass"))
                    .map(s -> s.replaceAll("[^\\s(]*\\$", ""))
                    .collect(Collectors.joining("\n"));
            try (var writer = new FileWriter(classFile)) {
                writer.write(result);
            }
        } catch (IOException e) {
            System.out.println("Problem with file!");
            e.printStackTrace();
        }

    }

    private static void recursivelyPrintStructure(@NotNull Class<?> someClass, Writer writer,
                                                  int indent, String className) throws IOException {
        printClassName(someClass, writer, indent, className);
        ++indent;
        printConstructors(someClass, writer, indent);
        printFields(someClass, writer, indent);
        printInnerAndNestedClasses(someClass, writer, indent);
        printMethods(someClass, writer, indent);
        --indent;
        printEnd(someClass, writer, indent);
    }

    @SuppressWarnings("Duplicates")
    private static void printConstructors(Class<?> clazz, Writer writer, int indent) throws IOException {
        for (Constructor constructor : clazz.getConstructors()) {
            if (constructor.isSynthetic()) {
                continue;
            }
            printSpaces(writer, indent);
            writer.write(leaveModifiersAndType(constructor.toGenericString(), clazz));
            writer.write(constructor.getName() + "(");
            Type[] genericParameterTypes = constructor.getGenericParameterTypes();
            printFunctionParameters(constructor.getParameters(), genericParameterTypes, writer);
            writer.write(") {\n");
            printSpaces(writer, indent);
            writer.write("}\n");
        }
    }

    private static void printFunctionParameters(Parameter[] parameters,
                                                Type[] genericParameterTypes,
                                                Writer writer) throws IOException {
        boolean printedSomething = false;
        for (int parameterId = 0; parameterId < genericParameterTypes.length; parameterId++) {
            if (printedSomething) {
                writer.write(", ");
            }
            if (parameters[parameterId].isSynthetic()) {
                continue;
            }
            printedSomething = true;
            writer.write(genericParameterTypes[parameterId].getTypeName() + " ");
            writer.write("arg" + parameterId);
        }

    }

    private static void importPackage(@NotNull Class<?> clazz, Writer writer) throws IOException {
        writer.write("import " + clazz.getPackageName() + ";\n\n");
    }

    private static void printClassName(@NotNull Class<?> clazz, Writer writer,
                                       int indent, String className) throws IOException {
        printSpaces(writer, indent);
        // printModifiers(clazz.getModifiers(), writer);
        writer.write(getGenericName(clazz, className) + " {\n");
    }

    private static void printInnerAndNestedClasses(@NotNull Class<?> clazz, Writer writer,
                                       int indent) throws IOException {
        for (Class<?> subclass : clazz.getDeclaredClasses()) {
            recursivelyPrintStructure(subclass, writer, indent, subclass.getSimpleName());
        }
    }

    private static void printFields(@NotNull Class<?> clazz, Writer writer,
                                       int indent) throws IOException {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isSynthetic()) {
                continue;
            }
            printSpaces(writer, indent);
            printField(writer, field);
            writer.write(" = ");
            printDefaultValue(field.getType(), writer);
            writer.write(";\n");
        }
    }

    private static void printField(@NotNull Writer writer, @NotNull Field field) throws IOException {
        printModifiers(field.getModifiers(), writer);
        if (isGeneric(field)) {
            writer.write(field.getGenericType().getTypeName());
        } else {
            writer.write(field.getType().getSimpleName());
        }
        writer.write(" " + field.getName());
    }

    @SuppressWarnings("Duplicates")
    private static void printMethods(@NotNull Class<?> clazz, Writer writer,
                                       int indent) throws IOException {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isSynthetic()) {
                continue;
            }
            printSpaces(writer, indent);
            printMethod(writer, method, clazz);
            writer.write(" {\n");
            if (method.getReturnType() != void.class) {
                printSpaces(writer, indent + 1);
                writer.write("return ");
                printDefaultValue(method.getReturnType(), writer);
                writer.write(";\n");
            }
            printSpaces(writer, indent);
            writer.write("}\n");
        }
    }

    @SuppressWarnings("Duplicates")
    private static void printMethod(Writer writer, Method method, Class<?> clazz) throws IOException {
        writer.write(leaveModifiersAndType(method.toGenericString(), clazz));
        writer.write(method.getName() + "(");
        Type[] genericParameterTypes = method.getGenericParameterTypes();
        printFunctionParameters(method.getParameters(), genericParameterTypes, writer);
        writer.write(")");
    }

    private static String leaveModifiersAndType(String methodName, Class<?> clazz) {
        return methodName.split(Pattern.quote(clazz.getName()), 2)[0];
    }

    private static void printEnd(@NotNull Class<?> clazz, Writer writer,
                                       int indent) throws IOException {
        printSpaces(writer, indent);
        writer.write("}\n");
    }

    private static void printDefaultValue(Class<?> type, Writer writer) throws IOException {
        if (!type.isPrimitive()) {
            writer.write("null");
        } else {
            if (type != char.class) {
                writer.write(Array.get(Array.newInstance(type, 1), 0).toString());
            } else {
                writer.write("0");
            }
        }
    }

    private static boolean isGeneric(Field field) {
        return !field.getGenericType().getTypeName().equals(field.getType().getTypeName());
    }

    private static String getGenericName(Class<?> clazz, String className) {
        return clazz.toGenericString().replaceAll(Pattern.quote(clazz.getName()), className);
    }

    private static void printSpaces(Writer writer, int indent) throws IOException {
        writer.write(" ".repeat(indent * NUMBER_OF_SPACES));
    }

    private static void printModifiers(int modifiers, Writer writer) throws IOException {
        writer.write(Modifier.toString(modifiers) + (modifiers == 0 ? "" : " "));
    }

    public static void diffClasses(@NotNull Class<?> a, @NotNull Class<?> b) {
        try {
            diffFields(a, b);
            diffMethods(a, b);
        } catch (IOException e) {
            System.out.println("IO problem!");
            e.printStackTrace();
        }
    }

    /**
     * Prints all fields that are contained only in one of given classes
     * Fields are same if they have same modifiers, same name and same type (generics are equal despite bounds)
     */
    @SuppressWarnings("Duplicates")
    private static void diffFields(@NotNull Class<?> a, @NotNull Class<?> b) throws IOException {
        var aFields = Arrays.stream(a.getDeclaredFields())
                .filter(f -> !f.isSynthetic())
                .map(FieldInformation::new)
                .collect(Collectors.toList());
        var bFields = Arrays.stream(b.getDeclaredFields())
                .filter(f -> !f.isSynthetic())
                .map(FieldInformation::new)
                .collect(Collectors.toList());

        var writer = new Writer() {
            @Override
            public void write(@NotNull char[] cbuf, int off, int len) throws IOException {
                var result = new StringBuilder();
                for (int position = off; position < off + len; ++position) {
                    result.append(cbuf[position]);
                }
                System.out.print(result);
            }

            @Override
            public void flush() throws IOException {}

            @Override
            public void close() throws IOException {}
        };

        for (FieldInformation field : aFields) {
            if (!bFields.contains(field)) {
                printField(writer, field.field);
                System.out.println();
            }
        }

        for (FieldInformation field : bFields) {
            if (!aFields.contains(field)) {
                printField(writer, field.field);
                System.out.println();
            }
        }
    }

    @SuppressWarnings("Duplicates")
    private static void diffMethods(@NotNull Class<?> a, @NotNull Class<?> b) throws IOException {
        var aMethods = Arrays.stream(a.getDeclaredMethods())
                .filter(m -> !m.isSynthetic())
                .map(m -> new MethodInformation(m, a))
                .collect(Collectors.toList());
        var bMethods = Arrays.stream(b.getDeclaredMethods())
                .filter(m -> !m.isSynthetic())
                .map(m -> new MethodInformation(m, b))
                .collect(Collectors.toList());

        var writer = new Writer() {
            @Override
            public void write(@NotNull char[] cbuf, int off, int len) throws IOException {
                var result = new StringBuilder();
                for (int position = off; position < off + len; ++position) {
                    result.append(cbuf[position]);
                }
                System.out.print(result);
            }

            @Override
            public void flush() throws IOException {}

            @Override
            public void close() throws IOException {}
        };

        for (MethodInformation method : aMethods) {
            if (!bMethods.contains(method)) {
                printMethod(writer, method.method, method.clazz);
                System.out.println();
            }
        }

        for (MethodInformation method : bMethods) {
            if (!aMethods.contains(method)) {
                printMethod(writer, method.method, method.clazz);
                System.out.println();
            }
        }
    }

    private static class MethodInformation {
        private Method method;
        private Class<?> clazz;

        private MethodInformation(Method method, Class<?> clazz) {
            this.method = method;
            this.clazz = clazz;
        }

        @Override
        public boolean equals(Object other) {
            if (other == null) {
                return false;
            }
            if (other instanceof MethodInformation) {
                var that = (MethodInformation) other;
                if (that.method.getModifiers() != method.getModifiers()) {
                    return false;
                }
                if (!leaveModifiersAndType(method.getName(), clazz)
                        .equals(leaveModifiersAndType(that.method.getName(), that.clazz))) {
                    return false;
                }
                if (!Arrays.equals(method.getParameterTypes(),
                        that.method.getParameterTypes())) {
                    return false;
                }
                return method.getName().equals(that.method.getName());
            }
            return false;
        }
    }

    private static class FieldInformation {
        private Field field;

        private FieldInformation(Field field) {
            this.field = field;
        }

        @Override
        public boolean equals(Object other) {
            if (other == null) {
                return false;
            }
            if (other instanceof FieldInformation) {
                var that = (FieldInformation) other;
                if (!that.field.getName().equals(field.getName())) {
                    return false;
                }
                if (isGeneric(field) ^ isGeneric(that.field)) {
                    return false;
                }
                if (isGeneric(field) && isGeneric(that.field)) {
                    return true;
                }
                if (field.getModifiers() != that.field.getModifiers()) {
                    return false;
                }
                return field.getType() == that.field.getType();
            }
            return false;
        }
    }
}

class A<T> {
    T a;
    int gg(int hh) {
        return 0;
    }

}

class B<E> {
    E a;
    int gg(int jj) {
        return 0;
    }
}