package spb.hse.smirnov.myjunit.classes;

import spb.hse.smirnov.myjunit.Test;

public class ExpectedTests {
    @Test(expected = IllegalArgumentException.class)
    void test1() {
        throw new IllegalArgumentException();
    }

    @Test(expected = IllegalArgumentException.class)
    void test2() {
    }
}
