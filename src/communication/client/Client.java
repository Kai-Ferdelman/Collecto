package communication.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import communication.exceptions.MoveOutOfRange;
import communication.exceptions.ServerUnavailableException;
import game.Tuple;
import player.AI;
import player.Human;
import player.MinMax;
import player.Naive;
import player.Player;
import player.SmartMinMax;
import player.SmartMinMaxThreaded;
import player.Strategy;
import communication.ProtocolMessages;
/**
 * Client for player to connect to server.
 * @ author Kelvin Jaramillo.
 */
public class Client {
	/**
	 * Socket 	used for connection with the server.
	 */
	private Socket serverSocket;
	/**
	 * Reads the stream send form the server through the socket.
	 */
	private BufferedReader in;
	/**
	 * Send stream through the socket to the server.
	 */
	private BufferedWriter out;
	/**
	 * The player that makes moves on the server's games.
	 */
	private Player player;
	/**
	 * Text user Interface used for the client to see the state of the game,
	 * input commands and read alert or respond messages.
	 */
	private ClientTUI view;
	/**
	 * Name of the client for which the server has reference to.
	 */
	private String name;
	/**
	 * Boolean to indicate if the client has correctly logged in to the server.
	 */
	private boolean loggedIn = false;
	/**
	 * Boolean to indicate if the client in the queue list on the server.
	 */
	private boolean inQueue = false;
	/**
	 * Boolean to indicate is the client currently is playing a game.
	 */
	private boolean onGame = false;
	/**
	 * String that is send after a new game has started.
	 * This line is used to store the input stream that is used during a game.
	 * This is because the reading of stream is done in a different during playing.
	 */
	private String line;
	/**
	 * Thread used to recieve stream inputs from the server during a game.
	 */
	public Thread t1;
	/**
	 * To keep track whos turn it is.
	 */
	private boolean yourTurn = false;
	/**
	 * Constructs a new client and a client user interface.
	 */
	public Client() {
		view  = new ClientTUI(this);
	}
	/**
	 * Starts a new connection  with the server.
	 * Sends a HELLO message is sent to the server to initialize exchange of commands.
	 * Display the user the available commands,
	 * Starts the view.
	 */
	private void star() {
		boolean connectToNewServer = true;
		while (connectToNewServer) {
			createConnection();
			try {
					// Do the HELLO handshake; show welcome to the user
				handleHello();
				// Show the available commands
				view.printHelpMenu();
				view.start();
			} catch (ServerUnavailableException e) {
				view.showMessage(e.getMessage());
				clearConnection();
			}
			connectToNewServer = view.getBoolean("Do you want to " + "connect to a new server?");
		}
		view.showMessage("See you later!");
	}
	/**
	 * Ask the user for input : < IP-address  port-number use-name>. 
	 * Then  it tries to connect to the server.
	 * @throws IOException 
	 * @requires IP-address instanceof InetAddress.
	 * @ensures serverSocket != null.
	 */
	public void createConnection()  {
		clearConnection();
		while (serverSocket == null) {
			String input = view.getString("Input: " + " <IP-address>  <port-number> <use-name>");
			String[] inputSplit = input.split(" ");
			if (inputSplit.length == 1 || inputSplit.length > 3) {
				view.showMessage("Incorrect input, try again.");
				continue;
			}
			String host = inputSplit[0];
			System.out.println(host);
			int port; 
			try {
				port = Integer.parseInt(inputSplit[1]);
			} catch (NumberFormatException e) {
				view.showMessage("Port number is not valid, enter integers only");
				continue;
			}
			name = inputSplit[2];   // the gets set here for the client
			InetAddress ip;
			try {
				if (host.equals("127.0.0.1")) {
					ip = InetAddress.getByName(host);
				} else {
					ip = InetAddress.getByName(host); 
				}
			} catch (UnknownHostException e) {
				view.showMessage("IP-address is not valid, enter valid IP");
				continue;
			}
			view.showMessage("Attempt to connect to " + ip + ":" + port + "...");
			try {
				serverSocket = new Socket(ip, port);
				in = new BufferedReader(new InputStreamReader(
						serverSocket.getInputStream()));
				out = new BufferedWriter(new OutputStreamWriter(
						serverSocket.getOutputStream()));
			} catch (IOException r) {
				view.showMessage("ERROR: could not create a socket on " 
						+ host + " and port " + port + "." + " Please try again.");
				continue;
			}
		}
	}
	/**
	 * Clears the socket and the stream inputs and outputs.
	 * @ensures in == null,
	 * 	out == null,
	 * serverSocket == null.
	 */
	private void clearConnection() {
		serverSocket = null;
		in = null;
		out = null;
	}
	
