package org.stevef.sudoku;

/**
 *  A group of 9 Cells, arranged in any way.
 *  
 *  This class is to be extended for specific kinds of groups,
 *  namely row, column, and 3x3 block. It's set up this way in case
 *  any special handling or reporting is needed for different kinds
 *  of groups. At architecture time I don't know of any such, but
 *  my knowledge of Sudoku is minimal. Also, requirements might
 *  change in some way. 
 */
public abstract class CellGroup {
	/** Is the number set for sure in this group? */
	private boolean numberKnown[] = {false, false, false, false, false, false, false, false, false};

	public CellGroup() {
	}

	/**
	 * Tell the group that a digit appears in it.
	 * @param number
	 * @return false if the number was already set
	 */
	boolean setNumberUsed(final Integer number) {
		boolean retval = true;
		if (number != null) {
			retval = !(numberKnown[number-1]);
			numberKnown[number-1] = true;
		}
		return retval;
	}

	/**
	 * Has the number already been used in this group?
	 * @param number 1-9
	 * @return
	 */
	boolean isNumberUsed(final int number) {
		return numberKnown[number-1];
	}
}
