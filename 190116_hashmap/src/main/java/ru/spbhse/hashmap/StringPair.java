package ru.spbhse.hashmap;

/**
 * Class to store pairs of Strings. First element named key, second value
 */
public class StringPair {

    private String key;
    private String value;

    String getKey() {
        return key;
    }

    String getValue() {
        return value;
    }

    void setValue(String value) {
        this.value = value;
    }

    StringPair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    StringPair(StringPair other) {
        key = other.key;
        value = other.value;
    }
}
