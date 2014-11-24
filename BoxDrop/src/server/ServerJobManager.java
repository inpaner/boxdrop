package server;

import commons.AbstractClient;
import commons.Job;
import client.JobManager;

public class ServerJobManager extends JobManager {
	
	
	@Override
	protected synchronized void handleCreate(AbstractClient client, Job job) {
		super.handleCreate(client, job);
		
		// broadcast that shit
		
	}
}
