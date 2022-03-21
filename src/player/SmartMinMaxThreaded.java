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
 * A MinMax strategy for the AI player implementing multi threading.
 * The strategy returns the best possible move calculated by the MinMax algorithm.
 * The depth can be individually set.
 * The smart MinMax AI uses a global BallKeeper to improve its capability.
 */

public class SmartMinMaxThreaded implements Strategy {

	/** The desired depth the algorithm should go to. */
	int level;
	/** The BallKeeper used by the AI. */
	BallKeeper bk;
	
	/**
	 * Constructs a MinMax strategy with a new BallKeeper and default level of 1.
	 */
	public SmartMinMaxThreaded() {
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
	 * Each move generates a new thread that runs its own minmax algorithm.
	 * Also does the move on the AIs board and evaluates it,
	 * adding adding some balls to its global BallKeeper.
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
				//create copies of board an bk
				Board testBoard = board.getCopy();
				BallKeeper testBK = bk.copy();
				//apply the move
				testBoard.doMove(move);
				//update the board and bk
				for (Ball b : testBoard.checkNeighbours(testBoard)) {
					b.remove();
					testBK.addBall(b, "p1");
					
				}
				//evaluate the move
				MinMaxThread mmThread = new MinMaxThread(level, testBoard, testBK);
				Thread thread = new Thread(mmThread);
				thread.start();
				try {
					thread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				int score  = mmThread.getScore();
				//check if its best score
				if (score > maxScore) {
					maxScore = score;
					bestMove = move;
				}
			}
			Board testBoard = board.getCopy();
			testBoard.doMove(bestMove);
			for (Ball b : board.checkNeighbours(board)) {
				b.remove();
				bk.addBall(b, "p1");
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
				//create copies of board an bk
				Board testBoard = board.getCopy();
				BallKeeper testBK = bk.copy();
				//apply the move
				testBoard.doMove(move);
				//update the board and bk
				for (Ball b : testBoard.checkNeighbours(testBoard)) {
					b.remove();
					testBK.addBall(b, "p1");
					
				}
				//evaluate the move
				MinMaxThread mmThread = new MinMaxThread(level, testBoard, testBK);
				Thread thread = new Thread(mmThread);
				thread.start();
				try {
					thread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				int score  = mmThread.getScore();
				if (score > maxScore) {
					maxScore = score;
					bestMove = move;
				}
			}
			Board testBoard = board.getCopy();
			testBoard.doMove(bestMove);
			for (Ball b : board.checkNeighbours(board)) {
				b.remove();
				bk.addBall(b, "p1");
			}
			return bestMove;
		}
		return null;
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
