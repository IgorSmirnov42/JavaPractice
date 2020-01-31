package spb.hse.smirnov.myjunit.classes;

import spb.hse.smirnov.myjunit.Before;
import spb.hse.smirnov.myjunit.Test;

public class ErrorInBefore {
    @Before
    void init() {
        int z = 2 / 0;
    }

    @Test
    void test() {
    }
}
