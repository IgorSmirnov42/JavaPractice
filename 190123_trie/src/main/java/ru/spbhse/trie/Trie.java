package ru.spbhse.trie;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Class realizing data structure Trie to store set of Unicode strings
 * Implemented using Hashtable
 */
public class Trie implements Serializable {
    private int size;

    /**
     * Adds given element to trie
     * @return true if this element was not presented in a trie, false otherwise
     */
    public boolean add(String element) {
        return false;
    }

    /** Returns true iff element is in trie */
    public boolean contains(String element) {
        return false;
    }

    /**
     * Removes element from trie
     * @return true iff given element was presented in a trie
     */
    public boolean remove(String element) {
        return false;
    }

    /** Returns number of strings in a trie */
    public int size() {
        return 0;
    }

    /** Returns number of strings in a trie that start with given prefix */
    public int howManyStartsWithPrefix(String prefix) {
        return 0;
    }

    @Override
    public void serialize(OutputStream out) throws IOException {

    }

    @Override
    public void deserialize(InputStream in) throws IOException {

    }
}
