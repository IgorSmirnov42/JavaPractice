package ru.spbhse.hashmap;

/**
 * HashMap class to store pairs of Strings
 * NB! Keys mustn't be nulls
 */
public class HashMap {

    private int size;
    private List[] buckets;
    final private int DEFAULT_SIZE = 3;
    final private int ACCEPTABLE_DIFF = 2;
    final private int RESIZE_TIMES = 2;

    /**
     * Basic constructor. Creates HashMap with DEFAULT_SIZE buckets
     */
    public HashMap() {
        size = 0;
        buckets = new List[DEFAULT_SIZE];
        for (int bucketId = 0; bucketId < DEFAULT_SIZE; bucketId++) {
            buckets[bucketId] = new List();
        }
    }

    /**
     * @return number of elements in HashMap
     */
    public int size() {
        return size;
    }

    /**
     * Checks if HashMap contains given key
     * @param key
     * @return true if HashMap contains this key and false otherwise
     */
    public boolean contains(String key) {
        return buckets[findBucketOf(key)].get(key) != null;
    }

    /**
     * @param key
     * @return value by given key if it exists in hash table and null otherwise
     */
    public String get(String key) {
        StringPair foundPair = buckets[findBucketOf(key)].get(key);
        return foundPair == null ? null : foundPair.getValue();
    }

    /**
     * puts pair (key, value) to hash table (or changes previous value by this key to new one)
     * @param key
     * @param value
     * @return previous value by given key if it exists or null otherwise
     */
    public String put(String key, String value) {
        int bucketId = findBucketOf(key);
        StringPair foundPair = buckets[bucketId].get(key);
        String ret;
        if (foundPair == null) {
            buckets[bucketId].put(new StringPair(key, value));
            ret = null;
            if ((++size) > ACCEPTABLE_DIFF * buckets.length) {
                reallocate();
            }
        } else {
            ret = foundPair.getValue();
            foundPair.setValue(value);
        }

        return ret;
    }

    /**
     * Removes element with given key from table
     * @param key
     * @return value by given key if it was in table and null otherwise
     */
    public String remove(String key) {
        StringPair deletedPair = buckets[findBucketOf(key)].remove(key);
        if (deletedPair != null) {
            --size;
            return deletedPair.getValue();
        } else {
            return null;
        }
    }

    /**
     * Removes all elements from table
     */
    public void clear() {
        size = 0;
        buckets = new List[DEFAULT_SIZE];
        for (int bucketId = 0; bucketId < DEFAULT_SIZE; bucketId++) {
            buckets[bucketId] = new List();
        }
    }

    /**
     * builds new HashMap with greater size
     */
    private void reallocate() {
        StringPair[] allContent = new StringPair[size];

        int ptr = 0;
        for (List bucket : buckets) {
            for (StringPair pair : bucket.toArray()) {
                allContent[ptr++] = pair;
            }
        }

        int bucketsSize = buckets.length * RESIZE_TIMES;
        buckets = new List[Primes.genNextPrime(bucketsSize)];
        for (int bucketId = 0; bucketId < buckets.length; bucketId++) {
            buckets[bucketId] = new List();
        }

        for (StringPair pair : allContent) {
            buckets[findBucketOf(pair.getKey())].put(pair);
        }
    }

    /**
     * finds number of bucket where this key should be placed
     * @param key
     * @return number of buckey
     */
    private int findBucketOf(String key) {
        return key.hashCode() % buckets.length;
    }
}

/**
 * Class to store lists of StringPairs
 * NB! StringPairs mustn't be null.
 */
class List {

    private StringPair head;
    private List next;
    private int size;

    /**
     * Constructs list with 0 elements
     */
    List() {
        head = null;
        next = null;
        size = 0;
    }

    /**
     * Constructs list with given head and tail
     * @param pair head of list
     * @param lst tail of list
     */
    private List(StringPair pair, List lst) {
        head = pair;
        next = lst;
        size = 1;
    }

    /**
     * Searches for element with given key in list
     * @param key
     * @return pair with given key if it exists and null otherwise
     */
    StringPair get(String key) {
        List ptr = this;
        while (ptr.head != null) {
            if (ptr.head.getKey().equals(key)) {
                return ptr.head;
            }
            ptr = ptr.next;
        }
        return null;
    }

    /**
     * Adds given pair to the head of list
     * @param pair
     */
    void put(StringPair pair) {
        next = new List(head, next);
        head = pair;
        ++size;
    }

    /**
     * removes pair with given key from list
     * @param key
     * @return removed pair if it was found and null otherwise
     */
    StringPair remove(String key) {
        List ptr = this;
        while (ptr.head != null) {
            if (ptr.head.getKey().equals(key)) {
                StringPair ret = ptr.head;
                ptr.head = ptr.next.head;
                ptr.next = ptr.next.next;
                --size;
                return ret;
            }
            ptr = ptr.next;
        }
        return null;
    }

    /**
     * Converts list to array
     * @return array made of this list with elements in order from head to tail
     */
    StringPair[] toArray() {
        StringPair[] content = new StringPair[size];
        List ptr = this;
        for (int i = 0; i < size; i++) {
            content[i] = ptr.head;
            ptr = ptr.next;
        }
        return content;
    }
}

class StringPair {

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

    /**
     * Constructs pair
     * @param key
     * @param value
     */
    StringPair(String key, String value) {
        this.key = key;
        this.value = value;
    }
}

class Primes {
    /**
     * @param n given number (must be more 2)
     * @return closest not less prime to given number
     */
    static int genNextPrime(int n) {
        while (!isPrime(n)) {
            ++n;
        }
        return n;
    }

    /**
     * @param n
     * @return true if n is prime and false otherwise
     */
    private static boolean isPrime(int n) {
        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }
}