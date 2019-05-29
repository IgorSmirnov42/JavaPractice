package spb.hse.smirnov.findpair;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FieldTest {

    @Test
    void shouldCreateNormalField() {
        int n = 100;
        var field = new Field(n);
        int maxValue = n * n / 2;
        assertEquals(n, field.getSize());
        var counter = new int[n * n / 2];
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                assertTrue(0 <= field.getNumber(i, j) && field.getNumber(i, j) < maxValue);
                assertTrue(++counter[field.getNumber(i, j)] <= 2);
                assertEquals(CellStatus.CLOSED, field.getStatus(i, j));
            }
        }
    }

}