package commons;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public abstract class AbstractClient {
	Socket socket;
	
	public AbstractClient(Socket socket) {
		this.socket = socket;
	}
	
	
	public void sendString(String message) {
		try {
			OutputStream outputStream = socket.getOutputStream();
			DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
			dataOutputStream.writeUTF(message);
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
	
}
