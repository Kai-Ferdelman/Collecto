package player;

import game.Board;
import game.Tuple;


/**
 * 
 * @author Kai Ferdelman
 *
 */

public class AI implements Player {

	/** The strategy used by this AI player. */
	Strategy strategy;
	/** The name of the AI player. */
	String name;
	/** The board used by the AI player. */
	Board board;
	
	/**
	 * Constructs a new AI player with a given strategy.
	 * Fills the board with invisible balls.
	 * @param strategy
	 */
	public AI(Strategy strategy) {
		this.strategy = strategy;
		board = new Board();
		board.fillEmpty(board);
	}
	
	/**
	 * Returns a single move.
	 * @return move
	 */
	public int determineSingleMove() {
		return strategy.determineMove(board);
		
	}
	
	/**
	 * Returns a double move.
	 * @return move
	 */
	public Tuple<Integer, Integer> determineDoubleMove() {
		return strategy.determineDoubleMove(board);
	}
	
	/**
	 * Returns the AIs name.
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * Sets the AIs name.
	 * @param name
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the AIs board.
	 * @return board
	 */
	@Override
	public Board getBoard() {
		return board;
	}

	/**
	 * Does the given move on the AIs board.
	 * @param move
	 */
	@Override
	public void doMove(int move) {
		board.doMove(move);
		
	}
	
	/**
	 * Sets the board based on a board string.
	 * @param board string
	 */
	@Override
	public void setBoard(String board) {
		this.board.setBoard(board);
	}

	/**
	 * Unused by the AI player.
	 */
	@Override
	public String getHint() {
		//Empty for AI player
		return null;
	}

}
