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

    @Test
    void shouldShoot() {
        var field = new Field(2);
        field.hit(0, 0);
        assertEquals(CellStatus.OPEN, field.getStatus(0, 0));
    }

    @Test
    void shouldKill() {
        var field = new Field(2);
        int ff = field.getNumber(0, 0);
        field.hit(0, 0);
        for (int i = 0; i < 2; ++i) {
            for (int j = 0; j < 2; ++j) {
                if (i != 0 || j != 0) {
                    if (field.getNumber(i, j) == ff) {
                        field.hit(351 * i, 351 * j);
                    }
                }
            }
        }
        assertEquals(CellStatus.FOREVER_OPEN, field.getStatus(0, 0));
    }
}