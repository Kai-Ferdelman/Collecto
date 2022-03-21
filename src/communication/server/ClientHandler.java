package communication.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import communication.exceptions.*;
import communication.ProtocolMessages;
/**
 * Handles the communication between the client and the server.
 * Uses the ProtocolMessages to send and receive commads.
 * @author Kelvin Jaramillo.
 */
public class ClientHandler implements Runnable {
	/**
	 * Reader to read the input stream from the socket.
	 */
	private BufferedReader in;
	/**
	 * Writer to write the stream to the clients.
	 */
	private BufferedWriter out;
	/**
	 * Sock for which handles communication with the client.
	 */
	private Socket sock;
	/**
	 * The server where the client handler is created on.
	 */
	private Server srv;
	/** Name of this ClientHandler, initially is not defined until the client logs in. */
	private String username = "NotDefined";
	/**
	 * Boolean to indicate if the client has logged in.
	 */
	private boolean loggedin;

	/**
	 * Constructs a new ClientHandler. Opens the In- and OutputStreams.
	 * @param sock The client socket
	 * @param srv  The connected server
	 */
	public ClientHandler(Socket sock, Server srv) {
		try {
			in = new BufferedReader(
					new InputStreamReader(sock.getInputStream()));
			out = new BufferedWriter(
					new OutputStreamWriter(sock.getOutputStream()));
			this.sock = sock;
			this.srv = srv;
		} catch (IOException e) {
			shutdown();
		}
	}
	/**
	 * Continuously listens to client input and forwards the input to the
	 * {@link #handleCommand(String)} method.
	 */
	public void run() {
		String msg;
		try {
			while (true) {
				msg = in.readLine();
				System.out.println("> [" + username + "] Incoming: " + msg);
				if (msg == null) {
					break;
				} 
				if (msg.equals(ProtocolMessages.QUEUE)) {
					// When queuing the server does not send response back.
					handleCommand(msg);
					continue;
				}
				handleCommand(msg);
				out.newLine();
				out.flush();
			}
			shutdown();
		} catch (IOException e) {
			System.out.println("Shutdown!!!");
			shutdown();
		} 
		
	}
	/**
	 * Handles commands received from the client by calling the according 
	 * methods at the Server. For example, when the message "QUEUE" 
	 * is received, the method doQueue() of Server should be called 
	 * and the output must be sent to the client.
	 * 
	 * If the received input is not valid, send an "Unknown Command" 
	 * message to the server.
	 * 
	 * @param msg command from client
	 * @throws IOException if an IO errors occur.
	 */
	
