package ru.spbhse.smirnov.reflector.testClasses;

public class JustBigClass<E> {
    final static int a = 239;
    private Integer b;
    final E c = null;
    int[] d;
    public Character e;

    private int f(int a, int[] b) {
        return 1;
    }

    private Character g() {
        return 'c';
    }

    private static <T> T h(T a) {
        return null;
    }
}
