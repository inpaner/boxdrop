package client;

import java.net.Socket;
import java.nio.file.Path;

import commons.AbstractClient;

public class Client extends AbstractClient {
	private Path folder;
	
	public Client(Socket socket, Path folder) {
		super(socket);
		this.folder = folder;
		JobManager.getInstance().setFolder(folder);
		
		JobListenerThread jlt = new JobListenerThread(this, JobManager.getInstance());
		new Thread(jlt).start();
		
		new Thread(new FileListenerThread(this)).start();
	}
	
	Path getFolder() {
		return folder;
	}
}
