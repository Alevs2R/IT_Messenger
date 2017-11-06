import java.io.*;

class ArithmeticCoding {

    /**
     * This method compresses input file using Arithmetic Codng algorithm.
     * @param fileName is the input file that needs to be compressed
     * @return array of bytes (after compression)
     */
    byte[] compressFile(String fileName) throws IOException {
        File initialFile = new File(fileName);
        InputStream targetStream = new FileInputStream(initialFile);
        byte[] arr1 = org.apache.commons.io.IOUtils.toByteArray(targetStream);
        System.out.println("Size before compression: " + ((double)arr1.length/1024)/1024 + " MBytes");
        byte[] arr2 = compress(arr1);
        System.out.println("Size after compression: " + ((double)arr2.length/1024)/1024 + " MBytes");
        return arr2;
    }

    /**
     * This method decompresses byte array of data (compressed using Arithmetic Coding)
     * @param b is the byte array that needs to be decompressed
     * @param fileName output file, in which we need to save result of decompression
     */
    public void decompressFile(byte[] b, String fileName) throws IOException {
        InputStream in = new ByteArrayInputStream(b);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ArithmeticDecompressor decomp = new ArithmeticDecompressor();
        decomp.decompress(new BitsInputStream(in), out);
        byte[] arr = decompress(b);
        FileOutputStream outFile = new FileOutputStream(fileName);
        outFile.write(arr);
        outFile.close();
    }

    /**
     * Private compression method that starts Arithmetic Compressor.
     * @param b input byte array
     * @return compressed byte array
     */
    private byte[] compress(byte[] b) throws IOException {
        InputStream in = new ByteArrayInputStream(b);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ArithmeticCompressor comp = new ArithmeticCompressor();
        try (BitsOutputStream bitOut = new BitsOutputStream(out)) {
            comp.compress(in, bitOut);
        }
        return out.toByteArray();
    }

    /**
     * Private decompression method that starts Arithmetic Decompressor.
     * @param b input byte array
     * @return compressed byte array
     */
    private byte[] decompress(byte[] b) throws IOException {
        InputStream in = new ByteArrayInputStream(b);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ArithmeticDecompressor decomp = new ArithmeticDecompressor();
        decomp.decompress(new BitsInputStream(in), out);
        return out.toByteArray();
    }
}
