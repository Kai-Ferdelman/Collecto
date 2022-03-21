package communication.testing;

import communication.ProtocolMessages;
import player.AI;
import player.Human;
import player.MinMax;
import player.SmartMinMax;

public class PlayerVisualTest {
	
	
	
	public static void main(String[] args) {
		String newgame = "NEWGAME~5~3~4~2~5~3~6~4~6~"
				+ "3~4~3~1~2~5~3~2~1~2~6~5~4~1~4~0~4~1"
				+ "~4~5~6~2~1~5~6~2~3~1~5~4~6~5~3~6~3"
				+ "~6~2~1~2~1~Alice~Bob";
		String[] split = newgame.split(ProtocolMessages.DELIMITER);
		String boardString = "";
		for (int i = 1; i < split.length - 2; i++) {
			boardString += split[i];
		}
		Human player = new Human();
		player.setBoard(boardString);
		player.doMove(17);
		player.updateBoard();
		//-------- MinMax Test
		MinMax minmax = new MinMax();
		minmax.setLevel(1);
		AI playerIn = new AI(minmax);
		playerIn.setBoard(boardString);
		int playing = 2;
		System.out.println(playerIn.getBoard().toFormattedString());
		for (int i = 0; i < playing; i++) {
			int move = playerIn.determineSingleMove();
			System.out.println("the move is: " + move);
			playerIn.doMove(move);
			System.out.println("Before update: ");
			System.out.println(playerIn.getBoard().toFormattedString());
			playerIn.updateBoard();
			System.out.println("After update: ");
			System.out.println(playerIn.getBoard().toFormattedString());
		}
		
		// Smart MinMax Test
		System.out.println("-------Smart min max fro here nd below:---------");
		SmartMinMax minmax2 = new SmartMinMax();
		minmax2.setLevel(1);
		AI playerIn2 = new AI(minmax2);
		playerIn2.setBoard(boardString);
		System.out.println(playerIn2.getBoard().toFormattedString());
		for (int i = 0; i < playing; i++) {
			int move =  playerIn2.determineSingleMove();
			System.out.println("The move found is: " + move);
			playerIn2.doMove(playerIn2.determineSingleMove());
			System.out.println("Before update: ");
			System.out.println(playerIn2.getBoard().toFormattedString());
			playerIn2.updateBoard();
			System.out.println("After update: ");
			System.out.println(playerIn2.getBoard().toFormattedString());
		}
		
	}

}
