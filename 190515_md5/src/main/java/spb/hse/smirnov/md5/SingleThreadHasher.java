package spb.hse.smirnov.md5;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/** Class for calculating MD5 hash in single-thread mode */
public class SingleThreadHasher {
    @NotNull
    /**
     * Calculates MD5 hash by given path
     * See algorithm here: http://hwproj.me/tasks/18105
     * Works in single-thread mode
     */
    public static byte[] calculateHash(@NotNull String path) {
        var file = new File(path);
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            // Impossible
            e.printStackTrace();
            return new byte[0];
        }
        if (file.isDirectory()) {
            messageDigest.update(file.getName().getBytes());
            for (var subfile : Objects.requireNonNull(file.listFiles())) {
                messageDigest.update(calculateHash(subfile.getPath()));
            }
        } else {
            try (var digestInputStream = new DigestInputStream(new FileInputStream(file),
                    messageDigest)) {
                while (digestInputStream.read() >= 0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return messageDigest.digest();
    }
}
