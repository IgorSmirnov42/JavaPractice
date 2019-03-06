package spb.hse.smirnov.cw2.testClasses;

public class CounterClass {
    public static int counter = 0;
    public CounterClass() {
        counter++;
    }
    public static void clear() {
        counter = 0;
    }
}
