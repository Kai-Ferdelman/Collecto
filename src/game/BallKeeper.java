package game;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Kai Ferdelman
 *
 */

public class BallKeeper {
	/** ArrayList of balls removed from the game and assigned to player 1. */
	List<Ball> player1Balls;
	/** ArrayList of balls removed from the game and assigned to player 2. */
	List<Ball> player2Balls;
	/** Name of player 1. */
	String player1;
	/** Name of player 2. */
	String player2;
	
	/**
	 * Constructs a BallKeeper, given the names of the 2 players.
	 * @param player1
	 * @param player2
	 */
	public BallKeeper(String player1, String player2) {
		player1Balls = new ArrayList<Ball>();
		player2Balls = new ArrayList<Ball>();
		this.player1 = player1;
		this.player2 = player2;
	}
	
	/**
	 * Returns the points for a single player given their name.
	 * Three balls of the same color count as a single point.
	 * @param player
	 * @return player.points
	 */
	public int getPoints(String player) {
		int points = 0;
		if (player.equals(player1)) {
			for (int i = 1; i < 7; i++) {
				int colorPoints = 0;
				for (Ball b : player1Balls) {
					if (b.getOriginalColor() == i) {
						colorPoints++;
					}
				}
				points += colorPoints / 3;
			}
		}
		if (player.equals(player2)) {
			for (int i = 1; i < 7; i++) {
				int colorPoints = 0;
				for (Ball b : player2Balls) {
					if (b.getOriginalColor() == i) {
						colorPoints++;
					}
				}
				points += colorPoints / 3;
			}
		}
		return points;
	}
	
	/**
	 * Returns the number of balls a player has given their name.
	 * @param player
	 * @return player.numOfBalls
	 */
	public int getNumOfBalls(String player) {
		if (player.equals(player1)) {
			return player1Balls.size();
		}
		if (player.equals(player2)) {
			return player2Balls.size();
		}
		return 0;
	}
	
	/**
	 * Adds a ball to the given players ball list.
	 * @param ball
	 * @param player
	 */
	public void addBall(Ball ball, String player) {
		if (player.equals(player1)) {
			if (!player1Balls.contains(ball)) {
				player1Balls.add(ball);
			}
		}
		if (player.equals(player2)) {
			if (!player2Balls.contains(ball)) {
				player2Balls.add(ball);
			}
		}
	}
	
	/**
	 * Determines the winner between the two players.
	 * First the points are taken into consideration.
	 * If they are the same for both players, the number of balls decides the winner.
	 * If the players have the same amount of balls its a draw.
	 * @return winner
	 */
	public String getWinner() {
		if (getPoints(player1) > getPoints(player2)) {
			return player1;
		} else if (getPoints(player2) > getPoints(player1)) {
			return player2;
		} else {
			if (getNumOfBalls(player1) > getNumOfBalls(player2)) {
				return player1;
			} else if (getNumOfBalls(player2) > getNumOfBalls(player1)) {
				return player2;
			} else {
				return "Draw";
			}
		}
	}

	/**
	 * Creates a deep copy of the BallKeeper object.
	 * @return new BallKeeper
	 */
	public BallKeeper copy() {
		// TODO Auto-generated method stub
		BallKeeper bk = new BallKeeper(String.valueOf(player1), String.valueOf(player2));
		for (Ball b : this.player1Balls) {
			Ball newBall = new Ball(b.getOriginalColor());
			newBall.remove();
			bk.addBall(newBall, this.player1);
		}
		for (Ball b : this.player2Balls) {
			Ball newBall = new Ball(b.getOriginalColor());
			newBall.remove();
			bk.addBall(newBall, this.player2);
		}
		return bk;
	}
}
