package communication.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import communication.exceptions.*;
import game.Collecto;
import game.Tuple;
import communication.ProtocolMessages;


public class Server implements Runnable {
	/** The ServerSocket of this GameServer.*/
	private ServerSocket ssock;
	/** List of ClientHandlers, one for each connected client. */
	private List<ClientHandler> clients;
	/**List of clients in queue. */
	private List<ClientHandler> queue;
	/**Map of games, ClientHandlers as keys and Collecto games as values. */
	private Map<ClientHandler, Collecto> games;
	/** Next client number, increasing for every new connection .*/
	private int nextClientNo;
	/** The view of this Server. */
	private ServerTUI view;
	/** Server name. */
	private String serverName;
	/** 
	 * Thread for initialize games when more than two clients on the queue.
	 */
	private Thread t1;
	/**
	 * Constructs a server object.
	 * Creates a list for clients.
	 * creates a list for queuing.
	 * Creates a HashMap for games.
	 */
	public Server() {
		clients = new ArrayList<>();
		queue = new ArrayList<>();
		view = new ServerTUI();
		nextClientNo = 1;
		games = new HashMap<>();
		
	}
	/**
	 * Gets the server's name.
	 * @return serverName The name of the server.
	 */
	public String getServerName() {
		return serverName;
	}
	/**
	 * Opens a new socket by calling {@link #setup()} and starts a new
	 * ClientHandler for every connecting client.
	 * If {@link #setup()} throws a ExitProgram exception, stop the program. 
	 * In case of any other errors, ask the user whether the setup should be 
	 * ran again to open a new socket.
	 */
	public void run() {
		try {
			setup();
		} catch (communication.exceptions.ExitProgram e2) {
			view.showMessage(e2.getMessage());
		}
		boolean openNewSocket = true;
		t1 = new Thread(new Runnable() {
			/**
			 * Creates new games when there are 
			 */
		    @Override
		    public  void run() {
		    	boolean serverRunning = true;
		    	// Synchronize the queue list.
		    	synchronized (queue) {
					while (serverRunning) {
						try {
							queue.wait();
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						if (queue.size() > 1) {
							Collecto newGame = new Collecto(queue.get(0), queue.get(1));
							games.put(queue.get(0), newGame); games.put(queue.get(1), newGame);
							// send response NEWGAME to both clients.
							try {
								queue.get(0).handleCommand(ProtocolMessages.NEWGAME);
							} catch (IOException e) { }
							// remove the the two first clients form the list.
							queue.remove(0); queue.remove(0); 
						}
			    	}
		    	}
		    }
		});  
		t1.start();
		while (openNewSocket) {
			try {
				while (true) {
					Socket sock = ssock.accept();
					view.showMessage("New client  connected! Number of clients: " + nextClientNo++);
					ClientHandler handler = new ClientHandler(sock, this);
					new Thread(handler).start();
				}
			} catch (IOException e) {
				view.showMessage("A server IO error occurred: " + e.getMessage());
				if (!view.getBoolean("Do you wish to open a new socket?")) {
					openNewSocket = false;
				}
			}
		}
		view.showMessage("See you later!");
	}
	/**
	 * Creates a server socket by asking the user for a server name and a port.
	 * @throws ExitProgram if a connection can not be created on the given 
	 *                     port and the user decides to exit the program.
	 * @ensures a serverSocket is opened.
	 */
	public void setup() throws ExitProgram {
		ssock = null;
		while (ssock == null) {
			serverName = view.getString("Please enter a name for the server:");
			int port = view.getInt("Please enter the server port.");
			// try to open a new ServerSocket
			try {
				view.showMessage("Attempting to open a socket at 127.0.0.1 "
						+ "on port " + port + "...");
				ssock = new ServerSocket(port, 20, 
						InetAddress.getByName("127.0.0.1"));
				view.showMessage("Server started at port " + port);
			} catch (IOException e) {
				view.showMessage("ERROR: could not create a socket on "
						+ "127.0.0.1" + " and port " + port + ".");
				if (!view.getBoolean("Do you want to try again?")) {
					
					throw new ExitProgram("User indicated to exit the "
							+ "program.");
					
				}
			}
		}
	
	}
	// ------------------ Server Methods --------------------------
	/**
	 * Register a new client with name and add it to the list of clients.
	 * If the client tries to log in with an already existing name then an exception is thrown.
	 * @param handler 
	 * @param username name for which the clients wants to log in.
	 * @throws AlreadyLoggedIn If the username is already on use.
	 * @requires this.clients.contains(handler)
	 */
	public void doLogin(ClientHandler handler, String username) throws AlreadyLoggedIn {
		if (clients.size() < 1 || clients.get(0) == null) {
			handler.setUserName(username);
			clients.add(handler);
		} else {
			boolean add = false;
			for (Iterator<ClientHandler> iterator1 = clients.iterator(); iterator1.hasNext(); ) {
				ClientHandler inclient = iterator1.next();
				if (!username.equals(inclient.getUserName())) {
					add = true;
				}
			}
			if (add) {
				handler.setUserName(username);
				clients.add(handler);
			} else {
				throw new AlreadyLoggedIn("The username is already in use.");
			}
			
		}
	}
	/**
	 * 	Upon request a list containing the names is sent to the client.
	 * The messages to be sent is: ProtocolMessages.LIST 
	 * + ProtocolMessages.DELIMETER + [user-in-queue]
	 * @return LIST[~username]* LIST plus the names of the clients in the clients list.
	 */
	public String doList() {
		String result = "";
		for (int i = 0; i < getClients().size(); i++) {
			result += ProtocolMessages.DELIMITER + getClients().get(i).getUserName();
		}
		return result;
	}
	/**
	 * Makes a move on the board.
	 * 1. Given a move, it checks if the move is valid
	 * 		if move is valid: move is made to the game.
	 * 			2. Server sends to clients: ProtocolMesages.MOVE  + ProtocolMessages.DELIMETER 
	 * + [move]
	 * 		if move is not valid 
	 * 			2. Server sends to clients: ProtocolMesages.ERROR + [message]
	 * @param client ClientHandler that is communicating with the client directly.
	 * @param move Integer that represents a move.
	 * @requires  27 <= move >= 0
	 * @ensure getGame(client).getBoard() != \old(getGame(client).getBoard())
	 * @throws InvalidMove
	 */
	public synchronized void doMove(ClientHandler client, int move) 
			throws InvalidMove, NotYourTurn, NotGameFound {
		if (getGame(client) != null) {
			if (getGame(client).doMove(move, client) == true) {
				updateBoards(client, getGame(client));
			} else {
				if (!getGame(client).getPlayerRightTurn(client)) {
					throw new NotYourTurn("Not your turn");
				} else {
					throw new InvalidMove("Move does not change configuration of the board.");
				}
			}
		} else {
			throw new NotGameFound("Game not found");
		}
	}
	/**
	 * Makes a double move on the board.
	 * 1. Given a double move, it checks if the move is valid
	 * 		if move is valid: move is made to the game.
	 * 	2. Server sends to clients: ProtocolMesages.MOVE  + ProtocolMessages.DELIMETER 
	 * + [move]*
	 * 		if move is not valid 
	 * 			2. Server sends to clients: ProtocolMesages.ERROR + [message]
	 * @param client ClientHandler that is communicating with the client directly.
	 * @param move Integer that represents a move.
	 * @param move2 Integer that represents a move.
	 * @requires  27 <= move >= 0 & 27 <= move2 >= 0
	 * @ensure getGame(client).getBoard() != \old(getGame(client).getBoard()) 
	 * @throws InvalidMove
	 */
	public synchronized void doMove(ClientHandler client, int move, int move2) 
			throws InvalidMove, NotYourTurn, NotGameFound {
		if (getGame(client) != null) {
			Tuple<Integer, Integer> tuple = new Tuple<Integer, Integer>(move,  move2);
			if (getGame(client).doMove(tuple, client) == true) {
				updateBoards(client, getGame(client));
			} else {
				if (!getGame(client).getPlayerRightTurn(client)) {
					throw new NotYourTurn("Not your turn");
				} else {
					throw new InvalidMove("Move is not valid.");
				}
			}
		} else {
			throw new NotGameFound("Game not found");
		}
	}
	/**
	 * Notifies the player that the game has ended.
	 * Sends to players: ProtocolMessages.GameOver.GAMEOVER + ProtocolMessages.DELIMETER + 
	 * [reason] + ProtocolMessages.DELIMETER + [winner - name]
	 * @param key ClientHandler.
	 */
	public synchronized void doGameOver(ClientHandler key) {
		games.remove(key, games.get(key));
		games.remove(getPartner(key),  games.get(getPartner(key)));
	}
	/**
	 * After making moves then the the games in the Map of games must be updates with he new games.
	 * @param client ClientHandler.
	 * @param game The game on which the client is playing on.
	 * @requires games.get(client) != null
	 */
	public synchronized void updateBoards(ClientHandler client, Collecto game) {
	    for (ClientHandler clientHandler : games.keySet()) {
			if (games.get(clientHandler) == games.get(client)) {
				games.replace(client, game);
				games.replace(clientHandler, game);
			}
	    }
	}
	/**
	 * Gets the clients list.
	 * @return clients.
	 */
	public synchronized List<ClientHandler> getClients() {
		return clients;
	}
	/**
	 * Gets the queue list.
	 * @return queue
	 */
	public synchronized List<ClientHandler> getQueue() {
		return queue;
	} 
   /**
    * When a client sends a QUEUE request this method puts it in the list queue.
    * @param client
    */
	public synchronized void doQueue(ClientHandler client) {
		synchronized (queue) {
			if (queue.contains(client)) {
				queue.remove(client);
			} else {
				queue.add(client);
				queue.notifyAll();
				view.showMessage("The current size of queue is: " + queue.size());
			}
		}
	}
	/**
	 * Given a client it gets removed from the list of clients.
	 * @param handler ClientHandler
	 */
	public synchronized void removeClient(ClientHandler handler) {
		clients.remove(handler);
	}
	/**
	 * Given a client handler that is currently on a game, this method returns the opponent 
	 * ClientHandler that is playing with him.
	 * @param client
	 * @return clientHandler
	 */
	public synchronized ClientHandler getPartner(ClientHandler client) {
		for (ClientHandler clientHandler : games.keySet()) {
			if (clientHandler != client) {
				if (games.get(clientHandler) == games.get(client)) {
					return clientHandler;
				}
			}
	    }
		return null;
	}
	
	/**
	 * Returns a string containing the board in the format 
	 * of ~[int]* for all the elements in the board.
	 * @param client  A ClientHandler.
	 * @return boardString
	 * @ensures boardString.length() == 98
	 */
	public synchronized String doNewgame(ClientHandler client) {
		Collecto game = getGame(client);
		String boardString = ProtocolMessages.DELIMITER;
		for (int i = 0; i < game.getBoard().length(); i++) {
			boardString += game.getBoard().charAt(i) + ProtocolMessages.DELIMITER;
		}
		return boardString;
	}
	/**
	 * Returns a Collecto game from the games HashMap.
	 * @param client
	 * @return games.get(client)
	 */
	public synchronized Collecto getGame(ClientHandler client) {
		return games.get(client);
	}
		// ------------------ Main --------------------------

	/** Start a new Server. */
	public static void main(String[] args) {
		Server server = new Server();
		System.out.println("Welcome to the Game's Server! Starting...");
		new Thread(server).start();
	}
	
	
}
