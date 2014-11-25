package server;

import java.net.Socket;

import client.AbstractClient;

public class ClientProxy extends AbstractClient {
	ClientProxy(Socket socket) {
		super(socket);
	}
	
	void startListening() {
		ServerJobListenerThread jlt = new ServerJobListenerThread(this, ServerJobManager.getInstance());
		new Thread(jlt).start();
	}
	
	
}
