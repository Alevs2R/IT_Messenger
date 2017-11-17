import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {

        byte[] testArr = {-2, -40, 119, -91};

        RepetitionEncoder re = new RepetitionEncoder();
        byte[] encoded = re.encode(testArr,3);

        RepetitionDecoder rd = new RepetitionDecoder();
        byte[] decoded = rd.decode(encoded,3);
        System.out.println("!!!");
    }
}