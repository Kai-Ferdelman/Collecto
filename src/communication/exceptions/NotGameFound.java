package communication.exceptions;
/**
 * Used by the server when a game is not found in the games maps.
 * @author Kelvin Jaramillo.
 *
 */
public class NotGameFound extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8527416433678112477L;

	public NotGameFound(String msg) {
		super(msg);
	}

}
