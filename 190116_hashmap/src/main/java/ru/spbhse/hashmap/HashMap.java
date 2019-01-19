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
        buckets = new List[DEFAULT_SIZE];
        for (int bucketId = 0; bucketId < DEFAULT_SIZE; bucketId++) {
            buckets[bucketId] = new List();
        }
    }

    /** Returns number of elements in HashMap */
    public int size() {
        return size;
    }

    /**
     * Checks if HashMap contains given key
     * @return true if HashMap contains this key and false otherwise
     */
    public boolean contains(String key) {
        if (key == null) {
            throw new IllegalArgumentException("key given to HashMap.contains mustn't be null");
        }
        return buckets[findBucketOf(key)].get(key) != null;
    }

    /** Returns value by given key if it exists in hash table and null otherwise */
    public String get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("key given to HashMap.get mustn't be null");
        }
        StringPair foundPair = buckets[findBucketOf(key)].get(key);
        return foundPair == null ? null : foundPair.getValue();
    }

    /**
     * Puts pair (key, value) to hash table (or changes previous value by this key to new one)
     * @return previous value by given key if it exists or null otherwise
     */
    public String put(String key, String value) {
        if (key == null) {
            throw new IllegalArgumentException("key given to HashMap.put mustn't be null");
        }
        int bucketId = findBucketOf(key);
        StringPair foundPair = buckets[bucketId].get(key);
        String previousValue;
        if (foundPair == null) {
            buckets[bucketId].put(new StringPair(key, value));
            previousValue = null;
            ++size;
        } else {
            previousValue = foundPair.getValue();
            foundPair.setValue(value);
        }

        if (size > ACCEPTABLE_DIFF * buckets.length) {
            reallocate();
        }

        return previousValue;
    }

    /**
     * Removes element with given key from table
     * @return value by given key if it was in table and null otherwise
     */
    public String remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("key given to HashMap.remove mustn't be null");
        }
        StringPair deletedPair = buckets[findBucketOf(key)].remove(key);
        if (deletedPair != null) {
            --size;
            return deletedPair.getValue();
        } else {
            return null;
        }
    }

    /** Removes all elements from table */
    public void clear() {
        size = 0;
        buckets = new List[DEFAULT_SIZE];
        for (int bucketId = 0; bucketId < DEFAULT_SIZE; bucketId++) {
            buckets[bucketId] = new List();
        }
    }

    /** Builds new HashMap with greater size */
    private void reallocate() {
        var allContent = new StringPair[size];

        int listElementPointer = 0;
        for (List bucket : buckets) {
            for (StringPair pair : bucket.toArray()) {
                allContent[listElementPointer++] = pair;
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
     * Finds number of bucket where this key should be placed
     * @return number of bucket
     */
    private int findBucketOf(String key) {
        return ((key.hashCode() % buckets.length) + buckets.length) % buckets.length;
    }
}
