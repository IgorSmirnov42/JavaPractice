package ru.spbhse.smirnov.reflector;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Class that has methods to compare to classes (their methods and fields)
 * and print structure of given class (skeleton of class without implementation)
 */
public class Reflector {
    public static final int NUMBER_OF_SPACES = 4;

    /** Creates file SomeClass.java where skeleton of given someClass is implemented */
    public static void printStructure(@NotNull Class<?> someClass) {
        var classFile = new File("SomeClass.java");
        try (var writer = new FileWriter(classFile)) {
            recursivelyPrintStructure(someClass, writer, 0, "SomeClass");
        } catch (IOException e) {
            System.out.println("Problem with file!");
            e.printStackTrace();
        }

        try {
            String result = Files.lines(Paths.get("SomeClass.java"))
                    .map(s -> s.replaceAll(Pattern.quote(someClass.getName()), "SomeClass"))
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

    /** Prints given class and its inner/nested classes */
    private static void recursivelyPrintStructure(@NotNull Class<?> someClass, Writer writer,
                                                  int indent, String className) throws IOException {
        printClassName(someClass, writer, indent, className);
        ++indent;
        printConstructors(someClass, writer, indent);
        printFields(someClass, writer, indent);
        printInnerAndNestedClasses(someClass, writer, indent);
        printMethods(someClass, writer, indent);
        --indent;
        printEnd(writer, indent);
    }

    /** Prints all constructors of class */
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

    /** Prints all function parameters as it is in function declaration (without brackets) */
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

    /** Prints class name. If class is generic, prints parameterized type too */
    private static void printClassName(@NotNull Class<?> clazz, Writer writer,
                                       int indent, String className) throws IOException {
        printSpaces(writer, indent);
        writer.write(getGenericName(clazz, className) + " {\n");
    }

    private static void printInnerAndNestedClasses(@NotNull Class<?> clazz, Writer writer,
                                       int indent) throws IOException {
        for (Class<?> subclass : clazz.getDeclaredClasses()) {
            recursivelyPrintStructure(subclass, writer, indent, subclass.getSimpleName());
        }
    }

    /** Prints all fields (not synthetic) with default values. Generic fields remain generic */
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

    /** Prints concrete field */
    private static void printField(@NotNull Writer writer, @NotNull Field field) throws IOException {
        printModifiers(field.getModifiers(), writer);
        if (isGeneric(field)) {
            writer.write(field.getGenericType().getTypeName());
        } else {
            writer.write(field.getType().getSimpleName());
        }
        writer.write(" " + field.getName());
    }

    /** Prints all methods of class with default implementation. Generics remain generics */
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

    /** Prints concrete method */
    @SuppressWarnings("Duplicates")
    private static void printMethod(Writer writer, Method method, Class<?> clazz) throws IOException {
        writer.write(leaveModifiersAndType(method.toGenericString(), clazz));
        writer.write(method.getName() + "(");
        Type[] genericParameterTypes = method.getGenericParameterTypes();
        printFunctionParameters(method.getParameters(), genericParameterTypes, writer);
        writer.write(")");
    }

    /** From given method name (as generic string) leaves only modifiers and type */
    private static String leaveModifiersAndType(String methodName, Class<?> clazz) {
        return methodName.split(Pattern.quote(clazz.getName()), 2)[0];
    }

    /** Just prints spaces and closing bracket */
    private static void printEnd(Writer writer,
                                       int indent) throws IOException {
        printSpaces(writer, indent);
        writer.write("}\n");
    }

    /** Generates default value by type and prints it */
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

    /** Checks if field has generic type */
    private static boolean isGeneric(Field field) {
        return !field.getGenericType().getTypeName().equals(field.getType().getTypeName());
    }

    /** Returns generic name of type with all class full names replaced with given class name */
    private static String getGenericName(Class<?> clazz, String className) {
        return clazz.toGenericString().replaceAll(Pattern.quote(clazz.getName()), className);
    }

    /** Prints given number of spaces multiplied by NUMBER_OF_SPACES */
    private static void printSpaces(Writer writer, int indent) throws IOException {
        writer.write(" ".repeat(indent * NUMBER_OF_SPACES));
    }

    /** Prints all modifiers and space if modifiers are presented */
    private static void printModifiers(int modifiers, Writer writer) throws IOException {
        writer.write(Modifier.toString(modifiers) + (modifiers == 0 ? "" : " "));
    }

    /** Prints all differences between methods and fields in given classes */
    public static void diffClasses(@NotNull Class<?> a, @NotNull Class<?> b) {
        try {
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
            diffFields(a, b, writer);
            diffMethods(a, b, writer);
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
    // Package private for testing
    static void diffFields(@NotNull Class<?> a,
                                   @NotNull Class<?> b,
                                   Writer writer) throws IOException {
        var aFields = Arrays.stream(a.getDeclaredFields())
                .filter(f -> !f.isSynthetic())
                .map(FieldInformation::new)
                .collect(Collectors.toList());
        var bFields = Arrays.stream(b.getDeclaredFields())
                .filter(f -> !f.isSynthetic())
                .map(FieldInformation::new)
                .collect(Collectors.toList());

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

    /**
     * Method that prints all differences between methods in classes
     * Methods are same if they have same types and order of arguments, same generic type,
     *      same return value type and same name
     * Methods with different names of generic types are considered different
     */
    @SuppressWarnings("Duplicates")
    // Package private for testing
    static void diffMethods(@NotNull Class<?> a,
                                    @NotNull Class<?> b,
                                    Writer writer) throws IOException {
        var aMethods = Arrays.stream(a.getDeclaredMethods())
                .filter(m -> !m.isSynthetic())
                .map(m -> new MethodInformation(m, a))
                .collect(Collectors.toList());
        var bMethods = Arrays.stream(b.getDeclaredMethods())
                .filter(m -> !m.isSynthetic())
                .map(m -> new MethodInformation(m, b))
                .collect(Collectors.toList());

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

    /** Class to compare methods on equality as described above */
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
                if (!leaveModifiersAndType(method.toGenericString(), clazz)
                        .equals(leaveModifiersAndType(that.method.toGenericString(), that.clazz))) {
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

    /** Class to compare fields on equality as described above */
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