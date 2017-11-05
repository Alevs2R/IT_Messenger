
/**
 * This class used for Hamming(7,4) decoding
 * @author Pavel Kozlov
 */
public class Hamming74Decoder {

    public Hamming74Decoder() {
    }

    public int[] decoding(int[] initialBits){

        //number of bits should devides by 7
        if((initialBits.length % 7) != 0){
            int[] oldInitialBits = initialBits;
            initialBits = new int[oldInitialBits.length/7 * 7 + 7];
            for (int i = 0; i < 7 - (oldInitialBits.length % 7); i++) {
                initialBits[i] = 0;
            }
            for (int i = 0; i < oldInitialBits.length; i++) {
                initialBits[i + 7 - (oldInitialBits.length % 7)] = oldInitialBits[i];
            }
        }

        int[] codedBits = new int[4 * (initialBits.length/7)];
        for (int i = 0; i < (initialBits.length/7); i++) {
            int[] tempBits = decode4bits(new int[]{initialBits[7*i],initialBits[7*i+1],initialBits[7*i+2],initialBits[7*i+3],
                    initialBits[7*i+4],initialBits[7*i+5],initialBits[7*i+6]});
            codedBits[4*i] = tempBits[0];
            codedBits[4*i+1] = tempBits[1];
            codedBits[4*i+2] = tempBits[2];
            codedBits[4*i+3] = tempBits[3];
        }

        return codedBits;

    }

    private int[] decode4bits(int[] initialBits){
        int e1 =(initialBits[0] + initialBits[2] + initialBits[4] + initialBits[6])%2;
        int e2 =(initialBits[1] + initialBits[2] + initialBits[5] + initialBits[6])%2;
        int e3 =(initialBits[3] + initialBits[4] + initialBits[5] + initialBits[6])%2;
        int err = e3 * 4 + e2 * 2 + e1;
        if(err > 0){
            initialBits[err - 1] += 1;
            initialBits[err - 1] %= 2;
        }

        return new int[]{initialBits[2], initialBits[4], initialBits[5], initialBits[6]};
    }
}
