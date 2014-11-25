package client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Path;

import commons.Constants;
import job.JobListenerThread;
import job.JobManager;

public class Client extends AbstractClient {
	private Path folder;


	public Client(String host, Path folder) throws UnknownHostException, IOException {
		super(new Socket(host, Constants.PORT));
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
