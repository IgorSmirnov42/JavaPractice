package ru.spbhse.smirnov.reflector.testClasses;

public class GenericClass<E> {
    E a;
    private <T> E genericMethod(T a) {
        return null;
    }
}
