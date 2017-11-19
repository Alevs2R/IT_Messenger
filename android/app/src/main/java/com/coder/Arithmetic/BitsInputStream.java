package com.coder.Arithmetic;
import java.io.IOException;
import java.io.InputStream;


/**
 * Input stream of bits is used because we can't address specific bits in a byte-based system.
 * @author Georgy Dzesov
 */
public final class BitsInputStream implements AutoCloseable {

	// Stream of bytes that we need to read bits from
	private InputStream input;
	// equals to -1 if the end of the stream
	private int currentByte;
	// How many bites are left to read in a bit
	private int numBitsRemaining;

	BitsInputStream(InputStream in) {
		input = in;
		currentByte = 0;
		numBitsRemaining = 0;
	}

	/**
	 * Read a bit from the input stream
	 * @return -1 if the end of the stream, otherwise 0 or 1
	 */
	public int read() throws IOException {
		if (currentByte == -1)
			return -1;
		if (numBitsRemaining == 0) {
			currentByte = input.read();
			if (currentByte == -1)
				return -1;
			numBitsRemaining = 8;
		}
		if (numBitsRemaining <= 0)
			throw new AssertionError();
		numBitsRemaining--;
		return (currentByte >>> numBitsRemaining) & 1;
	}
	

	/**
	 * Close the stream
	 */
	public void close() throws IOException {
		input.close();
		currentByte = -1;
		numBitsRemaining = 0;
	}
	
}
