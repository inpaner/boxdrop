package server;

import commons.AbstractClient;
import commons.Job;
import client.JobManager;

public class ServerJobManager extends JobManager {
	
	static {
		instance = new ServerJobManager(); 
	}
	
	
	public static ServerJobManager getInstance() {
		System.out.println("Getting ServerJobManager");
		return (ServerJobManager) instance;
	}
	
	@Override
	protected synchronized void handleCreate(AbstractClient client, Job job) {
		super.handleCreate(client, job);
		
		// broadcast that shit
		ClientManager.getInstance().broadcast(client, job);
	}
	
	@Override
	protected synchronized void handleDelete(AbstractClient client, Job job) {
		super.handleDelete(client, job);
		
		// broadcast that shit
		ClientManager.getInstance().broadcast(client, job);
	}
	
}
