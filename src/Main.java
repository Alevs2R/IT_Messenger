import java.io.*;

public class Main {

    public static void main(String[] args) throws IOException {
        ArithmeticCoding test = new ArithmeticCoding();
        test.decompressFile(test.compressFile("input.mp3"), "output.mp3");
    }
}
