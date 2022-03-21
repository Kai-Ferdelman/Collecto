package game;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Kai Ferdelman
 *
 */

public class Board {
	/** Defines the length of the boards side. */
	public int boardSize;
	/** Ball array storing all on board balls. */
	Ball[] board;
	
	/**
	 * Constructs a new board and with a ball array according to the size.
	 */
	public Board() {
		boardSize = 7;
		board = new Ball[boardSize * boardSize];
		
	}
	
	/**
	 * Sets the color of the boards balls according to the boardString.
	 * @param boardString is the string sent by the server to setup the board.
	 */
	public void setBoard(String boardString) {
		String[] spliting = boardString.split("");
		int num = 0;
		for (int i = 0; i < spliting.length; i++) {
			num = Integer.parseInt(spliting[i]);
			board[i] = new Ball(num);
		}
	}
	
	/**
	 * Populates an empty board with colored balls.
	 * No two balls of the same color are placed next to one another.
	 * There are 8 balls of each color.
	 * The balls are placed randomly each checking their neighbors as to not be the same color.
	 */
	public void populateBoard() {
		int[] colors = {0, 0, 0, 0, 0, 0};
		int firstColor = (int) ((Math.random() * 6) + 1);
		colors[firstColor - 1]++;
		board[0] = new Ball(firstColor);
		for (int i = 1; i < boardSize; i++) {
			int color = 0;
			while (color == board[i - 1].getColor() || color == 0 || colors[color - 1] >= 8) {
				color = (int) ((Math.random() * 6) + 1);
			}
			board[i] = new Ball(color);
			colors[color - 1]++;
		}
		for (int i = boardSize; i < board.length; i++) {
			if (i != board.length / 2) {
				int color = 0;
				int attempts = 0;
				while (color == board[i - 1].getColor() ||
						color == board[i - boardSize].getColor() ||
						color == 0 ||
						colors[color - 1] >= 8) {
					color = (int) ((Math.random() * 6) + 1);
					attempts++;
					if (attempts > 100) {
						populateBoard();
						return;
					}
				}
				board[i] = new Ball(color);
				colors[color - 1]++;
			} else {
				board[i] = new Ball(0);
			}
		}
	}
	
	/**
	 * Returns the board as an Object.
	 * @return this
	 */
	public Board getBoard() {
		return this;
	}
	
	/**
	 * Returns a deep copy of the current board.
	 * @return new Board
	 */
	public Board getCopy() {
		Board copy = new Board();
		copy.fillEmpty(copy);
		for (int i = 0; i < board.length; i++) {
			copy.board[i].setColor(board[i].getColor());
		}
		copy.boardSize = boardSize;
		return copy;
	}
	
	/**
	 * Returns a string based on the color of the balls in the array.
	 * @return ball array string
	 */
	public String toString() {
		String s = "";
		for (int i = 0; i < board.length; i++) {
			s += board[i].getColor();
		}
		return s;
	}
	
	/**
	 * Returns a string based on the color of the balls in the array.
	 * Formatted to look like the board.
	 * @return formatter ball array string
	 */
	public String toFormattedString() {
		String s = "";
		
		for (int i = 0; i < board.length; i++) {
			if (i % boardSize == 0) {
				s += "\n\n";
			}
			s += board[i].getColor() + "\t";
		}
		
		return s;
	}
	
