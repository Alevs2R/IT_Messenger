package com.coder.Arithmetic;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Output stream of bits is used because we can't address specific bits in a byte-based system.
 * @author Georgy Dzesov
 */
public final class BitsOutputStream implements AutoCloseable {
	// The output stream
	private OutputStream output;
	private int currentByte;
	// How many bits left to fill in a byte
	private int numBitsFilled;

	public BitsOutputStream(OutputStream out) {
		output = out;
		currentByte = 0;
		numBitsFilled = 0;
	}

	/**
	 * This function writes the specific bit into the byte
	 * @param b is the bit to be written
	 */
	public void write(int b) throws IOException {
		if (b != 0 && b != 1)
			throw new IllegalArgumentException("Argument must be 0 or 1");
		currentByte = (currentByte << 1) | b;
		numBitsFilled++;
		if (numBitsFilled == 8) {
			output.write(currentByte);
			currentByte = 0;
			numBitsFilled = 0;
		}
	}


	/**
	 * Close the stream. If the byte is not fully
	 * written, fills with zeroes.
	 */
	public void close() throws IOException {
		while (numBitsFilled != 0)
			write(0);
		output.close();
	}
	
}
