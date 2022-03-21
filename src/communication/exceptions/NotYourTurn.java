package communication.exceptions;
/**
 * This exception is thrown when the players wants to
 *  make a move but it is not his turn.
 * @author Kelvin Jaramillo.
 *
 */
public class NotYourTurn extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2598873623579859557L;
	
	public NotYourTurn(String msg) {
		super(msg);
	}
}
