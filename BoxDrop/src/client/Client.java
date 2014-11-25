package client;

import java.net.Socket;
import java.nio.file.Path;

import job.JobListenerThread;
import job.JobManager;

public class Client extends AbstractClient {
	private Path folder;


	public Client(Socket socket, Path folder) {
		super(socket);
		this.folder = folder;
		JobManager.getInstance().setFolder(folder);
		
		JobListenerThread jlt = new JobListenerThread(this, JobManager.getInstance());
		new Thread(jlt).start();
		
		new Thread(new DirectoryListenerThread(this)).start();
	}

	
	Path getFolder() {
		return folder;
	}
}
