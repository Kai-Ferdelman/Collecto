package communication.exceptions;
/**
 * Used by the client doing a move in the players board.
 * Thrown when the move is not in between 0 and 27.
 * @author Kelvin Jaramillo.
 *
 */
public class MoveOutOfRange extends Exception {
	/**
	 * This exception is throw when a move is intended outside the board.
	 */
	private static final long serialVersionUID = -6583976398554102673L;

	public MoveOutOfRange() {
		super("Move out of board");
	}
	
}
