package spb.hse.smirnov.myjunit.classes;

import spb.hse.smirnov.myjunit.Test;

public class AnotherExpected {
    @Test(expected = IllegalArgumentException.class)
    void test1() throws Exception {
        throw new Exception();
    }
}
