package spb.hse.smirnov.md5;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class HasherTest {
    private static final String singleFile = "src/test/java/resources/singleFile";
    private static final String directory = "src/test/java/resources/directory";
    private static final String directory1 = "src/test/java/resources/directory1";
    @Test
    void onOneFileShouldBeEqual() {
        assertArrayEquals(MultiThreadHasher.calculateHash(singleFile),
                SingleThreadHasher.calculateHash(singleFile));
    }

    @Test
    void shouldBeEqualOnSameDirectory() {
        assertArrayEquals(MultiThreadHasher.calculateHash(directory),
                SingleThreadHasher.calculateHash(directory));
    }

    @Test
    void shouldBeDifferentOnDifferentPaths() {
        assertFalse(Arrays.equals(MultiThreadHasher.calculateHash(directory),
                SingleThreadHasher.calculateHash(singleFile)));
    }

    @Test
    void shouldBeEqualOnSameButDifferentDirectories() {
        assertArrayEquals(MultiThreadHasher.calculateHash(directory + "/aaa"),
                SingleThreadHasher.calculateHash(directory1 + "/aaa"));
    }
}