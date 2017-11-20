/**
 * This class used for Hamming(7,4) coding
 * @author Pavel Kozlov
 */

package com.coder.Hamming;

public class Hamming74Coder {

    public Hamming74Coder() {
    }

    public byte[] coding(byte[] byteSequence){

        int[] bits = new int[8 * byteSequence.length];
        //converting all bytes to bits
        for (int i = 0; i < bits.length; i++) {
            bits[i] = (byteSequence[i / 8] >> (7 - i % 8)) & 1;
        }


        int[] codedBits = new int[7 * (bits.length/4)];


        for (int i = 0; i < (bits.length/4); i++) {
            int[] tempBits = code4bits(new int[]{bits[4*i],bits[4*i+1],bits[4*i+2],bits[4*i+3]});
            codedBits[7*i] = tempBits[0];
            codedBits[7*i+1] = tempBits[1];
            codedBits[7*i+2] = tempBits[2];
            codedBits[7*i+3] = tempBits[3];
            codedBits[7*i+4] = tempBits[4];
            codedBits[7*i+5] = tempBits[5];
            codedBits[7*i+6] = tempBits[6];
        }

        if(codedBits.length % 8 != 0){
            int[] oldcodedBits = codedBits;
            codedBits = new int[(oldcodedBits.length/8+1) * 8];
            int cnst = codedBits.length-oldcodedBits.length;
            for (int i = 0; i < cnst; i++) {
                codedBits[i] = 0;
            }

            for (int i = 0; i < oldcodedBits.length; i++) {
                codedBits[cnst+i] = oldcodedBits[i];
            }
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

    private int[] code4bits (int[] initialBits){
        int[] out_boolean = new int[]{0,0,0,0,0,0,0};
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