	/**
	 * Sends a message to the connected server, followed by a new line. 
	 * The stream is then flushed.
	 * @param msg the message to write to the OutputStream.
	 * @throws ServerUnavailableException if IO errors occur.
	 */
	public synchronized void sendMessage(String msg) 
			throws ServerUnavailableException {
		if (out != null) {
			try {
				out.write(msg);
				view.showMessage(">[ YOU ] Sending: " + msg);
				out.newLine();
				out.flush();
			} catch (IOException e) {
				view.showMessage(e.getMessage());
				throw new ServerUnavailableException("Could not write "
						+ "to server.");
			}
		} else {
			throw new ServerUnavailableException("Could not write "
					+ "to server.");
		}
	}
	/**
	 * Reads and returns one line from the server when client is not in a game.
	 * @return (BufferedWritter)in.readline() The line sent by the server.
	 * @throws ServerUnavailableException if IO errors occur.
	 */
	public String readLineFromServer() 
			throws ServerUnavailableException {
		if (in != null) {
			try {
				// Read and return answer from Server
				String answer = in.readLine();
				view.showMessage(">[Server] Incomnig: " + answer);
				if (answer == null) {
					throw new ServerUnavailableException("Could not read "
							+ "from server.");
				}
				return answer;
			} catch (IOException e) {
				throw new ServerUnavailableException("Could not read "
						+ "from server.");
			}
		} else {
			throw new ServerUnavailableException("Could not read "
					+ "from server.");
		}
	}
/**
 *Handles the initial connection between client and server.
 * 1. Sends a message to the server: 
 *  ProtocolMessages.HELLO + (message + (user's-name))
 *  2. Server returns on line containing: 
 *  ProtocolMessages.HELLO + ProtocolMessages.DELIMITER + (message).
 * @throws ServerUnavailableException
 */
	public void handleHello() 
			throws ServerUnavailableException {
		sendMessage(ProtocolMessages.HELLO 
				+ ProtocolMessages.DELIMITER + " Client by Kai & Kelvin");
		
		if (readLineFromServer() == null) {
			throw new ServerUnavailableException("Server is not availble");
		}
	}
	/**
	 *Sends a login request to the server.
	 * 1. The request message is: ProtocolMessages.LOGIN + (user's-name).
	 * 2. Server responds 
	 * if message equal to: ProtocolMessages.LOGIN, login is successful
	 * if message not equal to: ProtocolMessages.LOGIN, login is successful and the server returns:
	 * ProtocolMessages.ALREADYLOGIN, in which case the user has to try another name.
	 */
	public void doLogin() {
		try {
			
			sendMessage(ProtocolMessages.LOGIN + ProtocolMessages.DELIMITER + name);
		} catch (ServerUnavailableException e) {
			view.showMessage(e.getMessage());
		}
		String input;
		try {
			input = readLineFromServer();
			String[] inputSplit = input.split(ProtocolMessages.DELIMITER);
			if (inputSplit[0].equals(ProtocolMessages.ALREADYLOGGEDIN)) {
				view.showMessage("The user name " + name + " already exits on the server.");
				String nameLog = view.getString("Please enter a new username: \n");
				this.name = nameLog;
				doLogin();
			} else if (inputSplit[0].equals(ProtocolMessages.LOGIN)) {
				loggedIn = true;
				view.showMessage("Loggin successful, you can now get in queue.");
			}
		} catch (ServerUnavailableException e) {
			view.showMessage(e.getMessage());
		}
		
		
	}
	/**
	 * Sends a queue request to the server.
	 * 1. The request message is: ProtocolMessages.QUEUE + ProtocolMessages.DELIMETER + [user-name].
	 * 2. Check if client was put on list by sending a LIST request: ProtocolMessages.QUEUE.
	 * Then the server checks if there is other player available to create a game.
	 * If the server starts a new game.
	 * 2. The server then indicates that the user can start a game by message:
	 * ProtocolMessages.NEWGAME + ProtocolMessages.DELIMETER + <cell value>^49
	 * + ProtocolMessages.DELIMETER +  <player name>*
	 * 3. After NEWGAME is received the method toPlay() is call, this read requests
	 *  from server during game. 
	 * The read line method is not use during a game.
	 * 4. If the method is called again then it means the user want to be taken out the queue.
	 * @assert !onGame && loggedIn == true.
	 */
	public void doQueue() {
		if (loggedIn & !onGame) {
			try {
				if (inQueue == false) {
					inQueue = true;
					player = choosePlayer();
					sendMessage(ProtocolMessages.QUEUE);
					toPlay();
				} else if (inQueue == true) {
					sendMessage(ProtocolMessages.QUEUE);
					inQueue = false;
				}
				
			} catch (ServerUnavailableException e) {
				view.showMessage(e.getMessage());
			}
		} else {
			view.showMessage("Please logging first");
			view.printHelpMenu();
		}
	}
	/**
	 * Checks if the move is valid
	 * 	Sends a do move request to the server.
	 * 1. ProtocolMessages.MOVE + ProtocolMessages.DELIMITER + (move).
	 * If valid move then server respond with: ProtocolMessages.MOVE + 
	 * ProtocolMessages.DELIMITER + (move)
	 * else if invalid move the server responds with: ERROR+[no valid messages].
	 * @param move  Integer move given by the TUI.
	 * @assert 27 <= move >=0
	 * @throws MoveOutOfBounds 
	 */
	public void doMove(int[] move) throws MoveOutOfRange {
		for (int i = 0; i < move.length; i++) {
			if (move[i] < 0 || move[i] > 27) {
				throw new MoveOutOfRange();
			} else {
				continue;
			}
		}
		if (loggedIn & onGame) {
			String message = "";
			try {
				message = ProtocolMessages.MOVE;
				for (int i = 0; i < move.length; i++) {
					message += ProtocolMessages.DELIMITER + move[i];
				}
				sendMessage(message);
			} catch (ServerUnavailableException e) {
				view.showMessage(e.getMessage());
			}
			
		}
	}
	/**
	 * Sends LIST request to the server to see the clients that are already connected to the server.
	 * 1. The message for request is: ProtocolMessages.List + ProtocolMessages.DELIMETER
	 * 2. The server responds with a list of clients connected to server as: ProtocolMessages.List 
	 * + ProtocolMessages.DELIMETER + [user-names]*.
	 * if not logged in, the server automatically logs this client in.
	 */
	public void doList() {
		if (loggedIn) {
			String message = "";
			try {
				message = ProtocolMessages.LIST;
				sendMessage(message);
				//To not use the buffer outside the toPlay()
				//during play or after queuing.
				if (!inQueue || !onGame) {
					readLineFromServer();
				} 
			} catch (ServerUnavailableException e) {
				view.showMessage(e.getMessage());
			}
		} else {
			view.showMessage("You need LogIn first.");
		}
		
	}
	/**
	 * Shows the user possible moves that are legal.
	 * @requires onGame == true.
	 */
	public void doHint() {
		String hints = player.getHint();
		view.showMessage(hints);
	}
	/**
	 * Used to read the stream send by the server during game, 
	 * if it required outside the method toPlay().
	 * @return this.line The line obtained from the buffer during game.
	 */
	public synchronized String getLine() {
		return this.line;
	}
	/**
	 * Used by method doPlay() to set this.line to the stream read from the socket during a game.
	 * @param line String read from the socket stream.
	 * @requires line != null.
	 */
	public synchronized void setLine(String line) {
		this.line = line;
	}
	/**
	 * Reads stream from the server socket after the player has been queued.
	 * Starts the thread t1. 
	 * Handles commands send by the server during a game.
	 * @requires player != null & !onGame & inQueue
	 */
	public void toPlay() {
		t1 = new Thread(new Runnable() {
		    @Override
		    public void run() {
				BufferedReader extraIn = null;
				try {
					extraIn = new BufferedReader(new InputStreamReader(
							serverSocket.getInputStream()));
				} catch (IOException e1) {
					e1.printStackTrace();
				} 
				String input;
				
				while (true) {
					
					try {
						input = extraIn.readLine();
						if (input == null) {
							shutDown();
							break;
						}
						view.showMessage(">[Server] Incomnig: " + input);
						String[] inputSplit = input.split(ProtocolMessages.DELIMITER);
						if (inputSplit[0].equals(ProtocolMessages.NEWGAME)) {
							passBoard(input);
							onGame = true;
							inQueue = false;
							if (player instanceof AI && 
									inputSplit[inputSplit.length - 2].equals(name)) {
								yourTurn = true;
								makeAIPlay();
							}
						} else if (inputSplit[0].equals(ProtocolMessages.MOVE)) {
							yourTurn = !yourTurn;
							if (inputSplit.length == 2) {
								makeMove(inputSplit[1]);
							} else {
								makeMove(inputSplit[1], inputSplit[2]);
							}
							if (player instanceof AI) {
								makeAIPlay();
							}
						} else if (inputSplit[0].equals(ProtocolMessages.GAMEOVER)) {
							onGame = false;
							inQueue = false;
							yourTurn = false;
							break;
						} else if (inputSplit[0].equals(ProtocolMessages.ERROR)) {
							if (onGame) {
								String theBoard = player.getBoard().getBoard().toString();
								player = new Human();
								passBoardDuringGame(theBoard);
							}
							continue;
						} else {
							setLine(input);
							continue;
						}
					} catch (IOException e) {
						view.showMessage("Server has disconnected.");
						try {
							shutDown();
						} catch (IOException e1) {
							view.showMessage(e.getMessage());
						}
						break;
					} 
				}
				//
		    }
		});  
		t1.start(); 
	}
	/**
	 * Makes a double move in the players board.
	 * @param move Integer move.
	 * @requires 27<= move >= 0.
	 * @ensures  players.getBoard() != \old(players.getBoard()).
	 */
	public void makeMove(String move) {
		int i = 0;
		String rightmove = move;
		try {
			i = Integer.parseInt(move);
		} catch (NumberFormatException e) {
			for (int o = 0; o < move.length(); o++) {
				if (!(move.charAt(o) >= 48 && move.charAt(o) <= 57)) {
					rightmove = move.substring(0, o - 1);
				}
			}
			i = Integer.parseInt(rightmove);
		}
		player.getBoard().doMove(i);
		player.updateBoard();
		view.showMessage(player.getBoard().toFormattedString());
		
	
	}
	/**
	 * Makes a double move in the players board.
	 * @param move Integer move.
	 * @param move2 Integer move.
	 * @requires 27<= move >= 0 & 27<= move2 >= 0.
	 * @ensures  players.getBoard() != \old(players.getBoard())
	 */
	public void makeMove(String move, String move2) {
		int i = Integer.parseInt(move);
		String validmove = move2;
		
		int j = 0;
		try {
			j = Integer.parseInt(move2);
		} catch (NumberFormatException e) {
			for (int o = 0; o < move.length(); o++) {
				if (!(move2.charAt(o) >= 48 && move2.charAt(o) <= 57)) {
					validmove = move2.substring(0, o - 1);
				}
			}
			j = Integer.parseInt(validmove);
		}
		player.getBoard().doMove(i);
		player.getBoard().doMove(j);
		player.updateBoard();
		view.showMessage(player.getBoard().toFormattedString());
	}
	/**
	 * Ask the user what of player he wants to play the game with.
	 * If a human player is selected the "hint" command can be used to get suggestions.
	 * If AI payer is selected then the user can also choose the level of difficulty.
	 * @return Player player that will play the game with its own strategy.
	 */
	public Player choosePlayer() {
		Player playerIn = null;
		while (true) {
			String msg = "'What kind of player do you want to play with?\n"
					+ "H......................................for Human.\n"
					+ "A.............................for Naive. Level 0.\n"
					+ "M............................for MinMax. Level 1.\n"
					+ "S......................for Smart MinMax. Level 1.\n"
					+ "T........for Smart multi threaded MinMax Level 2.";
			String type = view.getString(msg);
			if (type.equals("H")) {
				playerIn = new Human();
				break;
			} else if (type .equals("A")) {
				Strategy strategy = new Naive();
				playerIn = new AI(strategy);
				break;
			} else if (type.equals("M")) {
				Strategy strategy = new MinMax();
				((MinMax) strategy).setLevel(1);
				playerIn = new AI(strategy);
				break;
			} else if (type.equals("S")) {
				Strategy strategy = new SmartMinMax();
				((SmartMinMax) strategy).setLevel(1);
				playerIn = new AI(strategy);
				break;
			} else if (type.equals("T")) {
				Strategy strategy = new SmartMinMaxThreaded();
				((SmartMinMaxThreaded) strategy).setLevel(2);
				playerIn = new AI(strategy);
				break;
			} else {
				continue;
			}
		}
		return playerIn;
	}
	/**
	 * Makes the a AI player to send the move an of the strategies decide to make.
	 * @requires player != null && player instanceof AI
	 */
	public void makeAIPlay() {
		if (yourTurn) {
			int move = ((AI) this.player).determineSingleMove();
			Tuple<Integer, Integer> moves = ((AI) player).determineDoubleMove();
			if (move != -1) {
				try {
					sendMessage(ProtocolMessages.MOVE + ProtocolMessages.DELIMITER + move);
				} catch (ServerUnavailableException e) {
					view.showMessage(e.getMessage());
				}
			} else if (moves != null) {
				
				try {
					sendMessage(ProtocolMessages.MOVE + 
							ProtocolMessages.DELIMITER 
							+ moves.x + ProtocolMessages.DELIMITER + moves.y);
				} catch (ServerUnavailableException e) {
					view.showMessage(e.getMessage());
				}
			} else {
				view.showMessage("GameOver");
			}
		}
	}
	/**
	 * Gives a string of integers representing a board.
	 * @param newgame This is command send by server when a new game is started on the server side.
	 * @requires newgame 
	 * newgame.split(ProtocolMessages.DELIMITER)[0].equals(ProtocolMessages.NEWGAME)
	 */
	public void passBoard(String newgame) {
		String[] split = newgame.split(ProtocolMessages.DELIMITER);
		String boardString = "";
		for (int i = 1; i < split.length - 2; i++) {
			boardString += split[i];
		}
		player.setBoard(boardString);
		view.showMessage(player.getBoard().toFormattedString());
				
	}
	/**
	 * Gives a string of integers representing a board.
	 * @param newgame This is command send by server when a new game is started on the server side.
	 * @requires newgame 
	 * newgame.split(ProtocolMessages.DELIMITER)[0].equals(ProtocolMessages.NEWGAME)
	 */
	public void passBoardDuringGame(String boardString) {
		player.setBoard(boardString);
		view.showMessage(player.getBoard().toFormattedString());
				
	}
	/**
	 * Finalizes cleanly the connection from the server.
	 * @ensures in.close() & out.close() & serverSocket.close() & System.exit(0).
	 * @throws IOException
	 */
	public void shutDown() throws IOException {
		in.close();
		serverSocket.close();
		out.close();
		view.showMessage("Ending program.");
		System.exit(0);
	}
	/**
	 * To get the onGame boolean, to see if the player is on a game.
	 * @return this.onGame
	 */
	public boolean getOnGame() {
		return this.onGame;
	}
	/**
	 * Sends errors to the server with a specific message.
	 * @param msg Error message to send to server.
	 * @throws ServerUnavailableException
	 */
	public void doError(String msg) throws ServerUnavailableException {
		sendMessage(msg);
	}
	/**
	 * To get the loggedIn boolean, to see if the client has been logged in 
	 * to the server.
	 * @return this.loggedIn.
	 */
	public boolean getLoggedIn() {
		return this.loggedIn;
	}
	/**
	 * Runs the start method and connection to server starts.
	 * @param args
	 */	
	public static void main(String[] args) {
		(new Client()).star();
	}
}
