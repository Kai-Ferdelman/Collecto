package player.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import game.Board;
import player.AI;
import player.MinMax;
import player.Naive;
import player.SmartMinMax;
import player.Strategy;

public class AITest {
	private AI ai;
	String boardString;
	private Strategy strategy;
	@BeforeEach
	public void setUp() {
		new Board();
		boardString = "5342536463431253212654"
				+ "140414562156231546536362121";
		
	}
	
	@Test
	public void testNaiveStrategy() {
		strategy = (Naive) new Naive();
		ai = new AI(strategy);
		ai.setBoard(boardString);
		assertEquals(ai.getBoard().toString(), boardString);
		assertTrue(ai.determineSingleMove() >= 0);
		assertTrue(ai.determineSingleMove() <= 27);
		
	}
	@Test
	public void testMinMaxStrategy() {
		strategy =  (MinMax) new MinMax();
		ai = new AI(strategy);
		ai.setBoard(boardString);
		assertEquals(ai.getBoard().toString(), boardString);
		// after the first move the best possible move is 17move
		ai.doMove(ai.determineSingleMove());
		ai.updateBoard();
		assertEquals(10, ai.determineSingleMove());
		
	}
	@Test
	public void testSmartMinMaxStrategy() {
		
		strategy =  (SmartMinMax) new SmartMinMax();
		ai = new AI(strategy);
		ai.setBoard(boardString);
		assertEquals(ai.getBoard().toString(), boardString);
		ai.doMove(ai.determineSingleMove());
		ai.updateBoard();
		assertEquals(10, ai.determineSingleMove());
		
	}

}
