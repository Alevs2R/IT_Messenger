/**
 * This class used for Hamming(7,4) coding
 * @author Pavel Kozlov
 */
public class Hamming74Coder {

    public Hamming74Coder() {
    }

    public byte[] coding(byte[] initialBits){

        //number of bits should devides by 4
        if((initialBits.length % 4) != 0){
            byte[] oldInitialBits = initialBits;
            initialBits = new byte[oldInitialBits.length/4 * 4 + 4];
            for (int i = 0; i < 4 - (oldInitialBits.length % 4); i++) {
                initialBits[i] = 0;
            }
            for (int i = 0; i < oldInitialBits.length; i++) {
                initialBits[i + 4 - (oldInitialBits.length % 4)] = oldInitialBits[i];
            }
        }

        byte[] codedBits = new byte[7 * (initialBits.length/4)];
        for (int i = 0; i < (initialBits.length/4); i++) {
            byte[] tempBits = code4bits(new byte[]{initialBits[4*i],initialBits[4*i+1],initialBits[4*i+2],initialBits[4*i+3]});
            codedBits[7*i] = tempBits[0];
            codedBits[7*i+1] = tempBits[1];
            codedBits[7*i+2] = tempBits[2];
            codedBits[7*i+3] = tempBits[3];
            codedBits[7*i+4] = tempBits[4];
            codedBits[7*i+5] = tempBits[5];
            codedBits[7*i+6] = tempBits[6];
        }

        return codedBits;

    }

    private byte[] code4bits(byte[] initialBits){
        byte[] out_boolean = new byte[]{0,0,0,0,0,0,0};
        out_boolean[0] = initialBits[0];
        out_boolean[1] = initialBits[0];
        out_boolean[2] = initialBits[0];

        out_boolean[0] += initialBits[1];
        out_boolean[4] = initialBits[1];
        out_boolean[3] += initialBits[1];

        out_boolean[1] += initialBits[2];
        out_boolean[5] = initialBits[2];
        out_boolean[3] += initialBits[2];

        out_boolean[0] += initialBits[3];
        out_boolean[6] = initialBits[3];
        out_boolean[1] += initialBits[3];
        out_boolean[3] += initialBits[3];

        out_boolean[0] %= 2;
        out_boolean[1] %= 2;
        out_boolean[2] %= 2;
        out_boolean[3] %= 2;
        out_boolean[4] %= 2;
        out_boolean[5] %= 2;
        out_boolean[6] %= 2;

        return out_boolean;
    }

}
