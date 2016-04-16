package org.stevef.sudoku;

import static org.junit.Assert.*;

import org.junit.Test;



public class TPuzzle01 {

	// NB: To run this test, change the access of Puzzle.rows and Puzzle.cols to package (the default)
	/*
	@Test
	public void testSetCell() {
		System.out.println("Starting test and initializing output");
		Puzzle puzzle = new Puzzle();
		puzzle.setCell(0, 0, 1);
		puzzle.dump();
		assertEquals(false, puzzle.cols[0].complete());
		puzzle.setCell(0, 1, 2);
		puzzle.setCell(0, 2, 3);
		puzzle.setCell(0, 3, 4);
		puzzle.setCell(0, 4, 5);
		puzzle.setCell(0, 5, 6);
		puzzle.setCell(0, 6, 7);
		puzzle.setCell(0, 7, 8);
		puzzle.setCell(0, 8, 9);
		puzzle.dump();
		assertEquals(true, puzzle.rows[0].complete());
	}
	*/

	@Test
	public void readFile01() {
		Puzzle puzzle = new Puzzle();
		assertEquals(true, puzzle.readInput("/home/steve/Downloads/Sudoku/puzzle1.txt"));
	}
	
	@Test
	public void readFile02() {
		Puzzle puzzle = new Puzzle();
		assertEquals(false, puzzle.readInput("/home/steve/Downloads/Sudoku/doesntexist.txt"));
	}

	// Not a puzzle file at all
	@Test
	public void readFile03() {
		Puzzle puzzle = new Puzzle();
		assertEquals(false, puzzle.readInput("/home/steve/Downloads/Sudoku/bad2.txt"));
	}

	// Looks like a puzzle grid, but has bad characters
	@Test
	public void readFile04() {
		Puzzle puzzle = new Puzzle();
		assertEquals(false, puzzle.readInput("/home/steve/Downloads/Sudoku/bad3.txt"));
	}

	@Test
	public void processPuzzle01() {
		Puzzle puzzle = new Puzzle();
		puzzle.readInput("/home/steve/Downloads/Sudoku/puzzle1.txt");
		int ret = puzzle.solve();
		puzzle.dump();
		assertEquals(0, ret);
	}

	@Test
	public void processPuzzle02() {
		Puzzle puzzle = new Puzzle();
		puzzle.readInput("/home/steve/Downloads/Sudoku/puzzle2.txt");
		int ret = puzzle.solve();
		puzzle.dump();
		assertEquals(0, ret);
	}

	@Test
	public void processPuzzle03() {
		Puzzle puzzle = new Puzzle();
		puzzle.readInput("/home/steve/Downloads/Sudoku/puzzle3.txt");
		int ret = puzzle.solve();
		puzzle.dump();
		assertEquals(0, ret);
	}

	@Test
	public void processPuzzle04() {
		Puzzle puzzle = new Puzzle();
		puzzle.readInput("/home/steve/Downloads/Sudoku/puzzle4.txt");
		int ret = puzzle.solve();
		puzzle.dump();
		assertEquals(0, ret);
	}

	@Test
	public void processPuzzle05() {
		Puzzle puzzle = new Puzzle();
		puzzle.readInput("/home/steve/Downloads/Sudoku/puzzle5.txt");
		int ret = puzzle.solve();
		puzzle.dump();
		assertEquals(0, ret);
	}

	// Test case where the puzzle can't be solved
	@Test
	public void processBadPuzzle01() {
		Puzzle puzzle = new Puzzle();
		puzzle.readInput("/home/steve/Downloads/Sudoku/bad1.txt");
		int ret = puzzle.solve();
		puzzle.dump();
		assertEquals(1, ret);
	}
}
