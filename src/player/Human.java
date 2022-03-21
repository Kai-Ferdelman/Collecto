package player;

import java.util.ArrayList;
import java.util.List;

import game.Board;
import game.Tuple;

public class Human implements Player {
	private Board board;
	private String name;
	public Human() {
		board = new Board();
	}
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Board getBoard() {
		return this.board;
	}

	@Override
	public void doMove(int move) {
		board.doMove(move);
	}
	
	@Override
	public void setBoard(String board) {
		this.board = new Board();
		this.board.setBoard(board);	
	}
	/**
	 * Checks the board and return a List of integers representing
	 *  the single possible moves on the board of the player.
	 * @return List A list of type List<Integer> with possible single moves.
	 */
	private List<Integer> getSingleMoves() {
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
	 * Checks the board and return a tuple of integers representing
	 *  the double possible moves on the board of the player.
	 * @return List A list of type List<Tuple<Integer, Integer>> with possible double moves.
	 */
	private  List<Tuple<Integer, Integer>> getDoubleMoves() {
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
	@Override
	public String getHint() {
		String result = "";
		List<Integer> hint = getSingleMoves();
		List<Tuple<Integer, Integer>> hintDouble = getDoubleMoves();
		if (hint.size() == 0) {
			for (Tuple<Integer, Integer> movee: hintDouble) {
				result += " (" + movee.x + " " + movee.y + ") ";
			}
		} else {
			for (Integer mov: hint) {
				result += " (" + mov + ") ";
			}
		}
		return result;
	}

}
