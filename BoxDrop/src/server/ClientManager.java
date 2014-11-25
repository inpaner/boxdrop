package server;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import commons.AbstractClient;
import commons.Job;

public class ClientManager {
	private static ClientManager instance = new ClientManager();
	
	static ClientManager getInstance() {
		return instance;
	}
	
	
	private List<ClientProxy> clients = new ArrayList<>();
	
	
	void newlyConnected(Socket socket) {
		ClientProxy client = new ClientProxy(socket);
		clients.add(client);
		new Thread(new NewConnectionThread(client)).start();
	}
	
	
	void broadcast(AbstractClient caller, Job job) {
		System.out.println("Beginning broadcst. Live via satellite.");
		for (ClientProxy client : clients) {
			if (!client.equals(caller)) {
				System.out.println("Broadcasting job to: " + client.getSocket().getRemoteSocketAddress());
				client.sendJob(job);
			}
		}
	}
}
