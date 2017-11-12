/**
 * This class represents decoder for the transmitted sequence that was encoded using Repetition method
 */

public class RepetitionDecoder {

    public RepetitionDecoder(){} //constructor

    /**
     * This method decodes transmitted sequence that was encoded using Repetition method
     * @param noisySequence - transmitted sequence with some bits flipped
     * @param n - number of repetitions
     * @return - decoded sequence
     */
    public String decode(String noisySequence, int n){
        String decodedSequence = new String();
        int nz = 0; //number of zeroes
        int no = 0; //number of ones
        for(int i=0; i<noisySequence.length(); i+=n){ //for every n-length-tuple
            for(int k=0; k<n; k++){ //checking every symbol in n-length tuple
                if(noisySequence.charAt(i+k) == '0') nz++;
                else no++;
            }
            if(no>nz) decodedSequence += "1"; //if number of ones in a tuple is bigger => 1
            else decodedSequence += "0"; //else => 0
            nz=0; no=0;
        }
        return decodedSequence;
    }
}