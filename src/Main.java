import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner read = new Scanner(new File("input.txt"));
        String line = read.nextLine();
        System.out.println(RunLengthCompression.compress(line));
        System.out.println(RunLengthCompression.decompress(RunLengthCompression.compress(line)));
    }
}
