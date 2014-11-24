package server;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.SocketException;

import commons.Job;

public class ClientListenerThread implements Runnable {
	ClientProxy client;
	
	public ClientListenerThread(ClientProxy client) {
		this.client = client;
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				System.out.println("Listening for job requests from " + client.getSocket().getRemoteSocketAddress());
				InputStream inputStream = client.getSocket().getInputStream();
				ObjectInputStream objectInput = new ObjectInputStream(inputStream);
				Job job = (Job) objectInput.readObject();
				System.out.println("RECEIVED JOB: " + job.getType());
				
				// objectInput.close();
				// inputStream.close();
				
			} catch (IOException ex) {
				// TODO finish socket in client manager
				System.out.println("Connection problem with " + client.getSocket().getRemoteSocketAddress() +". Closing socket.");
				client.closeSocket();
				ex.printStackTrace();
				break;
			} catch (ClassNotFoundException ex) {
				client.closeSocket();
				ex.printStackTrace();
				break;
			}
		}
	}

}
