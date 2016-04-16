package org.stevef.sudoku;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


/**
 * A Sudoku puzzle. This class has an array to hold the cell values (or
 * lack thereof) and various structures to aid in solution.
 * 
 * Recursive for handling guesses. Basically, get as far as we can
 * and then kick it off as a new puzzle to be solved by guessing at
 * some value. If it works, great, pass the solution back up. If not,
 * pass up a fail status so the higher level can flag this as a bad
 * guess and try again.
 * 
 * @author steve
 *
 */
public class Puzzle {
	Logger log = Logger.getLogger("sudoku.Puzzle");

	private RowCol rows[];
	private RowCol cols[];
	private Miniblock miniblocks[][];
	private Cell cells[][];
	private Guess currentGuess;


	public Puzzle() {
		rows = new RowCol[9];
		for (int i=0; i<9; ++i)
			rows[i] = new RowCol();
		cols = new RowCol[9];
		for (int i=0; i<9; ++i)
			cols[i] = new RowCol();
		miniblocks = new Miniblock[3][3];
		for (int i=0; i<3; ++i)
			for (int j=0; j<3; ++j)
				miniblocks[i][j] = new Miniblock();
		cells = new Cell[9][9];
		for (int i=0; i<9; ++i)
			for (int j=0; j<9; ++j)
				setCell(i, j, null);
	}

