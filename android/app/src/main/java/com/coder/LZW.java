package com.coder;

import java.io.*;
import java.util.*;

class LZW {

    private HashMap<String, Integer> table = new HashMap<>();
    private String[] symbols;
    private int count;
    private final int MAX_NUM = 4096;

    /**
     * Method that compresses input file using LZW algorithm and returns the
     * compressed byte array. Since computers don't support 12bit structures,
     * all the result is saved in 3 bytes (2 times using 12 bit).
     *
     * @param fileName is the input file that needs to be compressed
     * @throws IOException if file doesn't exist
     */
    byte[] compress(String fileName) throws IOException {
        symbols = new String[MAX_NUM];
        for (int i = 0; i < 256; i++) {
            table.put(Character.toString((char) i), i);
            symbols[i] = Character.toString((char) i);
        }
        count = 256;
        // Input stream
        DataInputStream read = new DataInputStream(new BufferedInputStream(
                new FileInputStream(fileName)));
        System.out.println("Size before compression: " + (((double)read.available()/1024)/1024) + "MBytes");
        // Output stream
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte input_byte;
        String previous = "";
        byte[] buffer = new byte[3];
        boolean left = true;
        input_byte = read.readByte();
        int i = new Byte(input_byte).intValue();
        if (i < 0)
            i += 256;
        char c = (char) i;
        previous = "" + c;
        while (read.available() > 0) { //while there are more bytes in the stream
            input_byte = read.readByte(); //read the byte
            i = new Byte(input_byte).intValue(); // store its integer value
            if (i < 0)
                i += 256;
            c = (char) i; //transform to char
            if (table.containsKey(previous + c)) { //if this sequence already exist in the map
                previous = previous + c; //update previous string
            } else { //if this is a new sequence
                String s = convertTo12Bit(table.get(previous)); //convert input into 12bits
                if (left) { //if the value is on the left of the second byte
                    buffer[0] = (byte) Integer.parseInt(
                            s.substring(0, 8), 2);
                    buffer[1] = (byte) Integer.parseInt(
                            s.substring(8, 12) + "0000", 2);
                } else { //if the value is on the right of the first byte
                    buffer[1] += (byte) Integer.parseInt(
                            s.substring(0, 4), 2);
                    buffer[2] = (byte) Integer.parseInt(
                            s.substring(4, 12), 2);
                    for (int b = 0; b < buffer.length; b++) {
                        out.write(buffer[b]);
                        buffer[b] = 0;
                    }
                }
                left = !left; //for next byte
                if (count < MAX_NUM) //if we haven't reached our maximum
                    table.put(previous + c, count++); //put the sequence in the map
                previous = "" + c; //update the "previous" sequence
            }
        }
        String tempPrevious = convertTo12Bit(table.get(previous)); //convert the previous sequence to 12 bit
        if (left) { // if the value is on the left of the second byte
            buffer[0] = (byte) Integer.parseInt(tempPrevious.substring(0, 8), 2);
            buffer[1] = (byte) Integer.parseInt(tempPrevious.substring(8, 12)
                    + "0000", 2);
            out.write(buffer[0]); // write the result into output stream
            out.write(buffer[1]);
        } else { // if the value is on the right of the first byte
            buffer[1] += (byte) Integer
                    .parseInt(tempPrevious.substring(0, 4), 2);
            buffer[2] = (byte) Integer
                    .parseInt(tempPrevious.substring(4, 12), 2);
            for (int b = 0; b < buffer.length; b++) {
                out.write(buffer[b]);
                buffer[b] = 0;
            }
        }
        read.close(); // close the input stream
        System.out.println("Size after compression: " + (((double)out.size() * 3/1024)/4096) + "MBytes");
        return out.toByteArray();
    }

    /**
     * This function is used to convert an integer value of a byte to the 12bit representation
     * @param i integer that needs to be converted
     * @return the result in the form of a string
     */
    private String convertTo12Bit(int i) {
        StringBuilder value = new StringBuilder(Integer.toBinaryString(i));
        while (value.length() < 12) {
            value.insert(0, "0");
        }
        return value.toString();
    }

    /**
     * Extract the 12 bit key from 2 bytes and get the int value of the key
     * Method to get the key from 12bit data (using two bytes)
     * @param b1 first byte
     * @param b2 second byte (we need only 4 bits of it)
     * @param isLeft whether the value on the left of the second byte or on the right of the first
     * @return integer key
     */
    private int getKey(byte b1, byte b2, boolean isLeft) {
        StringBuilder val1 = new StringBuilder(Integer.toBinaryString(b1));
        StringBuilder val2 = new StringBuilder(Integer.toBinaryString(b2));
        while (val1.length() < 8) {
            val1.insert(0, "0");
        }
        if (val1.length() == 32) {
            val1 = new StringBuilder(val1.substring(24, 32));
        }
        while (val2.length() < 8) {
            val2.insert(0, "0");
        }
        if (val2.length() == 32) {
            val2 = new StringBuilder(val2.substring(24, 32));
        }
        if (isLeft) {
            return Integer.parseInt(val1 + val2.substring(0, 4), 2);
        } else {
            return Integer.parseInt(val1.substring(4, 8) + val2, 2);
        }
    }

    /**
     * Decompression algorithm for ZLW compressed array of bytes.
     * @param fileName in which file result of decompression should be saved
     */
    void decompress(byte[] b, String fileName) throws IOException {
        symbols = new String[4096];
        for (int i = 0; i < 256; i++) {
            table.put(Character.toString((char) i), i);
            symbols[i] = Character.toString((char) i);
        }
        count = 256;
        FileOutputStream fos = new FileOutputStream("file.temp");
        File file = new File("file.temp"); //temporary file (faster to work with that the array)
        fos.write(b);
        fos.close();
        DataInputStream in = new DataInputStream(new BufferedInputStream(
                new FileInputStream("file.temp")));
        DataOutputStream out = new DataOutputStream(new BufferedOutputStream(
                new FileOutputStream(fileName)));
        int currentWord, previousWord;
        byte[] buffer = new byte[3]; //buffer for 2 coded words (12bit each = 24bits = 3 bytes)
        buffer[0] = in.readByte(); // read first two bytes
        buffer[1] = in.readByte();
        previousWord = getKey(buffer[0], buffer[1], true);
        boolean onLeft = false;
        out.writeBytes(symbols[previousWord]);
        while (in.available() > 0) { // if there are more bytes in the input stream
            if (onLeft) { // if the value is on the left of the second byte
                buffer[0] = in.readByte();
                buffer[1] = in.readByte();
                currentWord = getKey(buffer[0], buffer[1], true);
            } else { // if the value on the right of the first byte
                buffer[2] = in.readByte();
                currentWord = getKey(buffer[1], buffer[2], false);
            }
            onLeft = !onLeft; // change mode for the next byte
            if (currentWord >= count) { //if there is not enough space to write
                if (count < MAX_NUM)
                    symbols[count] = symbols[previousWord]
                            + symbols[previousWord].charAt(0);
                count++;
                out.writeBytes(symbols[previousWord]
                        + symbols[previousWord].charAt(0)); // write the output into the output stream
            } else {
                if (count < 4096)
                    symbols[count] = symbols[previousWord]
                            + symbols[currentWord].charAt(0);
                count++;
                out.writeBytes(symbols[currentWord]);
            }
            previousWord = currentWord; // update the words
        }
        in.close();
        out.close();
        if (!file.delete())
            throw new IllegalArgumentException("The filename is wrong");
    }
}