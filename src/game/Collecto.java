package game;

import java.util.ArrayList;
import java.util.List;

import communication.server.ClientHandler;
import communication.server.Server;

/**
 * 
 * @author Kai Ferdelman
 *
 */

public class Collecto {
	/** The two players associated with this game. */
	ClientHandler player1, player2;
	/** The main game board. */
	Board board;
	/** The server on which this game is hosted. */
	Server server;
	/** The BallKeeper associated with this game, keeping track of the score. */
	BallKeeper ballKeeper;
	/** A boolean value indicating whether it is player 1s turn. */
	boolean player1Turn;
	
	/**
	 * Constructs a collecto game.
	 * Takes two players (Clienthandlers) as parameters.
	 * Creates a new board and populates it.
	 * Creates a new BallKeeper.
	 * @param player1
	 * @param player2
	 */
	public Collecto(ClientHandler player1, ClientHandler player2) {
		this.player1 = player1;
		this.player2 = player2;
		board = new Board();
		ballKeeper = new BallKeeper(player1.getUserName(), player2.getUserName());
		player1Turn = true;
		board.populateBoard();
	}
	
	/**
	 * Applies a given single move to the main board.
	 * First checks whether the move is legal and if its the given players turn.
	 * @param move
	 * @param player
	 * @return move legality
	 */
	public boolean doMove(int move, ClientHandler player) {
		if ((player1Turn && player == player1) || (!player1Turn && player == player2)) {
			if (testMove(move)) {
				board.doMove(move);
				player1Turn = !player1Turn;
				for (Ball b : board.checkNeighbours(board)) {
					b.remove();
					if (player1Turn) {
						ballKeeper.addBall(b, player1.getUserName());
					} else {
						ballKeeper.addBall(b, player2.getUserName());
					}
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Applies a given double move to the main board.
	 * First checks whether the move is legal and if its the given players turn.
	 * @param move
	 * @param player
	 * @return move legality
	 */
	public boolean doMove(Tuple<Integer, Integer> move, ClientHandler player) {
		if ((player1Turn && player == player1) || (!player1Turn && player == player2)) {
			if (testMove(move)) {
				board.doMove(move);
				player1Turn = !player1Turn;
				for (Ball b : board.checkNeighbours(board)) {
					b.remove();
					if (player1Turn) {
						ballKeeper.addBall(b, player1.getUserName());
					} else {
						ballKeeper.addBall(b, player2.getUserName());
					}
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Tests whether a given single move is legal without applying it to the main board.
	 * @param move
	 * @return move legality
	 */
	boolean testMove(int move) {
		Board testBoard = board.getCopy();
		testBoard.doMove(move);
		if (!board.checkNeighbours(testBoard).isEmpty()) {
			return true;
		}
		return false;
	}
	
	/**
	 * Tests whether a given double move is legal without applying it to the main board.
	 * @param move
	 * @return move legality
	 */
	boolean testMove(Tuple<Integer, Integer> move) {
		Board testBoard = board.getCopy();
		testBoard.doMove(move.x);
		testBoard.doMove(move.y);
		if (!board.checkNeighbours(testBoard).isEmpty()) {
			return true;
		}
		return false;
	}
	
	/**
	 * Returns a list of valid single moves that can be applied to the main board.
	 * @return List of moves.
	 */
	public List<Integer> getSingleMoves() {
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
	List<Tuple<Integer, Integer>> getDoubleMoves() {
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
	
	/**
	 * Checks if there are any valid moves left on the board.
	 * @return true if now moves available
	 */
	public boolean checkGameOver() {
		if (getSingleMoves().isEmpty() && getDoubleMoves().isEmpty()) {
			return true;
		}
		return false;
	}
	
	/**
	 * Returns the name of the winner given by the BallKeeper.
	 * @return winner
	 */
	public String getWinner() {
		//Return Name of Winner or "Draw"
		if (checkGameOver()) {
			return ballKeeper.getWinner();
		}
		return null;
	}
	
	/**
	 * Returns the board layout in a string.
	 * @return Board string
	 */
	public String getBoard() {
		return board.toString();
	}
	
	/**
	 * Checks if its a given players turn.
	 * @param player
	 * @return true if its players turn
	 */
	public boolean getPlayerRightTurn(ClientHandler player) {
		if ((player1Turn && player == player1) || (!player1Turn && player == player2)) {
			return true;
		}
		
		return false;
	}
}
