import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import client.Client;


public class ClientDriver {
	public static void main(String[] args) {
        if (args.length > 3)
            usage();
        
        String folder = "client1";
        String host= "localhost";
        
        int i = 0;
        while (i < args.length) {
            if (args[i].equals("-h")) {
                host = args[i+1];
                i += 2;
            } else {
                folder = args[i];
                i += 1;
            }
        }
        
        Path path = Paths.get(folder);
        
        try {
            Files.createDirectory(path);
        } catch (IOException e) {} // Folder already exists. Like we care.
        
        
        try {
			new Client(host, path);
		
        } catch (UnknownHostException e) {
			System.err.println("Host cannot be found.");
			System.exit(-1);
		
        } catch (IOException e) {
			System.err.println("Something went wrong when making the socket.");
			e.printStackTrace();
		}
        
        
    }
        
    
    static void usage() {
        System.err.println("usage: java ClientDriver [folder] [-h host] ");
        System.exit(-1);
    }
}
