package client;

import java.net.Socket;
import java.nio.file.Path;

import commons.AbstractClient;

public class Client extends AbstractClient {
	private Path dir; 
	public Client(Socket socket, Path dir) {
		super(socket);
		this.dir = dir;
		//new Thread(new ServerListenerThread(this)).start();
		new Thread(new FileListenerThread(this)).start();
	}
	
	Path getDir() {
		return dir;
	}
}
