package spb.hse.smirnov.myjunit.classes;

import spb.hse.smirnov.myjunit.Test;

public class FailingTest {
    @Test
    void fail() {
        int z = 42 / 0;
    }
}
