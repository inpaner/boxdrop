package server;

import java.net.Socket;

import client.AbstractClient;
import job.JobListenerThread;

public class ClientProxy extends AbstractClient {
	ClientProxy(Socket socket) {
		super(socket);
	}
	
	void startListening() {
		JobListenerThread jlt = new JobListenerThread(this, ServerJobManager.getInstance());
		new Thread(jlt).start();
	}
	
	
}
