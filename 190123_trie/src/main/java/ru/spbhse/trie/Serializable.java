package ru.spbhse.trie;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/** Interface to turn class object to sequence of bytes (serialize) and back (deserialize) */
public interface Serializable {
    /** Converts object to sequence of bytes and writes it to given OutputStream */
    void serialize(OutputStream out) throws IOException;

    /** Replaces old object with new one from stream */
    void deserialize(InputStream in) throws IOException;
}
