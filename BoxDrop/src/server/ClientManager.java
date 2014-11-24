package server;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientManager {
	private static ClientManager instance;
	
	static {
		instance = new ClientManager();
	}
	
	static ClientManager getInstance() {
		return instance;
	}
	
	
	private List<ClientProxy> clients = new ArrayList<>();
	
	
	void newlyConnected(Socket socket) {
		ClientProxy client = new ClientProxy(socket);
		clients.add(client);
		new Thread(new NewConnectionThread(client)).start();
	}
}
