package game;

/**
 * 
 * @author Kai Ferdelman
 *
 */

public class Ball {
	/** The color the ball has while it is on the board. */
	private int color;
	/** The color of the ball when it was removed. */
	private int originalColor;
	
	/**
	 * Constructs a Ball with one of six colors.
	 * @param color
	 */
	public Ball(int color) {
		this.color = color;
	}
	
	/**
	 * Returns the balls color.
	 * @return this.color
	 */
	public int getColor() {
		return color;
	}
	
	/**
	 * Sets the balls color to a new color.
	 * @param color
	 */
	public void setColor(int color) {
		this.color = color;
	}
	
	/**
	 * Sets the balls color to 0 (invisible) and sets the originalColor variable.
	 * Only works if the ball is not removed yet.
	 */
	public void remove() {
		if (color != 0) {
			originalColor = color;
		}
		color = 0;
	}
	
	/**
	 * Returns the balls original color.
	 * @return originalColor
	 */
	public int getOriginalColor() {
		return originalColor;
	}
	
	/**
	 * Returns the ball as an object.
	 * @return this
	 */
	public Ball getBall() {
		return this;
	}
}
