package server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import commons.AbstractClient;

public class ClientProxy extends AbstractClient {
	ClientProxy(Socket socket) {
		super(socket);
		// TODO Auto-generated constructor stub
	}
	
	void startListening() {
		new Thread(new ClientListenerThread(this)).start();
	}
	
	
}
