package client;

import java.net.Socket;

import commons.AbstractClient;

public class Client extends AbstractClient {
	Socket socket;
	
	public Client(Socket socket) {
		super(socket);
		new Thread(new ServerListenerThread(this)).start();
		new Thread(new FileListenerThread(this)).start();
		
	}
}
