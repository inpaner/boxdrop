package client;

import java.io.IOException;
import java.nio.file.Path;


public class DirectoryListenerThread implements Runnable {
	private Client client;
	
	public DirectoryListenerThread(Client client) {
		this.client = client;
	}

	@Override
	public void run() {
		try {
			new DirectoryListener(client, true).processEvents();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
