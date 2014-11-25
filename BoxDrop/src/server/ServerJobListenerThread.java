package server;

import client.AbstractClient;
import job.JobListenerThread;
import job.JobManager;

public class ServerJobListenerThread extends JobListenerThread {

	public ServerJobListenerThread(AbstractClient client, JobManager jobmanager) {
		super(client, jobmanager);
	}
	
	@Override
	public void run() {
		super.run();
		ClientManager.getInstance().remove( (ClientProxy) getClient() );
	}

}
