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
 * A MinMax strategy for the AI player.
 * The strategy returns the best possible move calculated by the MinMax algorithm.
 * The depth can be individually set.
 */
public class MinMax implements Strategy {
	
	/** The desired depth the algorithm should go to. */
	int level;
	/** The BallKeeper used by the AI. */
	BallKeeper bk;
	
	/**
	 * Constructs a MinMax strategy with a new BallKeeper and default level of 1.
	 */
	public MinMax() {
		level = 1;
		bk = new BallKeeper("p1", "p2");
	}
	
	/**
	 * Sets the level of the MinMax algorithm.
	 * @param level
	 */
	public void setLevel(int level) {
		this.level = level;
	}
	
	//----------------------------Strategy------------------------------------
	
	/**
	 * Returns the best valid single move according to the algorithm.
	 * Calculates a score for all possible moves and selects the best.
	 * @param board
	 * @return move
	 */
	@Override
	public int determineMove(Board board) {
		
		List<Integer> singleMoves = getSingleMoves(board);
			
		if (!singleMoves.isEmpty()) {
			int maxScore = -10;
			int bestMove = -1;
			for (int move : singleMoves) {
				Board testBoard = board.getCopy();
				BallKeeper testBK = bk.copy();
				testBoard.doMove(move);
				for (Ball b : testBoard.checkNeighbours(testBoard)) {
					b.remove();
					testBK.addBall(b, "p1");
					
				}
				int score  = minMax(false, 0, testBoard, testBK);
				if (score > maxScore) {
					maxScore = score;
					bestMove = move;
				}
			}
			return bestMove;
		}
		return -1;
	}
	
	/**
	 * Returns the best valid double move according to the algorithm.
	 * Calculates a score for all possible moves and selects the best.
	 * @param board
	 * @return move
	 */
	@Override
	public Tuple<Integer, Integer> determineDoubleMove(Board board) {
		
		List<Tuple<Integer, Integer>> doubleMoves = getDoubleMoves(board);
		
		if (!doubleMoves.isEmpty()) {
			int maxScore = -10;
			Tuple<Integer, Integer> bestMove = null;
			for (Tuple<Integer, Integer> move : doubleMoves) {
				Board testBoard = board.getCopy();
				BallKeeper testBK = bk.copy();
				testBoard.doMove(move);
				for (Ball b : testBoard.checkNeighbours(testBoard)) {
					b.remove();
					testBK.addBall(b, "p1");
				}
				int score  = minMax(false, 0, testBoard, testBK);
				if (score > maxScore) {
					maxScore = score;
					bestMove = move;
				}
			}
			return bestMove;
		}
		return null;
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
	int minMax(boolean player1, int depth, Board board, BallKeeper ballKeeper) {
		int d = depth + 1;
		if (d <= level * 2) {
			List<Integer> singleMoves = getSingleMoves(board);
			if (!singleMoves.isEmpty()) {
				for (int move : singleMoves) {
					Board testBoard = board.getCopy();
					BallKeeper testBK = ballKeeper.copy();
					testBoard.doMove(move);
					for (Ball b : testBoard.checkNeighbours(testBoard)) {
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
						Board testBoard = board.getCopy();
						BallKeeper testBK = ballKeeper.copy();
						testBoard.doMove(move);
						for (Ball b : testBoard.checkNeighbours(testBoard)) {
							b.remove();
							if (player1) {
								ballKeeper.addBall(b, "p1");
							} else {
								ballKeeper.addBall(b, "p2");
							}
						}
						minMax(!player1, d++,  board, testBK);
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
	int determineScore(BallKeeper ballKeeper) {
		
		int score = ballKeeper.getPoints("p1") * ballKeeper.getPoints("p1") 
				+ ballKeeper.getNumOfBalls("p1");
		
		return score;
	}

	/**
	 * Returns a list of valid single moves that can be applied to the main board.
	 * @return List of moves.
	 */
	public List<Integer> getSingleMoves(Board board) {
		List<Integer> validMoves = new ArrayList<Integer>();
		
		
		for (int i = 0; i < board.boardSize * 4; i++) {
			Board testBoard = board.getCopy();
			testBoard.doMove(i);
			if (!board.checkNeighbours(testBoard).isEmpty()) {
				validMoves.add(i);
			}
		}
		
		return validMoves;
	}
	
	/**
	 * Returns a list of valid double moves that can be applied to the main board.
	 * @return List of moves.
	 */
	public List<Tuple<Integer, Integer>> getDoubleMoves(Board board) {
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

}
