package job;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import client.AbstractClient;

public class JobListenerThread implements Runnable {
	private AbstractClient client;
	private JobManager jobmanager;
	
	public JobListenerThread(AbstractClient client, JobManager jobmanager) {
		this.client = client;
		this.jobmanager = JobManager.getInstance();
	}

	@Override
	public void run() {
		while (true) {
			try {
				InputStream inputStream = client.getSocket().getInputStream();
				ObjectInputStream objectInput = new ObjectInputStream(inputStream);
				System.out.println("\n\nListening for job requests from " + client.getSocket().getRemoteSocketAddress());
				final Job job = (Job) objectInput.readObject();
				job.setAsReceived();
				System.out.println("[RECEIVED JOB] " + job);
				jobmanager.handle(client, job);
			
			} catch (IOException ex) {
				// TODO finish socket in client manager
				System.out.println("Connection problem with " + client.getSocket().getRemoteSocketAddress() +". Closing socket.");
				client.closeSocket();
				break;
			 
			} catch (ClassNotFoundException ex) {
				client.closeSocket();
				ex.printStackTrace();
				break;		
			}
		}
	}
	
	protected AbstractClient getClient() {
		return client;
	}
	
}
