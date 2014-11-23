package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
	private ServerSocket serverSocket;
	private ArrayList<Socket> liveSockets;

	public Server(int portNumber) {

		liveSockets = new ArrayList<Socket>();

		try {
			serverSocket = new ServerSocket(portNumber);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		new Thread(new AcceptConnectionThread(this)).start();
	}

	
	public Socket acceptNewConnection() {
		try {
			Socket newSocket = serverSocket.accept();
			liveSockets.add(newSocket);
			return newSocket;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
