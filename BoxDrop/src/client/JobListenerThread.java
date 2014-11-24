package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import commons.AbstractClient;
import commons.Job;

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
				System.out.println("Listening for job requests from " + client.getSocket().getRemoteSocketAddress());
				InputStream inputStream = client.getSocket().getInputStream();
				ObjectInputStream objectInput = new ObjectInputStream(inputStream);
				Job job = (Job) objectInput.readObject();
				System.out.println("Received job: " + job);
				jobmanager.handle(client, job);
				
			} catch (IOException ex) {
				// TODO finish socket in client manager
				System.out.println("Connection problem with " + client.getSocket().getRemoteSocketAddress() +". Closing socket.");
				client.closeSocket();
				//ex.printStackTrace();
				break;
			 
			} catch (ClassNotFoundException ex) {
				client.closeSocket();
				ex.printStackTrace();
				break;
			
			}
		}
	}
	
}
