package server;

import commons.AbstractClient;
import commons.Job;
import client.JobManager;

public class ServerJobManager extends JobManager {
	
	static {
		instance = new ServerJobManager(); 
	}
	
	
	public static ServerJobManager getInstance() {
		return (ServerJobManager) instance;
	}
	
	
	@Override
	protected synchronized boolean handleCreate(AbstractClient client, Job job) {
		boolean success = false;
		success = super.handleCreate(client, job);
		
		// broadcast that shit
		if (success)
			ClientManager.getInstance().broadcast(client, job);
		
		return success;
	}
	
	
	@Override
	protected synchronized void handleDelete(AbstractClient client, Job job) {
		super.handleDelete(client, job);
		
		// broadcast that shit
		ClientManager.getInstance().broadcast(client, job);
	}
	
}
