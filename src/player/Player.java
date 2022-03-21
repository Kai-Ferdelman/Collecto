package player;

import java.util.List;

import game.Ball;
import game.Board;

public interface Player {
	/**
	 * Used to get the name of the player outside the class.
	 * @return name The name of the player.
	 */
	public String getName();
	/**
	 * Given a String parameter, sets the player's name to that String.
	 * @param name The name of the player.
	 */
	public void setName(String name);
	/**
	 * Used to get the board outside the class.
	 * @return Board The board object of the player.
	 * @requires board != null.
	 */
	public Board getBoard();
	/**
	 * Makes a move in the board object of the player.
	 * @param move Integer presenting a possible move.
	 * @requires move > -1 & move < 28.
	 * @ensure getBoard() != \old(getBoard()).
	 */
	public void doMove(int move);
	/**
	 * Given a string with integers, it sets the object board of the player
	 * to the given string board.
	 * @param board String of integer representing a initial board.
	 * @requires board.length() == 49
	 * 					Integer.parseInt(board) to not throw NumberFormatException.
	 * @ensures getBoard() != null
	 */
	public void setBoard(String board);
	/**
	 * After each move this method withdraw the balls with adjacents balls off the board.
	 * @ensure getBoard() != \old(getBoard()).
	 */
	public default void updateBoard() {
		Board board = getBoard();
		List<Ball> list = board.checkNeighbours(board);
		for (Ball ball: list) {
			ball.remove();
		}
	}
	/**
	 * Used by the Human player to get the hints during a game.
	 * @return String move single move: (integer move) or double move 
	 * 			if not single move available: (int move 1, int move 2).
	 */
	public String getHint();

}
