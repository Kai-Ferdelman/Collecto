package game;

/**
 * 
 * @author Kai Ferdelman
 *
 * @param <X>
 * @param <Y>
 */

public class Tuple<X, Y> { 
	/** First value of the Tuple. */
	public final X x; 
	/** Second value of the Tuple. */
	public final Y y; 
	
	/**
	 * Constructs a tuple which can store two values of definable type.
	 * @param x
	 * @param y
	 */
	public Tuple(X x, Y y) { 
		this.x = x; 
	    this.y = y; 
	} 
}