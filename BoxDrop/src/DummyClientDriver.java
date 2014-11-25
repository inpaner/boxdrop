import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import client.Client;


public class DummyClientDriver {
	
	public static void main(String[] args) throws IOException {
		Path dir = Paths.get("client2"); 
		new Client("localhost", dir);
	}
}
