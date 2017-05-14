package org.elbek;

import java.util.Arrays;

/**
 * Simple utility class for packing and unpacking integer values into byte blocks.
 * Created by elbek on 5/13/17.
 */
public class BitPacker {

    /**
     * returns integer value by its position
     * @param packedBytes
     * @param at
     * @param bitsPerValue
     * @return returns integer value by its position
     */
    public static int get(byte[] packedBytes, int at, int bitsPerValue) {
        int result = 0;
        int start = (bitsPerValue * at) / 8; //which byte block values is starting
        int delta = 8 - (bitsPerValue * at) % 8; //starting bit position in the right byte block
        int remaining = bitsPerValue; //remaining bits to read
        if (delta > 0) {
            remaining -= delta;
            if (remaining > 0) {
                result |= ((packedBytes[start++] & 0xFF) & ((1 << delta) - 1)) << remaining;
            } else {
                result |= ((packedBytes[start++] & 0xFF) & ((1 << delta) - 1)) >>> -remaining;
            }
        }
        while (remaining >= 8) {
            remaining -= 8;
            result |= (packedBytes[start++] & 0xFF) << remaining;
        }
        if (remaining > 0) {
            result |= (packedBytes[start] & 0xFF) >>> (8 - remaining);
        }
        return result;
    }

    /**
     * sets the value to a given position, it is callers responsibility to pass right bitsPerValue
     * providing wrong bitsPerValue results undefined
     * @param packedBytes
     * @param at position
     * @param value value to be set
     * @param bitsPerValue, how many bits per value for the packed bytes.
     */
    public static void set(byte[] packedBytes, int at, int value, int bitsPerValue) {
        int start = (bitsPerValue * at) / 8;
        //this defines at what position in start the value starts
        int delta = 8 - (bitsPerValue * at) % 8;
        int remaining = bitsPerValue;
        if (delta > 0) {
            remaining -= delta;
            if (remaining > 0) { //this means value spans to the next byte
                packedBytes[start] &= ~((1 << (delta)) - 1); //wipe out existing value there.
                packedBytes[start++] |= value >>> remaining;
            } else {
                packedBytes[start] &= ~(((1 << (bitsPerValue)) - 1) << (delta - bitsPerValue)) | ((1 << (delta - bitsPerValue)) - 1); //wipe out existing value there.;
                packedBytes[start++] |= value << -remaining;
            }
        }
        while (remaining >= 8) {
            remaining -= 8;
            packedBytes[start++] = (byte) (value >>> remaining);
        }
        if (remaining > 0) {
            packedBytes[start] &= (1 << (8 - remaining)) - 1;
            packedBytes[start] |= (byte) (value << (8 - remaining));
        }
    }

    /**
     * packs values array to packed length byte array
     * @param values source array
     * @param valueStart where to start packing
     * @param length, how many positions to read
     * @param packedBytes, packed byte array
     * @param byteStart, where to start packing into byte array
     * @param bitsPerValue, how many bits per value
     */
    public static void pack(int[] values, int valueStart, int length, byte[] packedBytes, int byteStart, int bitsPerValue) {
        int shift = 8;
        int maxLen = valueStart + length;
        while (valueStart < maxLen) {
            shift -= bitsPerValue;
            if (shift > 0) { //we have enough room in current byte to accommodate
                packedBytes[byteStart] |= (values[valueStart++] << shift) & 0xFF;
            } else if (shift == 0) {
                packedBytes[byteStart++] |= (byte) (values[valueStart++]);
                shift = 8;
            } else {
                packedBytes[byteStart] |= (values[valueStart] >>> -shift) & 0xFF;
                byteStart++;
                shift = 8 + shift + bitsPerValue;
            }
        }
    }

    /**
     * unpacks packedBytes back to values array.
     * @param values int values written staring position valueStart, make sure this array is filled with 0, otherwise result is undefined.
     * @param valueStart where to start packing
     * @param length, how many positions to read
     * @param packedBytes, packed byte array to be unpacked
     * @param byteStart, where to start unpacking into int array
     * @param bitsPerValue, how many bits per value
     */
    public static void unPack(int[] values, int valueStart, int length, byte[] packedBytes, int byteStart, int bitsPerValue) {
        int shift = 8;
        int maxLen = valueStart + length;
        int hardMask = (1 << bitsPerValue) - 1;
        while (valueStart < maxLen) {
            shift -= bitsPerValue;
            if (shift > 0) { //we have enough room in current byte to accommodate
                values[valueStart++] |= ((packedBytes[byteStart] & 0xff) >>> shift) & hardMask;
            } else if (shift == 0) {
                values[valueStart++] |= (packedBytes[byteStart++] & 0xff) & hardMask;
                shift = 8;
            } else {
                values[valueStart] |= ((packedBytes[byteStart] & 0xff) & ((1 << (bitsPerValue + shift)) - 1)) << (-shift);
                byteStart++;
                shift = 8 + shift + bitsPerValue;
            }
        }
    }

    /**
     *
     * @param size length of array
     * @param bitsPerValue how many bits per value
     * @return size of packed array for given sized array
     */
    public static int calculatePackedSize(int size, int bitsPerValue) {
        return (int) Math.ceil((size * bitsPerValue) / 8d);
    }

    /**
     * finds highest leading bit position and returns what bitsPerValue value would be for given array
     * @param values source array
     * @return bitsPerValue
     */
    public static int getBitsPerValue(int[] values) {
        int i = 0;
        for (int value : values) {
            i |= value;
        }
        return 32 - Integer.numberOfLeadingZeros(i);
    }

    public static void main(String[] args) {
        int[] values = new int[]{15, 17, 19, 16, 18, 19, 21000, 17, 1700, 15, 21, 21, 21, 3, 15, 16, 1, 0, 2, 5, 31000};
        int bt = getBitsPerValue(values);
        byte[] packed = new byte[calculatePackedSize(values.length, bt)];
        pack(values, 0, values.length, packed, 0, bt);
        System.out.println(get(packed, 1, bt));
        set(packed, 1, 10, bt);
        System.out.println(get(packed, 1, bt));
        System.out.println(get(packed, 6, bt));
        Arrays.fill(values, 0);
        unPack(values, 0, values.length, packed, 0, bt);
        System.out.println(Arrays.toString(values));
    }
}