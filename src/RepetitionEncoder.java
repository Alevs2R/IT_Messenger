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
        byte[] encodedSequence = new byte[byteSequence.length * n];
        int count = 0;
        for(int i=0; i<byteSequence.length*n; i+=n){ //for every symbol in a sequence
            for(int k=0; k<n; k++){ //repeat it n times
                encodedSequence[i+k] = byteSequence[count];
            }
            count++;
        }
        return encodedSequence;
    }

}