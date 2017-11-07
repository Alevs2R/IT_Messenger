import java.io.*;

/**
 * @author Pavel Kozlov
 */
public class Hamming74 {
    public Hamming74() {
    }

    byte[] codingFile(String fileName) throws IOException {
        File initialFile = new File(fileName);
        InputStream targetStream = new FileInputStream(initialFile);
        byte[] arr1 = org.apache.commons.io.IOUtils.toByteArray(targetStream);
        return (new Hamming74Coder()).coding(arr1);
    }

    public void decodingFile(byte[] b, String fileName) throws IOException {
        byte[] arr = (new Hamming74Decoder()).decoding(b);
        FileOutputStream outFile = new FileOutputStream(fileName);
        outFile.write(arr);
        outFile.close();
    }
}
