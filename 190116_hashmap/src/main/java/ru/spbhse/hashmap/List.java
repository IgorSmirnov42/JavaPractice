package ru.spbhse.hashmap;

/**
 * Class to store lists of StringPairs
 * NB! StringPairs mustn't be null.
 */
public class List {

    private ListElement head;
    private int size;

    /**
     * Constructs list with 0 elements
     */
    public List() {
        head = new ListElement();
    }

    /**
     * Searches for element with given key in list
     * @return first pair with given key if it exists and null otherwise
     */
    public StringPair get(String key) {
        ListElement ptr = head;
        while (ptr.getElement() != null) {
            if (ptr.getElement().getKey().equals(key)) {
                return ptr.getElement();
            }
            ptr = ptr.getNext();
        }
        return null;
    }

    /**
     * Adds given pair to the head of list
     */
    public void put(StringPair pair) {
        if (pair == null) {
            throw new IllegalArgumentException("pair given to List.put mustn't be null");
        }
        head = new ListElement(new StringPair(pair), head);
        ++size;
    }

    /**
     * Removes first pair with given key from list
     * @return removed pair if it was found and null otherwise
     */
    public StringPair remove(String key) {
        ListElement ptr = head;
        while (ptr.getElement() != null) {
            if (ptr.getElement().getKey().equals(key)) {
                StringPair foundPair = ptr.getElement();
                ptr.setFields(ptr.getNext());
                --size;
                return foundPair;
            }
            ptr = ptr.getNext();
        }
        return null;
    }

    /** Returns number of elements in a list */
    public int size() {
        return size;
    }

    /**
     * Converts list to array
     * @return array made of this list with elements in order from head to tail
     */
    public StringPair[] toArray() {
        StringPair[] content = new StringPair[size];
        ListElement ptr = head;
        for (int i = 0; i < size; i++) {
            content[i] = ptr.getElement();
            ptr = ptr.getNext();
        }
        return content;
    }

    private static class ListElement {

        private StringPair element;
        private ListElement next;

        StringPair getElement() {
            return element;
        }

        ListElement getNext() {
            return next;
        }

        /**
         * Copies fields from other ListElement object
         */
        void setFields(ListElement other) {
            element = other.element;
            next = other.next;
        }

        ListElement(StringPair element, ListElement next) {
            this.element = element;
            this.next = next;
        }

        ListElement() {
        }
    }
}