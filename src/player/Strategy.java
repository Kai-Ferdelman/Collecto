package player;

import game.Board;
import game.Tuple;

/**
 * 
 * @author Kai Ferdelman
 *
 */

public interface Strategy {
	/**
	 * defines an interface for the ai strategies.
	 */
	public static final String STRATEGYNAME = "";
	int determineMove(Board board);
	Tuple<Integer, Integer> determineDoubleMove(Board board);
}
