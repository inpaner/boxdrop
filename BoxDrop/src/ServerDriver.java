

import java.io.IOException;

import commons.Constants;
import server.Server;

public class ServerDriver {
	public static void main(String[] args) throws IOException {
		Server server = new Server();
	}
	
    static void usage() {
        System.err.println("usage: java ServerDriver [-ip] dir");
        System.exit(-1);
    }
}
