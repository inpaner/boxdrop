package server;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ClientListenerThread implements Runnable {
	ClientProxy client;
	
	public ClientListenerThread(ClientProxy client) {
		this.client = client;
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				System.out.println("Listening for job requests from " + client.getSocket().getRemoteSocketAddress());
				InputStream inputStream = client.getSocket().getInputStream();
				DataInputStream dataInputSream = new DataInputStream(inputStream);
				String request = dataInputSream.readUTF();
				System.out.println(request);
			} catch (IOException ex) {
				client.closeSocket();
				ex.printStackTrace();
			}
		}
	}

}
