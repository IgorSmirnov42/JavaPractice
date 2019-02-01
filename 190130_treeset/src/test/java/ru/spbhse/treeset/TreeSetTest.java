package ru.spbhse.treeset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class TreeSetTest {

    private TreeSet<Integer> testWithoutComparator;

    @BeforeEach
    private void init() {
        testWithoutComparator = new TreeSet<>();
    }

    @Test
    void iterator() {
    }

    @Test
    void size() {
    }

    @Test
    void descendingIterator() {
    }

    @Test
    void descendingSet() {
    }

    @Test
    void contains() {
    }

    @Test
    void manyAdds() {
        for (int i = 0; i < 20; i++) {
            assertTrue(testWithoutComparator.add(i));
        }
        for (int i = -1; i >= -20; i--) {
            assertTrue(testWithoutComparator.add(i));
        }
        for (int i = 20; i < 40; i++) {
            assertTrue(testWithoutComparator.add(i));
        }
        for (int i = -20; i < 40; i++) {
            assertTrue(testWithoutComparator.contains(i));
        }
    }

    @Test
    void add() {
        assertTrue(testWithoutComparator.add(42));
        assertTrue(testWithoutComparator.contains(42));
        assertTrue(testWithoutComparator.add(21));
        assertTrue(testWithoutComparator.contains(21));
        assertTrue(testWithoutComparator.add(239));
        assertTrue(testWithoutComparator.contains(239));
        // TODO
    }

    @Test
    void first() {
        testWithoutComparator.add(1337);
        testWithoutComparator.add(42);
        testWithoutComparator.add(239);
        assertEquals(42, testWithoutComparator.first());
    }

    @Test
    void last() {
        testWithoutComparator.add(1337);
        testWithoutComparator.add(42);
        testWithoutComparator.add(239);
        assertEquals(1337, testWithoutComparator.last());
    }

    @Test
    void lower() {
        for (int i = 0; i < 20; ++i) {
            testWithoutComparator.add(i);
        }
        assertEquals(1, testWithoutComparator.lower(2));
        assertEquals(0, testWithoutComparator.lower(1));
        assertEquals(19, testWithoutComparator.lower(42));
        assertNull(testWithoutComparator.lower(0));
        assertNull(testWithoutComparator.lower(-42));
    }

    @Test
    void floor() {
        for (int i = 0; i < 20; ++i) {
            testWithoutComparator.add(i);
        }
        assertEquals(0, testWithoutComparator.floor(0));
        assertEquals(1, testWithoutComparator.floor(1));
        assertEquals(19, testWithoutComparator.floor(42));
        assertNull(testWithoutComparator.floor(-1));
        assertNull(testWithoutComparator.floor(-42));
    }

    @Test
    void ceiling() {
        for (int i = 0; i < 20; ++i) {
            testWithoutComparator.add(i);
        }
        assertEquals(0, testWithoutComparator.ceiling(0));
        assertEquals(19, testWithoutComparator.ceiling(19));
        assertEquals(0, testWithoutComparator.ceiling(-42));
        assertNull(testWithoutComparator.ceiling(42));
        assertNull(testWithoutComparator.ceiling(20));
    }

    @Test
    void higher() {
        for (int i = 0; i < 20; ++i) {
            testWithoutComparator.add(i);
        }
        assertEquals(1, testWithoutComparator.higher(0));
        assertNull(testWithoutComparator.higher(19));
        assertEquals(0, testWithoutComparator.higher(-42));
        assertNull(testWithoutComparator.higher(42));
        assertNull(testWithoutComparator.higher(20));
    }

    @Test
    void unsupportedClassShouldThrow() {
        var test = new TreeSet<ClassWithoutComparator>();
        test.add(new ClassWithoutComparator(42));
        assertThrows(ClassCastException.class, () -> test.add(new ClassWithoutComparator(23)));
        assertThrows(ClassCastException.class, () -> test.contains(new ClassWithoutComparator(42)));
    }

    private static class ClassWithoutComparator {
        private int x;
        private ClassWithoutComparator(int x) {
            this.x = x;
        }
    }
}