import java.io.IOException;
import java.net.Socket;

import client.Client;


public class ClientDriver {
	public static void main(String[] args) throws IOException {
		Socket socket = new Socket("localhost", 8080);
		new Client(socket);
	}
}
