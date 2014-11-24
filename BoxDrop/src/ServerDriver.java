

import java.io.IOException;

import commons.Constants;

import server.Server;

public class ServerDriver {
	public static void main(String[] args) throws IOException {
		for (int i = 0; i < Constants.EOF.length; i++) {
			System.out.print(Constants.EOF[i]);
		}
		System.out.println("\n"+Constants.EOF.length);
		Server server = new Server(8080);

	}
}
