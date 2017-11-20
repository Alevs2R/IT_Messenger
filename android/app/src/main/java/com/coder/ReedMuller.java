package com.coder;

import java.io.IOException;

public class ReedMuller {
    private final int ENCODE_BIT_MATRIX[][] =
            {
                    {1, 0, 0, 0, 0},
                    {1, 0, 0, 0, 1},
                    {1, 0, 0, 1, 0},
                    {1, 0, 0, 1, 1},
                    {1, 0, 1, 0, 0},
                    {1, 0, 1, 0, 1},
                    {1, 0, 1, 1, 0},
                    {1, 0, 1, 1, 1},
                    {1, 1, 0, 0, 0},
                    {1, 1, 0, 0, 1},
                    {1, 1, 0, 1, 0},
                    {1, 1, 0, 1, 1},
                    {1, 1, 1, 0, 0},
                    {1, 1, 1, 0, 1},
                    {1, 1, 1, 1, 0},
                    {1, 1, 1, 1, 1}
            };
    private final int[][] DECODE_BIT_MATRIX =
            {
                    {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {1, -1, 1, -1, 1, -1, 1, -1, 1, -1, 1, -1, 1, -1, 1, -1},
                    {1, 1, -1, -1, 1, 1, -1, -1, 1, 1, -1, -1, 1, 1, -1, -1},
                    {1, -1, -1, 1, 1, -1, -1, 1, 1, -1, -1, 1, 1, -1, -1, 1},
                    {1, 1, 1, 1, -1, -1, -1, -1, 1, 1, 1, 1, -1, -1, -1, -1},
                    {1, -1, 1, -1, -1, 1, -1, 1, 1, -1, 1, -1, -1, 1, -1, 1},
                    {1, 1, -1, -1, -1, -1, 1, 1, 1, 1, -1, -1, -1, -1, 1, 1},
                    {1, -1, -1, 1, -1, 1, 1, -1, 1, -1, -1, 1, -1, 1, 1, -1},
                    {1, 1, 1, 1, 1, 1, 1, 1, -1, -1, -1, -1, -1, -1, -1, -1},
                    {1, -1, 1, -1, 1, -1, 1, -1, -1, 1, -1, 1, -1, 1, -1, 1},
                    {1, 1, -1, -1, 1, 1, -1, -1, -1, -1, 1, 1, -1, -1, 1, 1},
                    {1, -1, -1, 1, 1, -1, -1, 1, -1, 1, 1, -1, -1, 1, 1, -1},
                    {1, 1, 1, 1, -1, -1, -1, -1, -1, -1, -1, -1, 1, 1, 1, 1},
                    {1, -1, 1, -1, -1, 1, -1, 1, -1, 1, -1, 1, 1, -1, 1, -1},
                    {1, 1, -1, -1, -1, -1, 1, 1, -1, -1, 1, 1, 1, 1, -1, -1},
                    {1, -1, -1, 1, -1, 1, 1, -1, -1, 1, 1, -1, 1, -1, -1, 1}
            };
    private int[][] encodeBitVector;
    private int[][] decodeBitVector;

    public ReedMuller() {
    }

    public byte[] encode(byte[] input) throws IOException {
        BitArray bits = new BitArray(8 * input.length, input);
        encodeBitVector = new int[bits.length() / 5 + 1][5];
        for (int i = 0; i < encodeBitVector.length; i++) {
            for (int j = 0; j < 5; j++) {
                if (5 * i + j < bits.length()) encodeBitVector[i][j] = (bits.get(5 * i + j)) ? 1 : 0;
                else encodeBitVector[i][j] = 0;
            }
        }
        int[][] encodedBitVector = new int[encodeBitVector.length][16];
        for (int i = 0; i < encodeBitVector.length; i++) {
            for (int j = 0; j < 16; j++) {
                encodedBitVector[i][j] = multiply(encodeBitVector[i], ENCODE_BIT_MATRIX[j]) % 2;
            }
        }
        BitArray encodedBits = new BitArray(encodedBitVector.length * 16);
        for (int i = 0; i < encodedBitVector.length; i++) {
            for (int j = 0; j < encodedBitVector[i].length; j++) {
                encodedBits.set(16 * i + j, encodedBitVector[i][j] == 1);
            }
        }
        return encodedBits.toByteArray();
    }

    public byte[] decode(byte[] input) throws IOException {
       BitArray bits = new BitArray(8*input.length,input);
        decodeBitVector = new int[bits.length() / 16][16];
        for (int i = 0; i < bits.length() / 16; i++) {
            for (int j = 0; j < 16; j++) {
                decodeBitVector[i][j] = (bits.get(16*i+j))?1:-1;
            }
        }
        int v;
        int[] index = new int[decodeBitVector.length];
        int[] value = new int[decodeBitVector.length];
        for (int i = 0; i < decodeBitVector.length; i++) {
            value[i] = 0;
            for (int j = 0; j < 16; j++) {
                v = multiply(decodeBitVector[i], DECODE_BIT_MATRIX[j]);
                if (Math.abs(v) > Math.abs(value[i])) {
                    value[i] = v;
                    index[i] = j;
                }
            }
        }
        int k = 0;
        int manyBits = 8 * ((decodeBitVector.length * 5) / 8);
        BitArray decodedBits = new BitArray(manyBits);
        for (int i = 0; i < index.length; i++) {
            int[] x = new int[5];
            int sign = (int) Math.signum(value[i]);
            x[0] = (sign * DECODE_BIT_MATRIX[index[i]][0] + 1) / 2;
            x[1] = ((sign * DECODE_BIT_MATRIX[index[i]][8] + 1) / 2 + x[0]) % 2;
            x[2] = ((sign * DECODE_BIT_MATRIX[index[i]][4] + 1) / 2 + x[0]) % 2;
            x[3] = ((sign * DECODE_BIT_MATRIX[index[i]][2] + 1) / 2 + x[0]) % 2;
            x[4] = ((sign * DECODE_BIT_MATRIX[index[i]][1] + 1) / 2 + x[0]) % 2;
            for (int j = 0; j < x.length; j++) {
                if (k < manyBits) decodedBits.set(k, x[j] == 1);
                ++k;
            }
        }
        return decodedBits.toByteArray();
    }

    private int multiply(int a[], int b[]) {
        int c = 0;
        for (int i = 0; i < a.length; i++) {
            c += a[i] * b[i];
        }
        return c;
    }
}