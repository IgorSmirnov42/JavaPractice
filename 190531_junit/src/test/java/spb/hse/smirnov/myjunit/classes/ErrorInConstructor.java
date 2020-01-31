package spb.hse.smirnov.myjunit.classes;

import spb.hse.smirnov.myjunit.Test;

public class ErrorInConstructor {

    ErrorInConstructor() {
        int z = 2 / 0;
    }

    @Test
    void a() {
    }
}