	public synchronized void handleCommand(String msg) throws IOException {
		String[] msgArray = msg.split(ProtocolMessages.DELIMITER);
		String action = msgArray[0];
		switch (action) {
			case ProtocolMessages.HELLO:
				String message = "Server by " + srv.getServerName();
				out.write(ProtocolMessages.HELLO + ProtocolMessages.DELIMITER + message);
				break;
			case ProtocolMessages.LOGIN:
				try {
					// add the list of clients on server.
					srv.doLogin(this, msgArray[1]);
					out.write(ProtocolMessages.LOGIN);
					loggedin = true;
				} catch (AlreadyLoggedIn o) {
					out.write(ProtocolMessages.ALREADYLOGGEDIN);
				}
				break;
			case ProtocolMessages.LIST:
				if (loggedin) {
					String messageList = ProtocolMessages.LIST;
					messageList += srv.doList();
					out.write(messageList);
				} else {
					out.write(ProtocolMessages.ERROR + ProtocolMessages.DELIMITER 
							+ " You need to LogIn first");
				}
			  	break;
			case ProtocolMessages.QUEUE:
				if (loggedin) {
					srv.doQueue(this);
				} else {
					out.write(ProtocolMessages.ERROR + ProtocolMessages.DELIMITER 
							+ " You need to LogIn first");
					out.newLine();
					out.flush();
				}
				break;
			case ProtocolMessages.MOVE:
				if (loggedin) {
					int i = 0;
					int j = 0;
					if (msgArray.length == 2) {
						try {
							i = Integer.parseInt(msgArray[1]);
							try {
								srv.doMove(this, i);
								String txt = ProtocolMessages.MOVE 
										+ ProtocolMessages.DELIMITER + i;
								out.write(txt);
								messageToPartner(txt);
							} catch (InvalidMove e) {
								out.write(ProtocolMessages.ERROR 
										+ ProtocolMessages.DELIMITER + e.getMessage());
							} catch (NotYourTurn e) {
								out.write(ProtocolMessages.ERROR
										+ ProtocolMessages.DELIMITER + e.getMessage());
							} catch (NotGameFound e) {	
								out.write(ProtocolMessages.ERROR 
										+ ProtocolMessages.DELIMITER + e.getMessage());
							}
							
						} catch (NumberFormatException e) {
							out.write(ProtocolMessages.ERROR + ProtocolMessages.DELIMITER 
									+ " move must be integer.");
						}
					} else if (msgArray.length == 3) {
						try {
							i = Integer.parseInt(msgArray[1]);
							j = Integer.parseInt(msgArray[2]);
							try {
								
								srv.doMove(this, i, j);
								String output = ProtocolMessages.MOVE + ProtocolMessages.DELIMITER 
										+ i + ProtocolMessages.DELIMITER + j;
								out.write(output);
								messageToPartner(output);
							} catch (InvalidMove e) {
								out.write(ProtocolMessages.ERROR 
										+ ProtocolMessages.DELIMITER + e.getMessage());
							} catch (NotGameFound e) {
								out.write(ProtocolMessages.ERROR 
										+ ProtocolMessages.DELIMITER + e.getMessage());
							} catch (NotYourTurn e) {
								out.write(ProtocolMessages.ERROR 
										+ ProtocolMessages.DELIMITER + e.getMessage());
							}
						} catch (NumberFormatException e) {
							out.write(ProtocolMessages.ERROR + ProtocolMessages.DELIMITER 
									+ " mve must be integer.");
						} 
					} else {
						handleCommand("notvalid");
					}
					if (srv.getGame(this).checkGameOver() == true) {
						
						String msgGameover = ProtocolMessages.GAMEOVER + ProtocolMessages.DELIMITER;
						String winner = srv.getGame(this).getWinner();
						if (winner.equals("Draw")) {
							msgGameover += ProtocolMessages.DRAW;
						} else if (!winner.equals(ProtocolMessages.DRAW)) {
							msgGameover += ProtocolMessages.VICTORY 
									+ ProtocolMessages.DELIMITER + winner;
						}
						System.out.println("A game is over!!");
						System.out.println("Server: " + msgGameover);
						out.newLine();
						out.write(msgGameover);
						out.newLine();
						out.flush();
						messageToPartner(msgGameover);
						
						srv.doGameOver(this);
					} 
					
				} else {
					out.write(ProtocolMessages.ERROR + ProtocolMessages.DELIMITER 
							+ " You need to LogIn first.");
				}
				break;
			case ProtocolMessages.NEWGAME:
				String msgNewgame = ProtocolMessages.NEWGAME;
				msgNewgame += srv.doNewgame(this);
				msgNewgame += getUserName() 
					+ ProtocolMessages.DELIMITER + srv.getPartner(this).getUserName();  
				messageToPartner(msgNewgame);
				out.write(msgNewgame);
				out.newLine();
				out.flush();
				break;
			case ProtocolMessages.DISCONNECT:
				String output = ProtocolMessages.GAMEOVER 
					+ ProtocolMessages.DELIMITER + ProtocolMessages.DISCONNECT;
				out.write(output);
				messageToPartner(output);
				srv.doGameOver(this);
				break;
			case ProtocolMessages.ERROR:
				out.write(ProtocolMessages.ERROR + ProtocolMessages.DELIMITER + "Try again!");
				break;
			default:
				System.out.println("Unkown command: " + action);
		}
	}
	/**
	 * Shut down the connection to this client by closing the socket and 
	 * the In- and OutputStreams and setting the loggedin boolean to false.
	 */
	private void shutdown() {
		System.out.println("> [" + username + "] Shutting down.");
		try {
			loggedin = false;
			in.close();
			out.close();
			sock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		srv.removeClient(this);
		srv.doGameOver(this);
	}
	/**
	 * Gets the the user's name.
	 * @return username
	 */
	public String getUserName() {
		return username;
	}
	/**
	 * Sets the user's name.
	 * @param username1 
	 */
	public void setUserName(String username1) {
		this.username = username1;
	}
	/**
	 * Gets the boolean logggedIn.
	 * @return this.loggedIn
	 */
	public boolean getLoggedIn() {
		return loggedin;
	}
	/**
	 * Given a message it sends the message to the other client.
	 * @requires srv.getPartner(this) != null
	 */
	public synchronized void messageToPartner(String msg) {
		ClientHandler other = srv.getPartner(this);
		BufferedWriter outN = other.getBufferWrite();
		try {
			outN.write("\n");
			outN.flush();
			outN.write(msg);
			outN.newLine();
			outN.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * Get the BufferedWrite used for communication.
	 * @return this.out
	 */
	public synchronized BufferedWriter getBufferWrite() {
		return this.out;
	}
	

	
}
