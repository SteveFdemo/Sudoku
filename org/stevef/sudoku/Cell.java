package org.stevef.sudoku;

/**
 * Data pertaining to a single cell in a Sudoku puzzle. This data is basically
 * the value, if known, and a list of values that this cell can't have.
 * 
 * I chose not to have the Cell aware of its location in the puzzle. This
 * appears to have been an error, as it's led to extra code surrounding its use.
 * 
 * Instead, every Cell object has references back to its "owners" - the row,
 * column, and 3x3 block which contain the row. The owners are updated or
 * queried as needed.
 * 
 * @author steve
 * 
 */
public class Cell {
	private RowCol owningCol = null;
	private RowCol owningRow = null;
	private Miniblock owningMiniblock = null;
	private Integer value = null;
	private boolean cantbe[] = {false, false, false, false, false, false, false, false, false};


	Cell(final Integer digit, final CellGroup col, final CellGroup row, final CellGroup block) {
		owningCol = (RowCol)col;
		owningRow = (RowCol)row;
		owningMiniblock = (Miniblock)block;
		setValue(digit);
	}

	/**
	 * Default constructor. It does nothing but needs to exist because there's a
	 * non-empty constructor.
	 */
	Cell() {}

	/**
	 * Set the cell's value, then tell its owners about it.
	 * 
	 * @param digit
	 * @return true if everything went well, false if one of the owners already
	 *         had that value.
	 */
	boolean setValue(final Integer digit) {
		value = digit;
		boolean ret1 = owningCol.setNumberUsed(value);
		boolean ret2 = owningRow.setNumberUsed(value);
		boolean ret3 = owningMiniblock.setNumberUsed(value);
		return ret1 & ret2 & ret3;
	}

	/**
	 * If a cell has only one possible value (ie, isn't in the cell's row,
	 * column, or 3x3 group and hasn't been excluded for some other reason), set
	 * it.
	 * 
	 * Throws an exception if anything goes wrong. This should never happen, as
	 * the method is used in practice. A possible change is to return false if
	 * there's a problem and change the caller to just call this method without
	 * necessarily checking the precondition, and test the return value for
	 * further handling.
	 */
	void setOnlyPossibleValue() {
		if (checkNeighbors() > 1)
			throw new RuntimeException("not possible to determine a single possible value");
		for (int number=1; number<10; ++number) {
			if (!cantbe[number-1])
				if (!setValue(number))
					throw new RuntimeException("Attempted to set a value which the group already has");
		}
	}

	/**
	 * Find a possible value for a cell which doesn't have a value set yet. This
	 * is to be used in guessing a value when the puzzle cannot be solved simply
	 * by filling in one forced value after another.
	 * 
	 * @return the value to go in the cell, in the range [1,9]
	 */
	int getFirstPossibleValue() {
		for (int number=1; number<10; ++number) {
			if (!cantbe[number-1])
				return number;
		}
		return -1; // should never get here, as this method is meant to be used
	}

	/** 
	 * Make a space-separated list of possible values for this cell. For 
	 * development and debugging. Not needed for normal use.
	 * 
	 * NB: has a leading space, because that turned out to be convenient
	 * @return A list of digits or "*none*"
	 */
	String listPossibleValues() {
		String retval = "";
		for (int i=1; i<10; ++i)
			if (!cantbe[i-1])
				retval = retval+" "+i;
		if (retval.equals(""))
			retval = " *none*";
		return retval;
	}

	/**
	 * Mark that the Cell cannot have some value. This method's intended use
	 * is in guessing values and finding some that don't work.
	 * @param digit
	 */
	void excludePossibleValue(final int digit) {
		cantbe[digit-1] = true;
	}
	
	public Integer getValue() { return value; }

	/**
	 * Check the row, column, and 3x3 block owning this Cell for values that
	 * have already been used. Update this Cell's <tt>cantbe</tt> array, then
	 * return a count of non-excluded values.
	 * 
	 * NB: This method will not alter already-excluded values, such as from
	 * earlier failed guesses.
	 * 
	 * @return The number of possible values for this Cell
	 */
	public int checkNeighbors() {
		if (value != null)
			return 0;

		for (int number=1; number<10; ++number) {
			cantbe[number-1] |= owningCol.isNumberUsed(number);
			cantbe[number-1] |= owningRow.isNumberUsed(number);
			cantbe[number-1] |= owningMiniblock.isNumberUsed(number);
		}
		int count = 0;
		for (boolean b : cantbe)
			if (b)
				count++;
		return 9-count;
	}
}
