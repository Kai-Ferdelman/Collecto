package player;

import java.util.ArrayList;
import java.util.List;

import game.Ball;
import game.BallKeeper;
import game.Board;
import game.Tuple;

/**
 * 
 * @author Kai Ferdelman
 *
 */

public class MinMaxThread implements Runnable {

	/** The desired depth the algorithm should go to. */
	int level;
	/** The score of the corresponding move. */
	int score;
	/** The board used to test the move. */
	Board testBoard;
	/** The ball keeper used to test the move. */
	BallKeeper bk;
	
	/**
	 * Constructs an instance of the MinMax thread to test a single move.
	 * @param level
	 * @param testBoard
	 * @param bk
	 */
	public MinMaxThread(int level, Board testBoard, BallKeeper bk) {
		this.level = level;
		this.testBoard = testBoard;
		this.bk = bk;
		score = -1;
	}
	
	@Override
	public void run() {
		score = minMax(false, 0, testBoard, bk);
	}
	
	/**
	 * Returns a score for the move it is applied on.
	 * Uses a recursive algorithm that terminates either when no more moves are available,
	 * or when maximum depth has been reached.
	 * @param player1 is it player 1s turn or not
	 * @param depth how many recursions have been made
	 * @param board of the previous depth
	 * @param ballKeeper of the previous depth 
	 * @return score
	 */
	synchronized int minMax(boolean player1, int depth, Board board, BallKeeper ballKeeper) {
		int d = depth + 1;
		if (d <= level * 2) {
			List<Integer> singleMoves = getSingleMoves(board);
			if (!singleMoves.isEmpty()) {
				for (int move : singleMoves) {
					Board testB = board.getCopy();
					BallKeeper testBK = ballKeeper.copy();
					testB.doMove(move);
					for (Ball b : testB.checkNeighbours(testB)) {
						b.remove();
						if (player1) {
							ballKeeper.addBall(b, "p1");
						} else {
							ballKeeper.addBall(b, "p2");
						}
					}
					minMax(!player1, d, board, testBK);
				}
			} else {
				List<Tuple<Integer, Integer>> doubleMoves = getDoubleMoves(board);
				if (!doubleMoves.isEmpty()) {
					for (Tuple<Integer, Integer> move : doubleMoves) {
						Board testB = board.getCopy();
						BallKeeper testBK = ballKeeper.copy();
						testB.doMove(move);
						for (Ball b : testB.checkNeighbours(testB)) {
							b.remove();
							if (player1) {
								ballKeeper.addBall(b, "p1");
							} else {
								ballKeeper.addBall(b, "p2");
							}
						}
						minMax(!player1, d, board, testBK);
					}
				}
			}
		}
		return determineScore(ballKeeper);
	}
	
	/**
	 * Calculates the score of a move.
	 * Checks the BallKeeper for collected Balls.
	 * Points are squared and added to number of balls.
	 * @param ballKeeper
	 * @return score
	 */
	synchronized int determineScore(BallKeeper ballKeeper) {
		
		int points = ballKeeper.getPoints("p1") * ballKeeper.getPoints("p1")
				+ ballKeeper.getNumOfBalls("p1");
		
		return points;
	}
	
	/**
	 * Returns a list of valid single moves that can be applied to the main board.
	 * @return List of moves.
	 */
	public synchronized List<Integer> getSingleMoves(Board board) {
		List<Integer> validMoves = new ArrayList<Integer>();
		
		
		for (int i = 0; i < board.boardSize * 4; i++) {
			Board testB = board.getCopy();
			testB.doMove(i);
			if (!board.checkNeighbours(testB).isEmpty()) {
				validMoves.add(i);
			}
		}
		
		return validMoves;
	}
	
	/**
	 * Returns a list of valid double moves that can be applied to the main board.
	 * @return List of moves.
	 */
	public synchronized List<Tuple<Integer, Integer>> getDoubleMoves(Board board) {
		List<Tuple<Integer, Integer>> validMoves = new ArrayList<Tuple<Integer, Integer>>();
		
		for (int i = 0; i < board.boardSize * 4; i++) {
			Board testBoard1 = board.getCopy();
			testBoard1.doMove(i);
			for (int o = 0; o < board.boardSize * 4; o++) {
				Board testBoard2 = testBoard1.getCopy();
				testBoard2.doMove(o);
				if (!board.checkNeighbours(testBoard2).isEmpty()) {
					validMoves.add(new Tuple<Integer, Integer>(i, o));
				}
			}
		}
		
		return validMoves;
	}
	
	public synchronized int getScore() {
		return score;
	}
}
