package com.coder.Repetition;

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
        int[] bits = new int[8 * noisySequence.length];
        //converting into bits
        for (int i = 0; i < bits.length; i++) {
            bits[i] = (noisySequence[i / 8] >> (7 - i % 8)) & 1;
        }
        int[] decodedBits = new int[bits.length/n];
        byte[] decodedSequence = new byte[decodedBits.length/8];
        int count=0;
        //getting rid of repetition
        for(int i=0; i<bits.length; i+=n){
            int nz=0;
            int no=0;
            for(int k=0; k<n; k++){
                if(bits[i+k]==0) nz++;
                if(bits[i+k]==1) no++;
            }
            if(nz>no) decodedBits[count]=0;
            else decodedBits[count]=1;
            count++;
        }
        String str;
        count = 0;
        //converting to byte
        for(int i=0; i<decodedBits.length; i+=8){
            str = new String();
            for(int k=0; k<8; k++) {
                str += decodedBits[i+k];
            }
            int tr = Integer.parseInt(str, 2);
            decodedSequence[count] = (byte) tr;
            count++;
        }
        return decodedSequence;
    }
}