/**
 * This class represents repetition encoder for a bit sequence
 */

public class RepetitionEncoder {

    public RepetitionEncoder(){} //constructor

    /**
     * This method encodes bit sequence using Repetition method
     * @param byteSequence - sequence to be encoded
     * @param n - number of repetitions, MUST BE ODD
     * @return byte[] encodedSequence - encoded sequence
     */
    public byte[] encode(byte[] byteSequence, int n){
        int[] bits = new int[8 * byteSequence.length];
        //converting all bytes to bits
        for (int i = 0; i < bits.length; i++) {
            bits[i] = (byteSequence[i / 8] >> (7 - i % 8)) & 1;
        }
        int[] encodedBits = new int[bits.length * n];
        int count = 0;
        //applying repetition
        for(int i=0; i<bits.length*n; i+=n){ //for every symbol in a sequence
            for(int k=0; k<n; k++){ //repeat it n times
                encodedBits[i+k] = bits[count];
            }
            count++;
        }
        byte[] encodedSequence = new byte[encodedBits.length/8];
        String str;
        count = 0;
        //converting bits to bytes
        for(int i=0; i<encodedBits.length; i+=8){
            str = new String();
            for(int k=0; k<8; k++) {
                str += encodedBits[i+k];
            }
            int tr = Integer.parseInt(str, 2);
            encodedSequence[count] = (byte) tr;
            count++;
        }
        return encodedSequence;
    }

}