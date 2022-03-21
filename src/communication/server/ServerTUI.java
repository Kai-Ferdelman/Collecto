package communication.server;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Scanner;

public class ServerTUI {
	/** The PrintWriter to write messages to the console. */
	private Writer console;
	/**
	 * Scanner to read inputs from the user.
	 */
	private Scanner scanner;
	/**
	 * Constructs a new PrintWriter and  Scanner.
	 * @param server
	 */
	public ServerTUI() {
		console = new PrintWriter(System.out, true);
		scanner = new Scanner(System.in);
	}
	/**
	 * Display the message given in the argument to the console.
	 * @param message String to show in the console.
	 */
	public void showMessage(String message) {
		((PrintWriter) console).println(message);
		
	}
	/**
	 * Reads lines from the console and return them.
	 * @param question String asking to user specific type of data.
	 * @return scanner.nextLine() String of line from the console.
	 */
	public String getString(String question) {
		showMessage(question);
		return scanner.nextLine();
	}
	/**
	 * Reads Y or N from the console and return true if Y is given or false if N is given.
	 * @param question The question to be answered as Yes or No
	 * @return boolean 
	 */
	public boolean getBoolean(String question)  {
		showMessage(question + ". Y/N");
		String input = scanner.nextLine();
		if (input.equals("Y")) { 
			return  true;
		}
		return false;
	}
	/**
	 * 
	 * @param question
	 * @return
	 * @requires Integer.parseInt(scanner.nextLine()) == Integer
	 */
	public int getInt(String question) {
		int result = 8888;
		boolean correctInput = true;
		while (correctInput) {
			String input  = getString(question);
			try {
				result = Integer.parseInt(input);
				correctInput = false;
			} catch (NumberFormatException e) {
				continue;
			}

		}
		return result;
	}

}
