package com.coder.Arithmetic;
import java.io.IOException;
import java.io.InputStream;

/**
 * This class is used for Arithmetic Coding compression.
 * @author Georgy Dzesov
 */
public class ArithmeticCompressor extends ArithmeticBase {

	private BitsOutputStream output;
	private int flowBits;


	void compress(InputStream in, BitsOutputStream out) throws IOException {
		output = out;
		flowBits = 0;
		CodingTable table = new CodingTable();
		int symbol = in.read();
		while (symbol != -1) {
			write(table, symbol);
			table.increment(symbol);
			symbol = in.read();
		}
		write(table, 256);  //end of the input
		finish();  // delete remaining bits
	}

	public void finish() throws IOException {
		output.write(1);
	}

	/**
	 * Updates the CodingTable and changes the range of values
	 * @param table is the coding table
	 * @param symbol symbol that needs to be encoded
	 */
	public void write(CodingTable table, int symbol) throws IOException {
		update(table, symbol);
	}

	/**
	 * This function is called when underflow occurs. It counts
	 * the amount of bits that are being underflowed.
	 */
	protected void underflow() {
		flowBits++;
	}

	/**
	 * This function deals with underflow bits
	 */
	protected void shift() throws IOException {
		int bit = (int)(low >>> (SIZE - 1));
		output.write(bit);
		while(flowBits > 0) {
			output.write(bit ^ 1);
			flowBits--;
		}
	}


}
