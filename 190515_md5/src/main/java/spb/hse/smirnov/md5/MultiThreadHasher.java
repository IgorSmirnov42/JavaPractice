package spb.hse.smirnov.md5;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/** Class for calculating MD5 hash in multi-thread mode */
public class MultiThreadHasher {
    /**
     * Calculates MD5 hash by given path
     * See algorithm here: http://hwproj.me/tasks/18105
     * Works in multi-thread mode, implemented with fork-join
     */
    @NotNull
    public static byte[] calculateHash(@NotNull String path) {
        var file = new File(path);
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            // Impossible
            e.printStackTrace();
            return new byte[0];
        }
        if (file.isDirectory()) {
            messageDigest.update(file.getName().getBytes());
            var tasks = new ArrayList<ForkJoinTask<?>>();
            for (var subfile : Objects.requireNonNull(file.listFiles())) {
                var task = new RecursiveTask<byte[]>() {
                    @Override
                    protected byte[] compute() {
                        return calculateHash(subfile.getPath());
                    }
                };
                tasks.add(task);
                task.fork();
            }
            for (var task : tasks) {
                messageDigest.update((byte[]) task.join());
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
