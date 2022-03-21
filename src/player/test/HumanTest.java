package player.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import player.Human;


public class HumanTest {
	private Human human;
	@BeforeEach
	public void setUp() {
		human = new Human();
	}
	
	@Test
	public void testHuman() {
		
		String name = "Human";
		human.setName(name);
		assertEquals(human.getName(), name);
		// String  of board with integers.
		String boardString = "5342536463431253212654"
						+ "140414562156231546536362121";
		human.setBoard(boardString);
		assertEquals(human.getBoard().toString(), boardString);
		
		assertThat(human.getHint(), containsString("17"));
		
	}
}
