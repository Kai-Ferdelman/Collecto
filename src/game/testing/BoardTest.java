package game.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import game.Ball;
import game.Board;

public class BoardTest {
	private Board board;
	/*
	 * Board used for testing
	5	3	4	2	5	3	6	
	
	4	6	3	4	3	1	2	
	
	5	3	2	1	2	6	5	
	
	4	1	4	0	4	1	4	
	
	5	6	2	1	5	6	2	
	
	3	1	5	4	6	5	3	
	
	6	3	6	2	1	2	1	
	 */
	@BeforeEach
	public void setUp() {
		board = new Board();
		
	}
	
	@Test
	public void testBoard() {
		// String  of board with integers.
		String boardString = "5342536463431253212654"
				+ "140414562156231546536362121";
		// Set if set board.
		board.setBoard(boardString);
		assertTrue(board.toString().equals(boardString));
		 //make move 17
		board.doMove(17);
		//check if after move there adjacent balls with same color to be withdrawn.
		List<Ball> listOfNeighbours = board.checkNeighbours(board);
		for (Ball ball: listOfNeighbours) {
			ball.remove();
		}
		assertEquals(listOfNeighbours.size(), 2);
		Board boardCopy = board.getCopy();
		//Check if making a copy actually copies the balls as well.
		assertEquals(boardCopy.toString(), board.toString());
	}
	@Test
	public void testPopulateBoard() {
		board.populateBoard();
		 //Test the length of board to string is indeed as expected
		assertEquals(board.toString().length(), 49);
		
		 
		 
	}
}
