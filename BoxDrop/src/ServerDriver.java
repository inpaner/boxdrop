import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import server.Server;


public class ServerDriver {

	
	public static void main(String[] args) {
		if (args.length > 1)
            usage();
        
		String folder = "server";
		if (args.length == 1) {
			folder = args[0];
		}
		
		Path path = Paths.get(folder);
		try {
			Files.createDirectory(path);
		} catch (IOException e) {} // Folder already exists. Like we care.
		
		new Server(path);
	}
		
	
    static void usage() {
        System.err.println("usage: java ServerDriver [folder]");
        System.exit(-1);
    }
}
