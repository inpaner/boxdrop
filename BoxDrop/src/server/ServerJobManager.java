package server;

import client.AbstractClient;
import job.Job;
import job.JobManager;

public class ServerJobManager extends JobManager {
	
	static {
		instance = new ServerJobManager(); 
	}
	
	
	public static ServerJobManager getInstance() {
		return (ServerJobManager) instance;
	}
	
	
	@Override
	protected synchronized boolean handleReceiveCreate(AbstractClient client, Job job) {
		boolean success = super.handleReceiveCreate(client, job);
		
		if (success) {
			ClientManager.getInstance().broadcast(client, job);
		}
		
		return success;
	}
	
	
	@Override
	protected synchronized boolean handleReceiveDelete(AbstractClient client, Job job) {
		boolean success = super.handleReceiveDelete(client, job);
		
		if (success) {
			ClientManager.getInstance().broadcast(client, job);
		}
		
		return success;
	}
}
