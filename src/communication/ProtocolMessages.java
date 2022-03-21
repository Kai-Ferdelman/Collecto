package communication;
/**
 * This class stores the commands used for the Collecto game.
 * The commands are used specially for sending requests and messages between client and server.
 * @author Kelvin Jaramillo.
 *
 */
public class ProtocolMessages {
	/**
	 * Sent as last line in a multi-line response to indicate the end of the text.
	 */
	public static final String EOM = "\n";
	// TODO check if this filed is necessary.
	/**
	 * Delimiter used to separate arguments sent over the network.
	 */
	public static final String DELIMITER = "~";
	/** Used for the server-client handshake. */
	public static final String HELLO = "HELLO";

	/*
	 * The following chars are both used by the TUI to receive user input, and the
	 * server and client to distinguish messages.
	 */
	/**
	 * Used by client to send a login request and by server to confirm the login request.
	 */
	public static final String LOGIN = "LOGIN";
	/**
	 * Used by client to send a List request, meaning the client wants
	 *  to see what players are on the list on the queue.
	 * Used by the server to respond to the request by adding
	 * the names of the players on queue after this command.
	 */
	public static final String LIST = "LIST";
	/**
	 * Sent by client to indicate it wants to play a game to the server.
	 */
	public static final String QUEUE = "QUEUE";
	/**
	 * Sent by client to make a move on the server's game.
	 * Also sent by server to the to player on the game to 
	 * confirm the move.
	 */
	public static final String MOVE = "MOVE";
	/*
	 * The following three commands are for the server only.
	 */
	/**
	 * Send by server to players that are wanting in the list to get 
	 * into a game.
	 */
	public static final String NEWGAME = "NEWGAME";
	/**
	 * Sent by server when a client sends a logging request 
	 * and the user name is already in use.
	 */
	public static final String ALREADYLOGGEDIN = "ALREADYLOGGEDIN";
	/**
	 * Command sent by server to both players on the game to indicate
	 * that the game has finished.
	 */
	public static final String GAMEOVER = "GAMEOVER";
	/**
	 * Send by server or client to indicate something wrong has happened.
	 */
	public static final String ERROR = "ERROR";
	/*
	 * Commands that are executed internally by the client only.
	 */
	/**
	 * Internal command in the client side to ask for available commands.
	 */
	public static final String HELP = "help";
	public static final String HINT = "hint";
	/**
	 * Internal command for client to see what are the possible moves during a game.
	 */
	public static final String EXIT = "exit";
	/*
	 * Commands for Game Over.
	 */
	/**
	 * Send by server to both clients in a game, it is presided by GAMEOVER 
	 * and indicates that one of the clients has disconnected.
	 */
	public static final String DISCONNECT = "DISCONNECT";
	/**
	 ** Send by server to both clients in a game, it is presided by GAMEOVER 
	 * and indicates that one of the clients has won the game.
	 */
	public static final String VICTORY = "VICTORY";
	/**
	 ** Send by server to both clients in a game, it is presided by GAMEOVER 
	 * and indicates that the game has ended with a draw.
	 */
	public static final String DRAW = "DRAW";

}
