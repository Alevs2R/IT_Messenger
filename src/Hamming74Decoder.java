
/**
 * This class used for Hamming(7,4) decoding
 * @author Pavel Kozlov
 */
public class Hamming74Decoder {

    public Hamming74Decoder() {
    }

    public byte[] decoding(byte[] byteSequence){


        int[] bits = new int[8 * byteSequence.length];
        //converting all bytes to bits
        for (int i = 0; i < bits.length; i++) {
            bits[i] = (byteSequence[i / 8] >> (7 - i % 8)) & 1;
        }



        //number of bits should devides by 7
        if((bits.length % 7) != 0){
            int[] oldInitialBits = bits;
            bits = new int[oldInitialBits.length/7 * 7 + 7];
            for (int i = 0; i < 7 - (oldInitialBits.length % 7); i++) {
                bits[i] = 0;
            }
            for (int i = 0; i < oldInitialBits.length; i++) {
                bits[i + 7 - (oldInitialBits.length % 7)] = oldInitialBits[i];
            }
        }

        int[] codedBits = new int[4 * (bits.length/7) + (8 - (4 * (bits.length/7))%8)%8];
        for (int i = 0; i < codedBits.length; i++) {
            codedBits[i] = 0;
        }

        for (int i = 0; i < (bits.length/7); i++) {
            int[] tempBits = decode4bits(new int[]{bits[7*i],bits[7*i+1],bits[7*i+2],bits[7*i+3],
                    bits[7*i+4],bits[7*i+5],bits[7*i+6]});
            codedBits[4*i] = tempBits[0];
            codedBits[4*i+1] = tempBits[1];
            codedBits[4*i+2] = tempBits[2];
            codedBits[4*i+3] = tempBits[3];
        }

        byte[] encodedSequence = new byte[codedBits.length/8];

        String str;
        int count = 0;
        //converting bits to bytes
        for(int i=0; i < codedBits.length; i+=8){
            str = new String();
            for(int k=0; k<8; k++) {
                str += codedBits[i+k];
            }
            int tr = Integer.parseInt(str, 2);
            encodedSequence[count] = (byte) tr;
            count++;
        }

        return encodedSequence;


    }

    private byte[] decode4bits(byte[] initialBits){
        int e1 =(initialBits[0] + initialBits[2] + initialBits[4] + initialBits[6])%2;
        int e2 =(initialBits[1] + initialBits[2] + initialBits[5] + initialBits[6])%2;
        int e3 =(initialBits[3] + initialBits[4] + initialBits[5] + initialBits[6])%2;
        int err = e3 * 4 + e2 * 2 + e1;
        if(err > 0){
            initialBits[err - 1] += 1;
            initialBits[err - 1] %= 2;
        }

        return new byte[]{initialBits[2], initialBits[4], initialBits[5], initialBits[6]};
    }
}
