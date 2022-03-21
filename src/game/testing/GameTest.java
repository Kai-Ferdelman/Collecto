package game.testing;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import communication.server.ClientHandler;
import game.Collecto;

public class GameTest {
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
	private Collecto game;
	private ClientHandler p1;
	private ClientHandler p2;
	
	@BeforeEach
	public void setUp() {
		//remember to adjust ClientHandler to make work
		p1 = new ClientHandler(null, null);
		p2 = new ClientHandler(null, null);
		p1.setUserName("p1");
		p2.setUserName("p2");
		game = new Collecto(p1, p2);
	}
	
	@Test
	public void testBoard() {
		assertFalse(game.doMove(4, p1));
		assertTrue(game.doMove(3, p1));
		assertFalse(game.doMove(3, p1));
		assertTrue(game.doMove(3, p2));
	}
	
	@Test
	public void testMoves() {
		List<Integer> testMoves = new ArrayList<Integer>();
		testMoves.add(3);
		testMoves.add(10);
		testMoves.add(17);
		testMoves.add(24);
		List<Integer> moves = game.getSingleMoves();
		for (int i : testMoves) {
			assertTrue(moves.contains(i));
		}
	}

}
