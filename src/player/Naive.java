package player;

import java.util.ArrayList;
import java.util.List;

import game.Board;
import game.Tuple;

/**
 * 
 * @author Kai Ferdelman
 * A naive AI strategy.
 * Uses random but valid moves to play.
 */

public class Naive implements Strategy {

	/**
	 * Returns a random valid single move for the given board.
	 * @param board
	 * @return move
	 */
	@Override
	public int determineMove(Board board) {
		List<Integer> singleMoves = getSingleMoves(board);
		if (!singleMoves.isEmpty()) {
			return singleMoves.get((int) (Math.random() * singleMoves.size()));
		}
		return -1;
	}
	
	/**
	 * Returns a random valid double move for the given board.
	 * @param board
	 * @return move
	 */
	@Override
	public Tuple<Integer, Integer> determineDoubleMove(Board board) {
		List<Tuple<Integer, Integer>> doubleMoves = getDoubleMoves(board);
		if (!doubleMoves.isEmpty()) {
			return doubleMoves.get((int) (Math.random() * doubleMoves.size()));
		}
		return null;
	}
	
	
	 /**
	 * Returns a list of valid single moves that can be applied to the given board.
	 * @param board
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
	 * Returns a list of valid double moves that can be applied to the given board.
	 * @param board
	 * @return move
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
