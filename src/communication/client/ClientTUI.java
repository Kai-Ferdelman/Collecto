package communication.client;

import java.io.PrintWriter;
import java.util.Scanner;

import communication.exceptions.ExitProgram;
import communication.exceptions.MoveOutOfRange;
import communication.exceptions.ServerUnavailableException;
import communication.ProtocolMessages;
/**
 * Clients User Interface is used to get the input from the user, as well as displaying messages 
 * to the user when the client is running.
 * @author Kelvin Jaramillo.
 *
 */
public class ClientTUI {
	/**
	 * Constructs a view for the client, makes the console System.out and 
	 * creates a new scanner for getting the inputs from the user.
	 * @param client
	 */
	public ClientTUI(Client client) {
		this.client = client;
		console = new PrintWriter(System.out, true);
		scan = new Scanner(System.in);
	}
	/**
	 * The client object that owns this TUI.
	 */
	private Client client;
	/**
	 * The interface of the user to read messages and input commands.
	 */
	private PrintWriter console;
	/**
	 * Scanner to read inputs form the console.
	 */
	private Scanner scan;
	/**
	 * Creates a loop for the use to input commands and handle them.
	 * When the users input "exit" the loops breaks.
	 */
	public void start() {
		
		String input;
		while (true) {
			input = scan.nextLine();
			try {
				handleUserInput(input);
			} catch (ExitProgram e) {
				showMessage(e.getMessage());
			}
		}
	}
	/**
	 * Handles the actions the user wants to perform.
	 * @param input
	 * @throws ExitProgram 
	 */
	private void handleUserInput(String input) throws ExitProgram {
		String[] splitted = input.split("\\s+"); // Split on space
		String commandString = splitted[0]; // Safe since input != empty
		String cmd1 = null;
		String cmd2 = null;
		if (splitted.length > 1) {
			cmd1 = splitted[1];
			if (splitted.length > 2) {
				cmd2 = splitted[2];
			}
		}
		switch (commandString) {
			case ProtocolMessages.LOGIN:
				if (client.getOnGame()) {
					try {
						client.doError("No possible to LOGIN during game");
					} catch (ServerUnavailableException e) {
						showMessage(e.getMessage());
					}
				} else if (client.getLoggedIn()) {
					try {
						client.doError(ProtocolMessages.ERROR +
								ProtocolMessages.DELIMITER + " Already Logged in");
					} catch (ServerUnavailableException e) {
						showMessage(e.getMessage());
					}
				} else {
					client.doLogin();
				}
				break;
			case ProtocolMessages.LIST:
				client.doList();
				break;
			case ProtocolMessages.QUEUE:
				if (client.getOnGame() == true) {
					showMessage("Not possible to queue during game.");
				} else {
					client.doQueue();
				}
				break;
			case ProtocolMessages.MOVE:
				int[] moves	=  null;
				if (splitted.length == 2) {
					moves = new int[1];
					moves[0] = Integer.parseInt(cmd1);
				} else if  (splitted.length == 3) {
					moves = new int[2];
					moves[0] = Integer.parseInt(cmd1);
					moves[1] = Integer.parseInt(cmd2);
				} else {
					try {
						client.doError(ProtocolMessages.ERROR + 
								ProtocolMessages.DELIMITER + " Wrong Move! ");
					} catch (ServerUnavailableException e) {
						showMessage(e.getMessage());
					}
					break;
				}
				try {
					client.doMove(moves);
				} catch (MoveOutOfRange e) {
					showMessage(e.getMessage());
					break;
				}
				break;
			case ProtocolMessages.HINT:
				client.doHint();
				break;
			case ProtocolMessages.HELP:
				printHelpMenu();
				break;
			case ProtocolMessages.EXIT:
				throw new ExitProgram("User exited");
			default:
				System.out.println("Unkown command: " + commandString);
				printHelpMenu();
		}
		
	}
	/**
	 * Given and message, it is displayed to the user through the console.
	 * @param message
	 */
	public void showMessage(String message) {
		console.println(message);
	}
	/**
	 * Gets a string from the console after asking the type of input the programs requires.
	 * @param question
	 * @return scan.nextLine()
	 */
	public String getString(String question)  {
		showMessage(question);
		return scan.nextLine();
	}
	/**
	 * Gets a Y or N form the console and specifies
	 *  the boolean to be true or false accordingly.
	 * @param question
	 * @return boolean
	 */
	public boolean getBoolean(String question)  {
		showMessage(question + ". Y/N");
		String input = scan.nextLine();
		if (input.equals("Y")) { 
			return  true;
		}
		return false;
	}
	/**
	 * Shows in the console the available commands when the user inputs and "help" command.
	 * @requires console != null
	 */
	public void printHelpMenu() {
		String result = "This commands are available.\n";
		
		result += "LOGIN ........................To claim a name on the server.\n"
					+ "QUEUE ...................................To get in the list to play.\n"
					+ "LIST ......To see which players are waiting in the list.\n"
					+ "MOVE number......................To make a single move.\n"
					+ "MOVE number number.....To make double move.\n"
					+ "help ..................................................To print this menu.\n"
					+ "exit ............................................... To quit the program.\n"
					+ "hint..........................To see what moves are available.\n"; 
		showMessage(result);
	}
}
