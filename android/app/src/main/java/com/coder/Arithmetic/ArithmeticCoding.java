package com.coder.Arithmetic;
import java.io.*;

public class ArithmeticCoding {

    /**
     * compression method that starts Arithmetic Compressor.
     * @param b input byte array
     * @return compressed byte array
     */
    public byte[] compress(byte[] b) throws IOException {
        InputStream in = new ByteArrayInputStream(b);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ArithmeticCompressor comp = new ArithmeticCompressor();
        try (BitsOutputStream bitOut = new BitsOutputStream(out)) {
            comp.compress(in, bitOut);
        }
        return out.toByteArray();
    }

    /**
     * decompression method that starts Arithmetic Decompressor.
     * @param b input byte array
     * @return compressed byte array
     */
    public byte[] decompress(byte[] b) throws IOException {
        InputStream in = new ByteArrayInputStream(b);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ArithmeticDecompressor decomp = new ArithmeticDecompressor();
        decomp.decompress(new BitsInputStream(in), out);
        return out.toByteArray();
    }
}
