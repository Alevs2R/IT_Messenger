import sun.security.util.BitArray;

import java.io.*;
import java.nio.file.Files;
import java.util.BitSet;

public class ReedMuller {
    private final static int ENCODE_BIT_MATRIX[][] =
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
    private final static int[][] DECODE_BIT_MATRIX =
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
    private static int[][] encodeBitVector;
    private static int[][] decodeBitVector;

    public ReedMuller() {
    }

    public static File encode(File file) throws IOException {
        byte[] data = Files.readAllBytes(file.toPath());
        int[] bits = new int[8 * data.length];
        for (int i = 0; i < bits.length; i++) {
            bits[i] = (data[i / 8] >> (7 - i % 8)) & 1;
        }
        encodeBitVector = new int[bits.length / 5 + 1][5];
        for (int i = 0; i < encodeBitVector.length; i++) {
            for (int j = 0; j < 5; j++) {
                if (5 * i + j < bits.length) encodeBitVector[i][j] = bits[5 * i + j];
                else encodeBitVector[i][j] = 0;
            }
        }
        int[][] encodedBitVector = new int[encodeBitVector.length][16];
        for (int i = 0; i < encodeBitVector.length; i++) {
            for (int j = 0; j < 16; j++) {
                encodedBitVector[i][j] = multiply(encodeBitVector[i], ENCODE_BIT_MATRIX[j]) % 2;
            }
        }
        BitSet encodedBits = new BitSet(encodedBitVector.length * 16);
        for (int i = 0; i < encodedBitVector.length; i++) {
            for (int j = 0; j < encodedBitVector[i].length; j++) {
                if (encodedBitVector[i][j] == 1) encodedBits.set(16 * i + j);
            }
        }
        String path = file.getPath();
        String name = path.substring(path.lastIndexOf('/'), path.lastIndexOf('.'));
        String extension = path.substring(path.lastIndexOf('.'));
        path = path.substring(0, path.lastIndexOf('/') + 1);
        File f = new File(path + name + "(1)" + extension);
        Integer i = 2;
        while (!f.createNewFile()) {
            f.renameTo(new File(path + name + "(" + i.toString() + ")" + extension));
            i++;
        }
        FileOutputStream stream = new FileOutputStream(f);
        stream.write(encodedBits.toByteArray());
        stream.close();
        return f;
    }

    public static File decode(File file) throws IOException {
        byte[] data = Files.readAllBytes(file.toPath());
        int[] bits = new int[8 * data.length];
        for (int i = 0; i < bits.length; i++) {
            bits[i] = (data[i / 8] >> (i % 8)) & 1;
        }
        decodeBitVector = new int[bits.length / 16][16];
        for (int i = 0; i < bits.length / 16; i++) {
            for (int j = 0; j < 16; j++) {
                decodeBitVector[i][j] = 2 * bits[16 * i + j] - 1;
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
        String path = file.getPath();
        String name = path.substring(path.lastIndexOf('/'), path.lastIndexOf('.'));
        String extension = path.substring(path.lastIndexOf('.'));
        path = path.substring(0, path.lastIndexOf('/') + 1);
        File f = new File(path + name + "(1)" + extension);
        Integer i = 2;
        while (!f.createNewFile()) {
            f.renameTo(new File(path + name + "(" + i.toString() + ")" + extension));
            i++;
        }
        FileOutputStream stream = new FileOutputStream(f);
        stream.write(decodedBits.toByteArray());
        stream.close();
        return file;
    }

    private static int multiply(int a[], int b[]) {
        int c = 0;
        for (int i = 0; i < a.length; i++) {
            c += a[i] * b[i];
        }
        return c;
    }
}
