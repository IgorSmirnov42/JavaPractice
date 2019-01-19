package ru.spbhse.hashmap;

/**
 * Serving class with methods to work with prime numbers
 */
public class Primes {
    /**
     * Generates prime number not less than given number (may be not prime)
     * @param n given number (must be not less 2)
     * @return closest not less prime to given number
     */
    public static int genNextPrime(int n) {
        if (n < 2) {
            throw new IllegalArgumentException("n given to Primes.genPrimeNumbers must be more than 1");
        }
        while (!isPrime(n)) {
            ++n;
        }
        return n;
    }

    /** Returns true if n is prime and false otherwise */
    private static boolean isPrime(int n) {
        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }
}
