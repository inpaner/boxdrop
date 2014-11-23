package server;

import java.net.Socket;

/**
 * This class is for continuously accepting new clients that want to connect to the server.
 */
public class AcceptConnectionThread implements Runnable {
	private Server server;

	
	public AcceptConnectionThread(Server server) {
		this.server = server;
	}

	
	public void run() {
		while (true) {
			Socket newSocket = server.acceptNewConnection();
			ClientManager.getInstance().newlyConnected(newSocket);
			System.out.println(newSocket.getRemoteSocketAddress() + " has connected.");
		}
	}
}
