package client;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ServerListenerThread implements Runnable {
	private Client client;
	
	public ServerListenerThread(Client client) {
		this.client = client;
	}

	@Override
	public void run() {
		while (true) {
			try {
				InputStream inputStream = client.getSocket().getInputStream();
				DataInputStream dataInputSream = new DataInputStream(inputStream);
				String request = dataInputSream.readUTF();
				System.out.println(request);
			} catch (IOException ex) {
				client.closeSocket();
				ex.printStackTrace();
				break;
			}
		}
	}
	
}
