package server;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import client.AbstractClient;
import job.Job;

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
		job.setForSending();
		for (ClientProxy client : clients) {
			if (!client.equals(caller)) {
				System.out.println("Will broadcast job to: " + client.getSocket().getRemoteSocketAddress());
				ServerJobManager.getInstance().handle(client, job);
				// client.sendJob(job); // bypass queue
			}
		}
	}
}