	/**
	 * applies a move to the board.
	 * 0 - 6 -> move left
	 * 7 - 13 -> move right
	 * 14 - 20 -> move up
	 * 21 - 27 -> move down
	 * All balls in the selected row or column are aligned in the matching direction.
 	 * @param move
	 */
	public void doMove(int move) {
		
		int ballPointer;
		int freeSlotPointer;
		

		//move left (0-6)
		if (move >= 0 && move <= 6) {
			freeSlotPointer = 0 + (boardSize * move);
			ballPointer = 0 + (boardSize * move);
			for (int i = 0; i < boardSize; i++) {
				if (board[ballPointer].getColor() != 0) {
					board[freeSlotPointer].setColor(board[ballPointer].getColor());
					freeSlotPointer++;
				}
				ballPointer++;
			}
			while (freeSlotPointer != ballPointer) {
				board[freeSlotPointer].remove();
				freeSlotPointer++;
			}
		}
		
		//move right (7-13)
		if (move >= 7 && move <= 13) {
			freeSlotPointer = boardSize - 1 + (boardSize * (move - 7));
			ballPointer = boardSize - 1 + (boardSize * (move - 7));
			for (int i = 0; i < boardSize; i++) {
				if (board[ballPointer].getColor() != 0) {
					board[freeSlotPointer].setColor(board[ballPointer].getColor());
					freeSlotPointer--;
				}
				ballPointer--;
			}
			while (freeSlotPointer != ballPointer) {
				board[freeSlotPointer].remove();
				freeSlotPointer--;
			}
		}
		
		//move up (14-20)
		if (move >= 14 && move <= 20) {
			freeSlotPointer = move - 14;
			ballPointer = move - 14;
			for (int i = 0; i < boardSize; i++) {
				if (board[ballPointer].getColor() != 0) {
					board[freeSlotPointer].setColor(board[ballPointer].getColor());
					freeSlotPointer += boardSize;
				}
				ballPointer += boardSize;
			}
			while (freeSlotPointer != ballPointer) {
				board[freeSlotPointer].remove();
				freeSlotPointer += boardSize;
			}
		}
		
		//move down (21-27)
		if (move >= 21 && move <= 27) {
			freeSlotPointer = boardSize * (boardSize - 1) + (move - 21);
			ballPointer = boardSize * (boardSize - 1) + (move - 21);
			for (int i = 0; i < boardSize; i++) {
				if (board[ballPointer].getColor() != 0) {
					board[freeSlotPointer].setColor(board[ballPointer].getColor());
					freeSlotPointer -= boardSize;
				}
				ballPointer -= boardSize;
			}
			while (freeSlotPointer != ballPointer) {
				board[freeSlotPointer].remove();
				freeSlotPointer -= boardSize;
			}
		}
	}

	
	/**
	 * Performs 2 moves given a double move.
	 * See doMove(int move)
	 * @param move
	 */
	public void doMove(Tuple<Integer, Integer> move) {
		doMove(move.x);
		doMove(move.y);
	}
	
	/**
	 * Returns a list of all balls that are placed next to each other and have the same color.
	 * @param b is the board on which it checks for neighbors
	 * @return Ball list
	 */
	public List<Ball> checkNeighbours(Board b) {
		List<Ball> sameNeighbours = new ArrayList<Ball>();
		for (int i = 0; i < b.boardSize; i++) {
			for (int o = 0; o < b.boardSize; o++) {
				if (i != b.boardSize - 1 && o != b.boardSize - 1) {
					Ball ball = b.board[o + b.boardSize * i].getBall();
					if (ball.getColor() != 0) {
						Ball neighbourRight = b.board[1 + o + b.boardSize * i].getBall();
						Ball neighbourDown = b.board[o + b.boardSize * (i + 1)].getBall();
						if (ball.getColor() == neighbourRight.getColor()) {
							sameNeighbours.add(ball);
							sameNeighbours.add(neighbourRight);
						}
						if (ball.getColor() == neighbourDown.getColor()) {
							sameNeighbours.add(ball);
							sameNeighbours.add(neighbourDown);
						}
					}
				}
				if (i == b.boardSize - 1 && o != b.boardSize - 1) {
					Ball ball = b.board[o + b.boardSize * i].getBall();
					if (ball.getColor() != 0) {
						Ball neighbourRight = b.board[1 + o + b.boardSize * i].getBall();
						if (ball.getColor() == neighbourRight.getColor()) {
							sameNeighbours.add(ball);
							sameNeighbours.add(neighbourRight);
						}
					}
				}
				if (i != b.boardSize - 1 && o == b.boardSize - 1) {
					Ball ball = b.board[o + b.boardSize * i].getBall();
					if (ball.getColor() != 0) {
						Ball neighbourDown = b.board[o + b.boardSize * (i + 1)].getBall();
						if (ball.getColor() == neighbourDown.getColor()) {
							sameNeighbours.add(ball);
							sameNeighbours.add(neighbourDown);
						}
					}
				}
			}
		}
		return sameNeighbours;
	}

	/**
	 * fills a given board with balls of color 0 (invisible).
	 * @param b
	 */
	public void fillEmpty(Board b) {
		for (int i = 0; i < b.board.length; i++) {
			b.board[i] = new Ball(0);
		}
	}
}
