package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;

import commons.Constants;

public class Server {
	static Path FOLDER;
	private ServerSocket serverSocket;
	
	public Server() {
		FOLDER = Paths.get("server");
		
		ServerJobManager.getInstance().setFolder(FOLDER);
		
				try {
			serverSocket = new ServerSocket(Constants.PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		new Thread(new AcceptConnectionThread(this)).start();
	}

	
	public Socket acceptNewConnection() {
		try {
			System.out.println("Waiting for connection.");
			Socket newSocket = serverSocket.accept();
			return newSocket;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
