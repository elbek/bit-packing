package org.elbek;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.*;

/**
 * Created by elbek on 5/13/17.
 */
@RunWith(Parameterized.class)
public class BitPackerTest {
    int values[] = null;
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { 2 }, { 3 }, { 4 }, { 5 }, { 6 }, { 7 }, { 8 }, { 9 }, { 10 }, { 11 }, { 12 }, { 13 },
                { 14 }, { 15 }, { 16 }, { 17 }, { 18 }, { 19 }, { 20 }, { 21 }, { 22 }, { 23 }, { 24 }, { 25 },
                { 26 }, { 27 }, { 28 }, { 29 }, { 30 }, { 31 }
        });
    }
    @Parameterized.Parameter
    public int bitPerValue;
    byte[] packed;

    @Before
    public void setUp() throws Exception {
        int size = ThreadLocalRandom.current().nextInt(500, 10000000);
        values = new int[size];
        for (int i = 0; i < values.length; i++) {
            int max = 1<<bitPerValue;
            if (bitPerValue == 31) {
                max = Integer.MAX_VALUE;
            }
            values[i] = ThreadLocalRandom.current().nextInt(1, max);
        }
        packed = new byte[BitPacker.calculatePackedSize(values.length, bitPerValue)];
    }

    @Test
    public void get() throws Exception {
        BitPacker.pack(values, 0, values.length, packed, 0, bitPerValue);
        for (int i = 0; i < values.length; i++) {
            assertEquals(values[i], BitPacker.get(packed, i, bitPerValue));
        }
    }

    @Test
    public void set() throws Exception {
        BitPacker.pack(values, 0, values.length, packed, 0, bitPerValue);
        for (int i = 0; i < values.length; i++) {
            BitPacker.set(packed, i, values[i]-1, bitPerValue);
        }
        for (int i = 0; i < values.length; i++) {
            assertEquals(values[i] - 1, BitPacker.get(packed, i, bitPerValue));
        }
    }

    @Test
    public void packUnPack() throws Exception {
        BitPacker.pack(values, 0, values.length, packed, 0, bitPerValue);
        int[] unPacked = new int[values.length];
        BitPacker.unPack(unPacked, 0, unPacked.length, packed, 0, bitPerValue);
        assertArrayEquals(values, unPacked);
    }

}