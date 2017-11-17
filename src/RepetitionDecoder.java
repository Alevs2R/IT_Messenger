import java.util.HashMap;
import java.util.Map;

/**
 * This class represents decoder for the transmitted sequence that was encoded using Repetition method
 */

public class RepetitionDecoder {

    public RepetitionDecoder(){} //constructor

    /**
     * This method decodes transmitted sequence that was encoded using Repetition method
     * @param noisySequence - transmitted sequence with some bits flipped
     * @param n - number of repetitions
     * @return byte[] decodedSequence - decoded sequence
     */
    public byte[] decode(byte[] noisySequence, int n){
        byte[] decodedSequence = new byte[noisySequence.length/n];
        int count=0;
        for(int i=0; i<noisySequence.length/n; i++){
            Map<Byte, Integer> frequency = new HashMap<>();
            for (int k = 0; k < n; k++) {
                if (frequency.containsValue(noisySequence[count+k])) {
                    frequency.put(noisySequence[count+k], frequency.get(noisySequence[count+k])+1);
                } else {
                    frequency.put(noisySequence[count + k], 1);
                }
            }
            byte finalByte = 0;
            int maxFreq = 0;
            for (byte currByte : frequency.keySet()) {
                if (maxFreq < frequency.get(currByte)){ //if frequency of current byte is higher than before
                    maxFreq = frequency.get(currByte); //update maximum frequency
                    finalByte = currByte; //update final byte
                }
            }
            decodedSequence[i] = finalByte;
            count+=n;
        }
        return decodedSequence;
    }
}