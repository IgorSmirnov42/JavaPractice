package ru.spbhse.trie;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class TrieTest {

    private Trie testTrie1;

    @BeforeEach
    private void init() {
        testTrie1 = new Trie();
    }

    @Test
    void addCheckThrows() {
        assertThrows(IllegalArgumentException.class, () -> testTrie1.add(null));
    }

    @Test
    void addAlreadyExisting() {
        testTrie1.add("aa");
        assertFalse(testTrie1.add("aa"));
    }

    @Test
    void addSimpleTests() {
        assertTrue(testTrie1.add("aba"));
        assertEquals(1, testTrie1.size());
        assertTrue(testTrie1.contains("aba"));

        assertTrue(testTrie1.add("baba"));
        assertEquals(2, testTrie1.size());
        assertTrue(testTrie1.contains("baba"));
    }

    @Test
    void containsCheckThrows() {
        assertThrows(IllegalArgumentException.class, () -> testTrie1.contains(null));
    }

    @Test
    void containsFalseTests() {
        assertFalse(testTrie1.contains(""));
        assertFalse(testTrie1.contains("aaa"));

        testTrie1.add("aba");
        assertFalse(testTrie1.contains("ab"));
        assertFalse(testTrie1.contains("abab"));
    }

    @Test
    void containsTrueTests() {
        testTrie1.add("abacaba");
        testTrie1.add("abadabs");
        testTrie1.add("aba");

        assertTrue(testTrie1.contains("aba"));
        assertTrue(testTrie1.contains("abacaba"));
        assertTrue(testTrie1.contains("abadabs"));
    }

    @Test
    void removeCheckThrows() {
        assertThrows(IllegalArgumentException.class, () -> testTrie1.remove(null));
    }

    @Test
    void removeExisting() {
        testTrie1.add("aba");
        assertTrue(testTrie1.remove("aba"));
        assertEquals(0, testTrie1.size());
    }

    @Test
    void removeNotExisting() {
        testTrie1.add("aba");
        assertFalse(testTrie1.remove("ab"));
        assertFalse(testTrie1.remove("abad"));
        assertFalse(testTrie1.remove(""));
    }

    @Test
    void removeCheckDoesNothingIfThereIsNoElement() {
        testTrie1.add("abac");
        testTrie1.add("abad");
        assertFalse(testTrie1.remove("aba"));
        assertTrue(testTrie1.contains("abac"));
        assertTrue(testTrie1.contains("abad"));
    }

    @Test
    void howManyStartsWithPrefixCheckThrows() {
        assertThrows(IllegalArgumentException.class, () -> testTrie1.howManyStartsWithPrefix(null));
    }


    @Test
    void howManyStartsWithPrefixTotalTest() {
        testTrie1.add("abac");
        testTrie1.add("abad");
        assertEquals(2, testTrie1.howManyStartsWithPrefix(""));
        assertEquals(2, testTrie1.howManyStartsWithPrefix("aba"));
        assertEquals(1, testTrie1.howManyStartsWithPrefix("abac"));
        assertEquals(0, testTrie1.howManyStartsWithPrefix("abaac"));

        testTrie1.remove("abac");
        assertEquals(1, testTrie1.howManyStartsWithPrefix(""));
        assertEquals(1, testTrie1.howManyStartsWithPrefix("aba"));
        assertEquals(1, testTrie1.howManyStartsWithPrefix("abad"));
        assertEquals(0, testTrie1.howManyStartsWithPrefix("abac"));
    }

    @Test
    void equalsTotalTest() {
        testTrie1.add("aba");
        testTrie1.add("abeda");

        var testTrie2 = new Trie();
        testTrie2.add("abeda");
        testTrie2.add("aba");

        assertEquals(testTrie1, testTrie2);

        testTrie2.remove("aba");
        testTrie2.add("abe");
        assertNotEquals(testTrie1, testTrie2);

        testTrie1.remove("aba");
        assertNotEquals(testTrie1, testTrie2);

        assertEquals(testTrie2, testTrie2);

        assertNotEquals(testTrie1, null);

        assertNotEquals(testTrie1, "not a trie");
    }

    @Test
    void serializeAndDeserializeTotalTest() throws IOException {
        testTrie1.add("aba");
        testTrie1.add("caba");
        testTrie1.add("abc");

        try (var out = new ByteArrayOutputStream()) {
            testTrie1.serialize(out);

            var testTrie2 = new Trie();

            try (var in = new ByteArrayInputStream(out.toByteArray())) {

                testTrie2.deserialize(in);

                assertEquals(testTrie1, testTrie2);
            }

        }

    }
}