public class Main {
    public static void main(String[] args) {
        String toEncode = "101";
        RepetitionEncoder re = new RepetitionEncoder();
        String encoded = re.encode(toEncode,3);
        System.out.println(encoded);

        String toDecode = "010111011100101";
        RepetitionDecoder rd = new RepetitionDecoder();
        String decoded = rd.decode(toDecode,3);
        System.out.println(decoded);
    }
}