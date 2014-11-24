import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;

import client.Client;


public class ClientDriver {
	public static void main(String[] args) throws IOException {
		Socket socket = new Socket("localhost", 8080);
		Path dir = Paths.get("client1"); 
		new Client(socket, dir);
	}
}
