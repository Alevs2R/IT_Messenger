/**
 * The table of symbol appearances for Arithmetic Coding.
 * @author Georgy Dzesov
 */
public final class CodingTable {

	private final int LIMIT = 257;
	// The frequency for each symbol. Its length is at least 1, and each element is non-negative.
	private int[] frequencies;
	// distribution distribution function
	private int[] distribution;
	private int total;


	public CodingTable() {
		//256 is reserved for 'end of file'
		frequencies = new int[LIMIT];
		total = 0;
		frequencies[0] = 0;
		for (int i = 0; i < LIMIT; i++) {
			frequencies[i] = 1;
			total++;
		}
		distribution = null;
	}

	/**
	 * This function calculates the cumulative distribution
	 */
	private void calculateDistribution() {
		distribution = new int[LIMIT + 1];
		int sum = 0;
		for (int i = 0; i < LIMIT; i++) {
			sum = frequencies[i] +  sum;
			distribution[i + 1] = sum;
		}
	}

	/**
	 * Returns maximum amount of symbols
	 * @return LIMIT value
	 */
	public int getSymbolLimit() {
		return LIMIT;
	}

	/**
	 * This function increments the frequency of the symbol
	 * @param symbol the symbol whose frequency needs to be incremented
	 */
	public void increment(int symbol) {
		total++;
		frequencies[symbol]++;
		distribution = null;
	}

	/**
	 * Returns the total symbols' frequency
	 * @return sum of all frequencies
	 */
	public int getTotal() {
		return total;
	}


	/**
	 * Returns the lowest acceptable value for a symbol
	 * @param symbol is the symbol that we need to calculate low for
	 * @return lowest legal value
	 */
	public int getLow(int symbol) {
		if (distribution == null)
			calculateDistribution();
		return distribution[symbol];
	}

	/**
	 * Returns the highest acceptable value for a symbol
	 * @param symbol is the symbol that we need to calculate high for
	 * @return highest legal value
	 */
	public int getHigh(int symbol) {
		if (distribution == null)
			calculateDistribution();
		return distribution[symbol + 1];
	}
}
