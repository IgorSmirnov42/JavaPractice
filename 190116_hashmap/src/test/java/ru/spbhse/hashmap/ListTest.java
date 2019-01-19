package ru.spbhse.hashmap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ListTest {

    private List test;

    @BeforeEach
    void init() {
        test = new List();
    }

    @Test
    public void getNotExist() {
        assertNull(test.get("a"));
    }

    @Test
    public void getExist() {
        var a = new StringPair("a", "b");
        var b = new StringPair("b", "c");
        test.put(a);
        test.put(b);

        var got = test.remove("a");
        assertEquals(a.getKey(), got.getKey());
        assertEquals(a.getValue(), got.getValue());

        got = test.remove("b");
        assertEquals(b.getKey(), got.getKey());
        assertEquals(b.getValue(), got.getValue());
    }

    @Test
    public void putTotalTest() {
        var a = new StringPair("a", "b");
        var b = new StringPair("b", "c");
        test.put(a);
        assertEquals(1, test.size());
        test.put(b);
        assertEquals(2, test.size());
    }

    @Test
    public void putCheckThrows() {
        assertThrows(IllegalArgumentException.class, () -> test.put(null));
    }

    @Test
    public void removeExist() {
        var a = new StringPair("a", "b");
        var b = new StringPair("b", "c");
        test.put(a);
        test.put(b);

        var removed = test.remove("a");
        assertEquals(a.getKey(), removed.getKey());
        assertEquals(a.getValue(), removed.getValue());

        removed = test.remove("b");
        assertEquals(b.getKey(), removed.getKey());
        assertEquals(b.getValue(), removed.getValue());
    }

    @Test
    public void removeNotExist() {
        assertNull(test.remove("Ab"));
        assertNull(test.remove(null));
    }

    @Test
    public void toArrayTotalTest() {
        StringPair[] array = {
                new StringPair("a", "b"),
                new StringPair("b", "c"),
                new StringPair("d", "e"),
                new StringPair("e", "f"),
                new StringPair("f", "g")
        };
        int n = array.length;

        for (StringPair element : array) {
            test.put(element);
        }

        StringPair[] generated = test.toArray();
        for (int i = 0; i < n; i++) {
            assertEquals(array[i].getKey(), generated[n - i - 1].getKey());
            assertEquals(array[i].getValue(), generated[n - i - 1].getValue());
        }
    }

    @Test
    public void sizeTotalTest() {
        assertEquals(0, test.size());

        test.put(new StringPair("aa", "bb"));
        assertEquals(1, test.size());

        test.put(new StringPair("bb", "cc"));
        assertEquals(2, test.size());
    }
}