package ru.spbhse.trie;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

/**
 * Class realizing data structure Trie to store set of Unicode strings
 * Implemented using Hashtable
 */
public class Trie implements Serializable {
    private int size;
    private HashMap<Character, Trie> nextNode;
    private boolean isTerminal;

    public Trie() {
        nextNode = new HashMap<>();
    }

    /**
     * Adds given element to trie
     * @return true if this element was not presented in a trie, false otherwise
     */
    public boolean add(String element) {
        if (element == null) {
            throw new IllegalArgumentException("Trie.add got null as element. It is forbidden.");
        }
        return addStartFrom(element, 0);
    }

    /**
     * Recursive addition of element to trie
     * @return true if this element was not presented in a trie, false otherwise
     */
    private boolean addStartFrom(String element, int charId) {
        if (charId == element.length()) {

            boolean isNew = !isTerminal;
            if (isNew) {
                ++size;
                isTerminal = true;
            }

            return isNew;
        }

        char currentChar = element.charAt(charId);

        if (!nextNode.containsKey(currentChar)) {
            nextNode.put(currentChar, new Trie());
        }

        boolean isNew = nextNode.get(currentChar).addStartFrom(element, charId + 1);
        if (isNew) {
            ++size;
        }

        return isNew;
    }

    /** Returns true iff element is in trie */
    public boolean contains(String element) {
        if (element == null) {
            throw new IllegalArgumentException("Trie.contains got null as element. It is forbidden.");
        }
        Trie prefixNode = goDownPrefix(element);
        return prefixNode != null && prefixNode.isTerminal;
    }

    /**
     * Removes element from trie
     * @return true iff given element was presented in a trie
     */
    public boolean remove(String element) {
        if (element == null) {
            throw new IllegalArgumentException("Trie.remove got null as element. It is forbidden.");
        }
        return removeStartFrom(element, 0);
    }

    /**
     * Recursive removing of element from trie
     * @return true if this element was presented in a trie, false otherwise
     */
    private boolean removeStartFrom(String element, int charId) {
        if (charId == element.length()) {

            boolean wasInTrie = isTerminal;
            if (wasInTrie) {
                --size;
                isTerminal = false;
            }

            return wasInTrie;
        }

        char currentChar = element.charAt(charId);

        if (!nextNode.containsKey(currentChar)) {
            return false;
        }

        Trie removingNode = nextNode.get(currentChar);
        boolean wasInTrie = removingNode.removeStartFrom(element, charId + 1);
        if (wasInTrie) {
            --size;
            if (removingNode.size == 0) {
                nextNode.put(currentChar, null);
            }
        }

        return wasInTrie;
    }

    /** Returns number of strings in a trie */
    public int size() {
        return size;
    }

    /** Returns number of strings in a trie that start with given prefix */
    public int howManyStartsWithPrefix(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("Trie.howManyStartsWithPrefix got null as prefix. It is forbidden.");
        }
        Trie prefixNode = goDownPrefix(prefix);
        return prefixNode == null ? 0 : prefixNode.size;
    }

    /** Returns Node appropriated to given prefix (and null if it doesn't exist) */
    private Trie goDownPrefix(String prefix) {
        Trie currentNode = this;
        for (char c : prefix.toCharArray()) {

            if (!currentNode.nextNode.containsKey(c)) {
                return null;
            }

            currentNode = currentNode.nextNode.get(c);
        }

        return currentNode;
    }

    @Override
    public void serialize(OutputStream out) throws IOException {

    }

    @Override
    public void deserialize(InputStream in) throws IOException {

    }
}
