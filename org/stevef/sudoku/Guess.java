package org.stevef.sudoku;

/**
 * 
 * With more domain knowledge I might have been able to omit this class.
 * 
 * Everything's open because 
 * @author steve
 *
 */
public class Guess {
	int row, col; // the cell
	int value;    // the guessed value
	
	Guess() {}
	
	Guess(final int r, final int c, final int v) {
		row = r;
		col = c;
		value = v;
	}
}
