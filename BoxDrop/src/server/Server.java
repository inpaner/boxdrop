package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Server {
	static Path FOLDER;
	private ServerSocket serverSocket;
	private ArrayList<Socket> liveSockets;
	
	public Server(int portNumber) {
		FOLDER = Paths.get("server");
		
		ServerJobManager.getInstance().setFolder(FOLDER);
		
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
			System.out.println("Waiting for connection.");
			Socket newSocket = serverSocket.accept();
			liveSockets.add(newSocket);
			return newSocket;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
