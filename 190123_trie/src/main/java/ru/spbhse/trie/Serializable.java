package ru.spbhse.trie;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

// TODO : Javadocs
public interface Serializable {
    public void serialize(OutputStream out) throws IOException;
    public void deserialize(InputStream in) throws IOException;
}
