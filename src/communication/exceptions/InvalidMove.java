package communication.exceptions;
/**
 * Exception throw if a client tries to make a move but it is not a valid move.
 * A invalid move is if 0 <= move >= 27 or move
 *  does not change the state of the game.
 * @author Kelvin Jaramillo.
 *
 */
public class InvalidMove extends Exception {

	/**
	 * Exception is throw when a move is invalid.
	 */
	private static final long serialVersionUID = -4994792320381471790L;
	public InvalidMove(String msg) {
		super(msg);
	}
	
}
