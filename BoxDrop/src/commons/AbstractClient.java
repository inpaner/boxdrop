package commons;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public abstract class AbstractClient {
	Socket socket;
	
	public AbstractClient(Socket socket) {
		this.socket = socket;
	}
	
	public void sendJob(Job job) {
		if (job == null) {
			System.out.println("Null Job: " + job);
			return;
		}
		
		try {
			OutputStream outputStream = socket.getOutputStream();
			ObjectOutputStream objectStream = new ObjectOutputStream(outputStream);
			objectStream.writeObject(job);
			objectStream.flush();
			outputStream.flush();
		} catch (IOException e) {
			closeSocket();
			e.printStackTrace();
		}
	}
	
	
	public Socket getSocket() {
		return socket;
	}
	
	
	public void closeSocket() {
		// TODO tell client manager to finish this
		
		try {
			socket.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	
	
	@Override
	public int hashCode() {
		return socket.hashCode();
	}
	

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof AbstractClient))
			return false;
		
		AbstractClient other = (AbstractClient) obj;
		// TODO: Verify if identicality is sufficient
		if (this == other) {
			return true;
		} else {
			return false;
		}
	}
}
