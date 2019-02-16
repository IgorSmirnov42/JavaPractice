package ru.spbhse.smirnov.database;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testng.collections.Lists;

import java.sql.SQLException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class PhoneDatabaseTest {

    private PhoneDatabase testDatabase;

    @BeforeEach
    void init() throws SQLException {
        testDatabase = new PhoneDatabase("test");
        testDatabase.clear();
    }

    @Test
    void getAllNamePhonePairsTotalTest() throws SQLException {
        assertEquals(Lists.newArrayList(), testDatabase.getAllNamePhonePairs());
        testDatabase.addRecord("a", "b");
        testDatabase.addRecord("c", "d");
        assertEquals(Lists.newArrayList(new NamePhonePair("a", "b"),
                new NamePhonePair("c", "d")),
                testDatabase.getAllNamePhonePairs());
    }

    @Test
    void getAllNamesByPhoneShouldThrowOnNull() {
        assertThrows(IllegalArgumentException.class, () -> testDatabase.getAllNamesByPhone(null));
    }

    @Test
    void getAllNamesByPhoneTotalTest() throws SQLException {
        testDatabase.addRecord("a", "b");
        testDatabase.addRecord("c", "d");
        testDatabase.addRecord("r", "b");
        assertEquals(Lists.newArrayList("a", "r"),
                testDatabase.getAllNamesByPhone("b"));
        assertEquals(Lists.newArrayList(), testDatabase.getAllNamesByPhone("аа"));
        assertEquals(Lists.newArrayList("c"),
                testDatabase.getAllNamesByPhone("d"));
    }

    @Test
    void getAllPhonesByNameShouldThrowOnNull() {
        assertThrows(IllegalArgumentException.class, () -> testDatabase.getAllPhonesByName(null));
    }

    @Test
    void getAllPhonesByNameTotalTest() throws SQLException {
        testDatabase.addRecord("a", "b");
        testDatabase.addRecord("c", "d");
        testDatabase.addRecord("a", "r");
        assertEquals(Lists.newArrayList("b", "r"),
                testDatabase.getAllPhonesByName("a"));
        assertEquals(Lists.newArrayList(), testDatabase.getAllPhonesByName("аа"));
        assertEquals(Lists.newArrayList("d"),
                testDatabase.getAllPhonesByName("c"));
    }

    @Test
    void addRecordShouldThrowOnNull() {
        assertThrows(IllegalArgumentException.class, () -> testDatabase.addRecord(null, "a"));
        assertThrows(IllegalArgumentException.class, () -> testDatabase.addRecord("a", null));
    }

    @Test
    void addRecordSimpleTest() throws SQLException {
        testDatabase.addRecord("Пожар", "01");
        assertEquals(1, testDatabase.getAllNamePhonePairs().size());
        testDatabase.addRecord("Грабят", "02");
        assertEquals(2, testDatabase.getAllNamePhonePairs().size());
        testDatabase.addRecord("Болезнь", "03");
        assertEquals(3, testDatabase.getAllNamePhonePairs().size());
    }

    @Test
    void addShouldNotDuplicate() throws SQLException {
        testDatabase.addRecord("Проще позвонить чем у кого-то занимать",
                "8 (800) 555-35-35");
        testDatabase.addRecord("Проще позвонить чем у кого-то занимать",
                "8 (800) 555-35-35");
        assertEquals(1, testDatabase.getAllNamePhonePairs().size());
    }

    @Test
    void deleteRecordShouldThrowOnNull() {
        assertThrows(IllegalArgumentException.class, () -> testDatabase.deleteRecord(null, "a"));
        assertThrows(IllegalArgumentException.class, () -> testDatabase.deleteRecord("a", null));
    }

    @Test
    void deleteRecordTotalTest() throws SQLException {
        testDatabase.deleteRecord("a", "b");
        assertEquals(0, testDatabase.getAllNamePhonePairs().size());
        testDatabase.addRecord("a", "b");
        testDatabase.addRecord("b", "c");
        testDatabase.deleteRecord("c", "d");
        assertEquals(2, testDatabase.getAllNamePhonePairs().size());
        testDatabase.deleteRecord("a", "b");
        assertEquals(1, testDatabase.getAllNamePhonePairs().size());
        testDatabase.deleteRecord("a", "b");
        assertEquals(1, testDatabase.getAllNamePhonePairs().size());
        testDatabase.deleteRecord("b", "c");
        assertEquals(0, testDatabase.getAllNamePhonePairs().size());
    }

    @Test
    void replaceNameByPairShouldThrowOnNull() {
        assertThrows(IllegalArgumentException.class, () -> testDatabase.replaceNameByPair(null, "a", "b"));
        assertThrows(IllegalArgumentException.class, () -> testDatabase.replaceNameByPair("a", null, "b"));
        assertThrows(IllegalArgumentException.class, () -> testDatabase.replaceNameByPair("a", "b", null));
    }

    @Test
    void replaceNameByPairSimpleTests() throws SQLException {
        testDatabase.addRecord("a", "b");
        testDatabase.replaceNameByPair("a", "b", "c");
        assertEquals("c", testDatabase.getAllNamesByPhone("b").get(0));
        assertEquals(1, testDatabase.getAllNamePhonePairs().size());
    }

    @Test
    void replaceNameByPairShouldNotDuplicate() throws SQLException {
        testDatabase.addRecord("a", "b");
        testDatabase.addRecord("c", "b");
        testDatabase.replaceNameByPair("a", "b", "c");
        assertEquals(1, testDatabase.getAllNamePhonePairs().size());
    }

    @Test
    void replacePhoneByPairShouldThrowOnNull() {
        assertThrows(IllegalArgumentException.class, () -> testDatabase.replacePhoneByPair(null, "a", "b"));
        assertThrows(IllegalArgumentException.class, () -> testDatabase.replacePhoneByPair("a", null, "b"));
        assertThrows(IllegalArgumentException.class, () -> testDatabase.replacePhoneByPair("a", "b", null));
    }

    @Test
    void replacePhoneByPairSimpleTests() throws SQLException {
        testDatabase.addRecord("a", "b");
        testDatabase.replacePhoneByPair("a", "b", "c");
        assertEquals("c", testDatabase.getAllPhonesByName("a").get(0));
        assertEquals(1, testDatabase.getAllNamePhonePairs().size());
    }

    @Test
    void replacePhoneByPairShouldNotDuplicate() throws SQLException {
        testDatabase.addRecord("a", "b");
        testDatabase.addRecord("a", "d");
        testDatabase.replacePhoneByPair("a", "d", "b");
        assertEquals(1, testDatabase.getAllNamePhonePairs().size());
    }

    @Test
    void clearTotalTest() throws SQLException {
        testDatabase.addRecord("a", "b");
        testDatabase.clear();
        assertEquals(0, testDatabase.getAllNamePhonePairs().size());
    }
}