	/**
	 * Copy constructor, used for recursion in the solve() method.
	 * @param previous
	 */
	public Puzzle(final Puzzle previous) {
		log.fine("Recursion constructor:");
		rows = new RowCol[9];
		for (int i=0; i<9; ++i)
			rows[i] = new RowCol();
		cols = new RowCol[9];
		for (int i=0; i<9; ++i)
			cols[i] = new RowCol();
		miniblocks = new Miniblock[3][3];
		for (int i=0; i<3; ++i)
			for (int j=0; j<3; ++j)
				miniblocks[i][j] = new Miniblock();
		cells = new Cell[9][9];
		for (int i=0; i<9; ++i)
			for (int j=0; j<9; ++j)
				setCell(i, j, previous.cells[i][j].getValue());
		log.fine("About to copy ("+previous.currentGuess.row+","+previous.currentGuess.col+","+previous.currentGuess.value+") into the cell array");
		Cell guessCell = cells[previous.currentGuess.row][previous.currentGuess.col];
		log.fine("  That cell currently has value ("+guessCell.getValue()+") and possible values ("+guessCell.listPossibleValues()+")");
		guessCell.setValue(previous.currentGuess.value);
		cells[previous.currentGuess.row][previous.currentGuess.col].excludePossibleValue(previous.currentGuess.value); // not sure about this
	}


	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("Please provide an input file name on the command line.");
			System.exit(1);
		}

		Puzzle puzzle = new Puzzle();
		if (puzzle.readInput(args[0])) {
			int ret = puzzle.solve();
			if (ret == 0)
				puzzle.writeOutput(puzzle.makeOutfileName(args[0]));
		}
	}

	/**
	 * Create a Cell, passing in references to the "owning" row, column, and 3x3
	 * block as well as the value.
	 * 
	 * @param row
	 *            [0-8], counting from the top
	 * @param col
	 *            [0-8], counting from the left
	 * @param value
	 *            the digit or null
	 */
	void setCell(final int row, final int col, final Integer value) {
		int miniblockRow = row/3;
		int miniblockCol = col/3;
		cells[row][col] = new Cell(value, cols[col], rows[row], miniblocks[miniblockRow][miniblockCol]);
	}

	/**
	 * Read the source file and load the puzzle data from it.
	 * This is a fragile method - it expects that the file will be in the
	 * correct formta.
	 * @param filename
	 * @return true=file read, false=error
	 */
	boolean readInput(final String filename) {
		try {
			FileReader fr = new FileReader(filename);
			BufferedReader br = new BufferedReader(fr);
			for (int row=0; row<9; ++row) {
				String line = br.readLine();
				if (line.length() < 9)
					throw new RuntimeException("Not enough data in row "+(row+1));
				for (int col=0; col<9; ++col) {
					char c = line.charAt(col);
					if (c=='X')
						cells[row][col].setValue(null);
					else if (Character.isDigit(c)) {
						if (!cells[row][col].setValue(Character.getNumericValue(c)))
							throw new RuntimeException("Digit already used ");
					}
					else {
						throw new RuntimeException("Invalid character at ("+(row+1)+","+(col+1)+")");
					}
				}
			}
			br.close();
			return true;
		}
		catch (FileNotFoundException ex) {
			System.err.println("File not found for reading: "+filename);
			return false;
		}
		catch (IOException ex) {
			ex.printStackTrace();
			return false;
		}
		catch (RuntimeException ex) {
			System.err.println("Problem reading the input: "+ex.getMessage());
			return false;
		}
	}

	/**
	 * Write a simple grid to the named file
	 * @param filename
	 */
	void writeOutput(final String filename) {
		try {
			PrintStream ps = new PrintStream(filename);

			for (int i=0; i<9; ++i) {
				for (int j=0; j<9; ++j) {
					Integer val = cells[i][j].getValue();
					if (val == null)
						ps.print("X");
					else
						ps.print(val);
				}
				ps.println();
				ps.flush();
			}
		}
		catch (IOException ex) {
			System.err.println("Problem writing the output");
			ex.printStackTrace();
		}
	}

	/**
	 * Print the 9x9 puzzle. For development only.
	 */
	void dump() {
		String debugStr = "";
		for (int i=0; i<9; ++i) {
			debugStr = debugStr+"\n"+(i+1)+"   ";
			for (int j=0; j<9; ++j) {
				Integer val = cells[i][j].getValue();
				if (val == null)
					debugStr = debugStr+"X";
				else
					debugStr = debugStr+val;
			}
		}
		log.fine(debugStr);
	}

	/**
	 * This method is subject to change. It meets the written requirements
	 * but the requirement may be incorrect.
	 * @param filename
	 * @return
	 */
	String makeOutfileName(final String filename) {
		return filename+".sln.txt";
	}
	
	/**
	 * Fill in as much of the puzzle as possible.
	 * 
	 * First find all of the forced moves, cells which have only one possible
	 * value because of digits filled in elsewhere or because of other excluded
	 * values. This is done in a loop.
	 * 
	 * Second, find the first open cell, which will have more than one possible
	 * value, and guess one of its possibilities. Then create a new Puzzle
	 * object, fill in the guessed value in the selected cell, and try to solve
	 * the new Puzzle. Recurse in this fashion as deep as necessary.
	 * 
	 * If solve() gets to a point where no solution is possible -- generally
	 * meaning that a cell has no possible value -- it returns a non-0 value. If
	 * this was a recursively-created Puzzle, the upper layer marks the value as
	 * "not possible" for the worked-on cell. In practice, this means starting
	 * the loop over again.
	 * 
	 * There is no depth test for the recursion. The maximum possible recursion
	 * depth would be 80 with an initially empty puzzle. This has not presented
	 * a problem in testing. In practice the maximum depth should be about 5,
	 * which should be able to run even on a constrained system.
	 * 
	 * @return
	 *   0 - success
	 *   1 - internal contradiction
	 *   2 - some other inability to solve
	 */
	int solve() {
		boolean notDeadYet = true;

		while (notDeadYet) {
			notDeadYet = false; // stop the loop unless there's a reason to keep going

			boolean keepFindingForcedValues = true;
			while (keepFindingForcedValues) {
				keepFindingForcedValues = false;
				log.fine("Looking for a forced play");
				int numprinted = 0;
				for (int i=0; i<9; ++i)
					for (int j=0; j<9; ++j) {
						int numPossible = cells[i][j].checkNeighbors();
						if (numPossible>0 && (numprinted++ < 3))
							log.fine("Cell "+i+","+j+" can have "+numPossible+" values ("+cells[i][j].listPossibleValues()+")");
						if (numPossible==1) {
							cells[i][j].setOnlyPossibleValue();
							if (!keepFindingForcedValues)
								log.fine("Found at least one cell with only one possible value");
							keepFindingForcedValues = true; // with this cell filled in, go around for another solution
						}
					}
			}

			// Done with the forced plays. See if we're done. If not, see about a guess.
			boolean guessLoop = findFirstOpenCell() != null;

			while (guessLoop) {
				guessLoop = false;

				List<Integer> firstOpen = findFirstOpenCell();
				if (firstOpen != null) {
					// There's an unfilled cell. Iterate over its possible values,
					// guessing that each is the solution and seeing if we can finish
					// the puzzle. If yes, bubble it up the recursion stack. If no,
					// just exit and that will return a failure code.
					int i = firstOpen.get(0);
					int j = firstOpen.get(1);
					int v = cells[i][j].getFirstPossibleValue();
					
					if (v == -1) {
						log.fine("solve() has run into a dead end at cell ("+i+","+j+")");
						return 1; // this cell doesn't have any possible value
					}

					if (	currentGuess != null &&
							i==currentGuess.row && // was col
							j==currentGuess.col && // was row
							v==currentGuess.value) {
						// At this point, nothing seems needed. Might want to report it as a problem.
					}
					else {
						currentGuess = new Guess(i, j, v);
						log.fine("About to recurse - cell("+i+","+j+") <- "+v);
						Puzzle recursed = new Puzzle(this);
						int retval = recursed.solve();
						log.fine("Popped out of a recursion");
						if (retval == 0) {
							// Success. Put the guess value into the cell array, exit
							// the guess loop, and do another round of the outer loop.
							if (!cells[i][j].setValue(v))
								throw new RuntimeException("attempted to set an already-used value in solve() recursion");
							guessLoop = false;
							
							log.fine("Result of solving the recursion:"+retval);
							recursed.dump(); // debugging
						}
						else {
							cells[i][j].excludePossibleValue(v);
							log.fine("That recursion failed.");
						}
					}
				}
				notDeadYet = true;
				dump();
			}
		}

		if (findFirstOpenCell() == null)
			return 0;
		else {
			log.fine("solve() returning status 2");
			return 2;
		}
	}
	
	/**
	 * Scan through the <tt>cells</tt> array for a Cell which has not been
	 * filled yet. 
	 * 
	 * @return Y coord (row), X coord (col) in a List<>, or null if there is no empty cell
	 */
	List<Integer> findFirstOpenCell() {
		for (int i=0; i<9; ++i)
			for (int j=0; j<9; ++j)
				if (cells[i][j].getValue() == null) {
					log.fine("First open cell is ("+i+","+j+"). Possible values are"+cells[i][j].listPossibleValues());
					List<Integer> ret = new ArrayList<Integer>();
					ret.add(i);
					ret.add(j);
					return ret;
				}
		return null;
	}
}
