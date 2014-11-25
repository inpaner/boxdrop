package server;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import client.JobManager;
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
		for (ClientProxy client : clients) {
			if (!client.equals(caller)) {
				System.out.println("Will broadcasting job to: " + client.getSocket().getRemoteSocketAddress());
				ServerJobManager.getInstance().enqueue(client, job);
				// client.sendJob(job); // bypass queue
			}
		}
	}
}
