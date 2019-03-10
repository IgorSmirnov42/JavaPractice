package ru.spbhse.smirnov.reflector.testClasses;

public class ClassWithInnerClass {
    Integer b;
    private ClassWithInnerClass() {
    }
    private class Inner {
        Integer a;
        Inner() {
        }
        public int innerFunction() {
            return 0;
        }
    }
}
