

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import commons.Constants;
import server.Server;

public class ServerDriver {
	public static void main(String[] args) {
		new Server(Paths.get("server"));
	}
	
	
	public static void main2(String[] args) throws IOException {
		if (args.length > 1)
            usage();
        
		String folder = "server";
		if (args.length == 1) {
			folder = args[0];
		}
		
		Path path = Paths.get(folder);
		
		new Server(path);
	}
		
	
    static void usage() {
        System.err.println("usage: java ServerDriver [folder]");
        System.exit(-1);
    }
}
