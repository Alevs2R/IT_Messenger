package com.coder.Arithmetic;
import java.io.IOException;
import java.io.OutputStream;


/**
 * This class is used for Arithmetic Coding decompression.
 * @author Georgy Dzesov
 */
public class ArithmeticDecompressor extends ArithmeticBase {

	private BitsInputStream input;
	// The current code
	private long code;

	void decompress(BitsInputStream in, OutputStream out) throws IOException {
		input = in;
		code = 0;
		for (int i = 0; i < SIZE; i++)
			code = code << 1 | readCodeBit();
		CodingTable table = new CodingTable();
		int symbol = read(table);
		while (symbol != 256) {
			out.write(symbol);
			table.increment(symbol);
			symbol = read(table);
		}
	}

	/**
	 * This function is used to decode the symbol based on a coding table
	 * @param table is the not-null coding table
	 * @return the next symbol
	 */
	public int read(CodingTable table) throws IOException {
		long total = table.getTotal();
		long range = high - low + 1;
		long offset = code - low;
		long value = ((offset + 1) * total - 1) / range;
		// We need to find the highest symbol suck that the low of that symbol is less or then value
		int start = 0;
		int end = table.getSymbolLimit();
		while (end - start > 1) {
			int middle = (start + end) >>> 1;
			if (table.getLow(middle) > value)
				end = middle;
			else
				start = middle;
		}
		int symbol = start;
		update(table, symbol);
		return symbol;
	}

	/**
	 * This function shifts the bits of the code when highest bits are equal
	 */
	protected void shift() throws IOException {
		code = ((code << 1) & MASK) | readCodeBit();
	}

	/**
	 * This function deals with the underflow by freeing the space for a new bit
	 * using bitwise shifting
	 */
	protected void underflow() throws IOException {
		code = (code & HIGH_MASK) | ((code << 1) & (MASK >>> 1)) | readCodeBit();
	}

	/**
	 * This function returns the next bit of the input stream
	 * @return if the end, return -1, otherwise return the next bit
	 */
	private int readCodeBit() throws IOException {
		int temp = input.read();
		if (temp == -1)
			temp = 0;
		return temp;
	}
}
