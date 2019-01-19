package ru.spbhse.hashmap;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PrimesTest {

    @Test
    public void genNextPrimeTotalTest() {
        assertEquals(7, Primes.genNextPrime(7));
        assertEquals(7, Primes.genNextPrime(6));
        assertEquals(37, Primes.genNextPrime(34));
    }

    @Test
    public void genNextPrimeCheckThrows() {
        assertThrows(IllegalArgumentException.class, () -> Primes.genNextPrime(1));
        assertThrows(IllegalArgumentException.class, () -> Primes.genNextPrime(-1));
    }
}