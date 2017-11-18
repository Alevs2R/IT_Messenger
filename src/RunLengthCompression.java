import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Run Length Encoding is a lossless algorithm that it is only efficient
 * with files that contain lots of repetitive data.
 */
class RunLengthCompression {

    /**
     * This function takes a file, transforms it into bytes then compresses it with RLE algorithm.
     * The algorithm replaces a sequence of repeating symbols with a single symbol and a count (run length)
     * indicating the number of times the symbol is repeated.
     *
     * @param fileName the name of the file that should be compressed
     * @return a compressed byte array
     */
    static byte[] compress(String fileName) throws IOException {
        File initialFile = new File(fileName);
        InputStream targetStream = new FileInputStream(initialFile);
        byte[] binary = org.apache.commons.io.IOUtils.toByteArray(targetStream);
        System.out.println("Size before compression: " + ((double) binary.length / 1024) / 1024 + " MBytes");
        ArrayList<Byte> compressed = new ArrayList<>();
        int counter = 1;
        for (int i = 0; i < binary.length; i++) {
            while (i + 1 < binary.length && binary[i] == binary[i + 1]) {
                counter++;
                if (counter == 256) {
                    compressed.add((byte) 0);
                    counter = 1;
                }
                i++;
            }
            compressed.add((byte) counter);
            compressed.add(binary[i]);
            counter = 1;
        }
        Byte[] comp = new Byte[compressed.size()];
        comp = compressed.toArray(comp);
        byte[] compr;
        compr = toPrimitives(comp);
        System.out.println("Size after compression: " + ((double) compr.length / 1024) / 1024 + " MBytes");
        return compr;
    }

    //Byte[] to byte[]
    private static byte[] toPrimitives(Byte[] oBytes) {
        byte[] bytes = new byte[oBytes.length];
        for (int i = 0; i < oBytes.length; i++) {
            bytes[i] = oBytes[i];
        }
        return bytes;
    }

    /**
     * @param compressed the file that should be decompressed
     * @return an array of decompressed bytes
     */
    static byte[] decompress(byte[] compressed) {
        ArrayList<Byte> decompressed = new ArrayList<>();
        byte tmp;
        int count = 0;
        for (int i = 0; i < compressed.length; i++) {
            tmp = compressed[i];
            if (tmp == 0) {
                count += 255;
                continue;
            }
            count += tmp;
            for (int j = 0; j < count; j++) {
                decompressed.add(compressed[i + 1]);
            }
            i++;
            count = 0;
        }
        Byte[] decomp = new Byte[decompressed.size()];
        decomp = decompressed.toArray(decomp);
        byte[] decompr;
        decompr = toPrimitives(decomp);
        return decompr;
    }

}