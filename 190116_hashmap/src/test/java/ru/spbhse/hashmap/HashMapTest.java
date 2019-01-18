package ru.spbhse.hashmap;

import org.testng.annotations.Test;

import static org.junit.Assert.*;

public class HashMapTest {

    @Test
    public void sizeTotalTest() {
        HashMap table = new HashMap();

        assertEquals(0, table.size());

        table.put("aba", null);
        assertEquals(1, table.size());

        table.put("caba", null);
        assertEquals(2, table.size());

        table.put("aba", null);
        assertEquals(2, table.size());

        table.clear();
        assertEquals(0, table.size());
    }

    @Test
    public void containsShouldBeTrue() {
        HashMap test = new HashMap();

        test.put("aa", null);
        assertTrue(test.contains("aa"));

        test.put("aba", null);
        assertTrue(test.contains("aba"));
    }

    @Test
    public void containsShouldBeFalse() {
        HashMap test = new HashMap();

        assertFalse(test.contains("aa"));

        test.put("aa", null);
        assertFalse(test.contains("ab"));
    }

    @Test
    public void getTotalTest() {
        HashMap test = new HashMap();

        test.put("aa", "bb");
        assertEquals("bb", test.get("aa"));

        assertNull(test.get("ab"));
    }

    @Test
    public void putSimpleTests() {
        HashMap test = new HashMap();

        assertNull(test.put("aa", "bb"));
        assertEquals(1, test.size());

        assertNull(test.put("ab", "cc"));
        assertEquals(2, test.size());
    }

    @Test
    public void putSameKeys() {
        HashMap test = new HashMap();

        assertNull(test.put("aa", "bb"));
        assertEquals("bb", test.get("aa"));

        assertEquals("bb", test.put("aa", "cc"));
        assertEquals(1, test.size());
        assertEquals("cc", test.get("aa"));
    }

    @Test
    public void removeSimpleTests() {
        HashMap test = new HashMap();
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
        HashMap test = new HashMap();

        test.put("aa", "bb");
        assertEquals("bb", test.remove("aa"));
        assertNull(test.remove("aa"));

        assertNull(test.remove("gg"));
    }

    @Test
    public void clearTest() {
        HashMap test = new HashMap();

        test.put("aa", "bb");
        test.put("ab", "cd");

        test.clear();
        assertEquals(0, test.size());
    }

    @Test
    public void removeSameHashCode() {
        HashMap test = new HashMap();

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
        HashMap test = new HashMap();

        assertNull(test.put("FB", "aa"));
        assertNull(test.put("Ea", "bb"));
        assertEquals(2, test.size());
    }

    @Test
    public void getSameHashCode() {
        HashMap test = new HashMap();

        test.put("FB", "aa");
        test.put("Ea", "bb");

        assertEquals("aa", test.get("FB"));
        assertEquals("bb", test.get("Ea"));
    }

    @Test
    public void manyPuts() {
        // Need to test reallocate
        HashMap test = new HashMap();
        for (Integer i = 0; i < 100; i++) {
            test.put(i.toString(), null);
        }
        assertEquals(100, test.size());
    }
}