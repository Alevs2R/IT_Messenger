import java.io.IOException;

/**
 * This class provides behavior and some constant values for Compression and Decompression
 * with use of Arithmetic Coding Algorithm.
 * @author Georgy Dzesov
 */
public abstract class ArithmeticBase {

	// Maximum number of bits for "low" and "high" values
	protected final long SIZE = 32;
	// Maximum range of values (calculated depending on previous value)
	protected final long MAX = 1L << SIZE;
	// Just a mask for maximum range
	protected final long MASK = MAX - 1;
	// Mask for a highest bit
	protected final long HIGH_MASK = MAX >>> 1;
	// Mask for a second highest bit
	protected final long SHIGH_MASK = HIGH_MASK >>> 1;
	// Lowest value of the range
	protected long low;
	// Highest value of the range
	protected long high;


	ArithmeticBase() {
		low = 0;
		high = MASK;
	}

	/**
	 * This function is used to update the range of the possible values
	 * @param table is the input CodingTable
	 * @param symbol for which symbol to update the range
	 */
	public void update(CodingTable table, int symbol) throws IOException {
		long range = high - low + 1;
		long total = table.getTotal();
		// updating ranges according to formula
		long newLow  = low + table.getLow(symbol)  * range / total;
		long newHigh = low + table.getHigh(symbol) * range / total - 1;
		// set new values
		low = newLow;
		high = newHigh;
		// While highest bits are equal
		while (((low ^ high) & HIGH_MASK) == 0) {
			// to be implemented in the child classes
			shift();
			low = (low << 1) & MASK;
			high = ((high << 1) & MASK) | 1;
		}

		// While the low bigger than high (based on the second highest bit)
		while ((low & ~high & SHIGH_MASK) != 0) {
			// to be implemented in the child classes
			underflow();
			low = (low << 1) & (MASK >>> 1);
			high = ((high << 1) & (MASK >>> 1)) | HIGH_MASK | 1;
		}
	}

	/**
	 * This method should be called when highest bits are equal
	 */
	protected abstract void shift() throws IOException;

	/**
	 * This method should be called when second highest bit of low
	 * is bigger then the second highest bit of the high
	 */
	protected abstract void underflow() throws IOException;
	
}
