package ru.spbhse.hashmap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HashMapTest {

    private HashMap test;

    @BeforeEach
    void init() {
        test = new HashMap();
    }

    @Test
    public void sizeTotalTest() {
        assertEquals(0, test.size());

        test.put("aba", null);
        assertEquals(1, test.size());

        test.put("caba", null);
        assertEquals(2, test.size());

        test.put("aba", null);
        assertEquals(2, test.size());

        test.clear();
        assertEquals(0, test.size());
    }

    @Test
    public void containsShouldBeTrueWhenElementContains() {
        test.put("aa", null);
        assertTrue(test.contains("aa"));

        test.put("aba", null);
        assertTrue(test.contains("aba"));
    }

    @Test
    public void containsCheckThrows() {
        assertThrows(IllegalArgumentException.class, () -> test.contains(null));
    }

    @Test
    public void containsShouldBeFalseWhenElementNotContains() {
        assertFalse(test.contains("aa"));

        test.put("aa", null);
        assertFalse(test.contains("ab"));
    }

    @Test
    public void getCheckThrows() {
        assertThrows(IllegalArgumentException.class, () -> test.get(null));
    }

    @Test
    public void getTotalTest() {
        test.put("aa", "bb");
        assertEquals("bb", test.get("aa"));

        assertNull(test.get("ab"));
    }

    @Test
    public void putCheckThrows() {
        assertThrows(IllegalArgumentException.class, () -> test.put(null, "Aa"));
    }

    @Test
    public void putSimpleTests() {
        assertNull(test.put("aa", "bb"));
        assertEquals(1, test.size());

        assertNull(test.put("ab", "cc"));
        assertEquals(2, test.size());
    }

    @Test
    public void putSameKeys() {
        assertNull(test.put("aa", "bb"));
        assertEquals("bb", test.get("aa"));

        assertEquals("bb", test.put("aa", "cc"));
        assertEquals(1, test.size());
        assertEquals("cc", test.get("aa"));
    }

    @Test
    public void removeCheckThrows() {
        assertThrows(IllegalArgumentException.class, () -> test.remove(null));
    }

    @Test
    public void removeSimpleTests() {
        test.put("aa", "bb");
        test.put("ab", "bc");

        assertEquals("bb", test.remove("aa"));
        assertEquals(1, test.size());
        assertNull(test.get("aa"));

        assertEquals("bc", test.remove("ab"));
        assertEquals(0, test.size());
        assertNull(test.get("ab"));
    }

    @Test
    public void removeNotExisting() {
        test.put("aa", "bb");
        assertEquals("bb", test.remove("aa"));
        assertNull(test.remove("aa"));

        assertNull(test.remove("gg"));
    }

    @Test
    public void clearTest() {
        test.put("aa", "bb");
        test.put("ab", "cd");

        test.clear();
        assertEquals(0, test.size());
    }

    @Test
    public void removeSameHashCode() {
        test.put("FB", "aa");
        test.put("Ea", "bb");

        assertEquals("bb", test.remove("Ea"));
        assertEquals("aa", test.remove("FB"));

        test.put("Ea", "bb");
        test.put("FB", "aa");

        assertEquals("bb", test.remove("Ea"));
        assertEquals("aa", test.remove("FB"));
    }

    @Test
    public void putSameHashCode() {
        assertNull(test.put("FB", "aa"));
        assertNull(test.put("Ea", "bb"));
        assertEquals(2, test.size());
    }

    @Test
    public void getSameHashCode() {
        test.put("FB", "aa");
        test.put("Ea", "bb");

        assertEquals("aa", test.get("FB"));
        assertEquals("bb", test.get("Ea"));
    }

    @Test
    public void manyPuts() {
        // Need to test reallocate
        for (Integer i = 0; i < 100; i++) {
            test.put(i.toString(), null);
        }
        assertEquals(100, test.size());
    }
}