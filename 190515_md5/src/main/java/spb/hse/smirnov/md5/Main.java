package spb.hse.smirnov.md5;

import java.io.File;
import java.util.Arrays;
import java.util.Scanner;

/** Console application to compare time of work of hashers */
public class Main {
    public static void main(String[] args) {
        var consoleScanner = new Scanner(System.in);
        System.out.println("Write path to file");
        String path = consoleScanner.nextLine();
        var file = new File(path);
        if (!file.exists()) {
            System.out.println("Wrong path!");
            return;
        }
        long timeStart = System.currentTimeMillis();
        System.out.println(Arrays.toString(SingleThreadHasher.calculateHash(path)));
        System.out.println("Single-thread: " + (System.currentTimeMillis() - timeStart) + "ms");
        timeStart = System.currentTimeMillis();
        System.out.println(Arrays.toString(MultiThreadHasher.calculateHash(path)));
        System.out.println("Multi-thread: " + (System.currentTimeMillis() - timeStart) + "ms");
    }
}
