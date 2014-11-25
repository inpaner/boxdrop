package job;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import client.AbstractClient;
import commons.Constants;
import commons.Util;

public class JobManager {
	private final int BUFFER_SIZE = 32768;
	protected static JobManager instance = new JobManager(); 
	private volatile boolean isDone = true;
	private ArrayList<JobStruct> jobs = new ArrayList<>();
	Comparator<JobStruct> jobstructComparator;
	private JobStruct currentJob = null;

	public static JobManager getInstance() {
		return instance;
	}
	
	protected JobManager() {
		 jobstructComparator = new Comparator<JobStruct>() {
			@Override
			public int compare(JobStruct o1, JobStruct o2) {
				return o1.job.compareTo(o2.job);
			}
		};
	}
	
	private Path folder;
	
	
	public void setFolder(Path folder) {
		this.folder = folder;
	}
	
	
	public Job newCreate(Path path) {
		return constructJob(JobType.CREATE, path);
	}
	
	
	public Job newModify(Path path) {
		// Same as Create. Maybe.
		return constructJob(JobType.CREATE, path);
	}
	
	
	public Job newDelete(Path path) {
		return constructJob(JobType.DELETE, path);
	}
	
	
	public Job newRequest(Job job) { 
		return new Job(JobType.REQUEST, job.getFilename(), job.getLastModified(), job.getChecksum());
	}
	
	
	private synchronized void delegateJob(JobStruct jobstruct) {
		AbstractClient client = jobstruct.client; 
		Job job = jobstruct.job;
		
		if (job.isForSending()) {
			if (job.getType().equals(JobType.CREATE)) {
				System.out.println("Setting to not Done.");
				isDone = false;
			}
			client.sendJob(job);
		} else if (job.getType().equals(JobType.CREATE)) {
			handleReceiveCreate(client, job);
			
		} else if (job.getType().equals(JobType.REQUEST)) {
			handleReceiveRequest(client, job);
			
		} else if (job.getType().equals(JobType.DELETE)) {
			handleReceiveDelete(client, job);
		}
		
		finishJob();
	}
	
	
	public synchronized void finishJob() {
		if (jobs.isEmpty()) {
			currentJob = null;
		} else if (isDone) {
			JobStruct nextJob = jobs.remove(0);
			delegateJob(nextJob);
		}
	}
	
	
	private void sendDone(AbstractClient client) {
		handle(client, new Job());
	}
	
	
	public synchronized void handle(AbstractClient client, Job job) {
		JobStruct jobstruct = new JobStruct(client, job);
		
		// Bypass queue for Requests and Done jobs
		if (job.getType().equals(JobType.REQUEST)) {
			currentJob = jobstruct;
			handleReceiveRequest(client, job);
			currentJob = null;
			return;
		} else if (job.getType().equals(JobType.DONE)) {
			currentJob = jobstruct;
			handleDone(client, job);
			currentJob = null;
			return;
		}
		
		// TODO order by job time creation method
		// System.out.println((currentJob == null)+","+isDone);
		if (currentJob == null && isDone) {
			currentJob = jobstruct;
			System.out.println("First job.");
			delegateJob(jobstruct);
		} else {
			System.out.println("Enqueuing: " + job);
			jobs.add(jobstruct);
			Collections.sort(jobs, jobstructComparator);
			System.out.println("Jobs: " + jobs.size());
		}
	}


	protected synchronized boolean handleReceiveCreate(AbstractClient client, Job job) {
		// Ignore job if file exists and is newer or 
		// Same last modified is a VERY LOOSE assumption that they are the same file.
		// Else, do MD5.
		Path toCreate = getLocalizedFile(job);
		try {
			if (Files.exists(toCreate) 
					&& (Files.getLastModifiedTime(toCreate).compareTo(FileTime.fromMillis(job.getLastModified()))) >= 0 
					|| job.hasSameContents(toCreate) ) {
				System.out.println("Denying creation of same or more recent file.");
				sendDone(client);
				return false;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
		
		// If doesn't exist or is older, send request
		Job request = newRequest(job);
		client.sendJob(request);
		
		// Get file from opposite end
		try {
			FileOutputStream outputStream = new FileOutputStream( toCreate.toFile() );
			
			int read = 0;
			byte[] buffer = new byte[BUFFER_SIZE];
			
			InputStream inputStream = client.getSocket().getInputStream();
			DataInputStream dis = new DataInputStream(inputStream);
			
			System.out.println("\nReceiving file.");
			while ((read = dis.read(buffer)) != -1) {
				if (read == Constants.EOF.length) {
					byte[] actualRead = Arrays.copyOfRange(buffer, 0, read);
					if (Arrays.equals(actualRead, Constants.EOF))
						break;
				}
				
				outputStream.write(buffer, 0, read);  
			}
			
			Files.setLastModifiedTime( toCreate, FileTime.fromMillis(job.getLastModified()) );
			System.out.println("Created new file: " + job.getFilename());
			outputStream.close();
			
		} catch (IOException e) {
			// something went terribly, horribly wrong
			// TODO clean up new file
			e.printStackTrace();
			sendDone(client);
			return false;
		} 
		
		sendDone(client);
		return true;
	}
	
	
	protected synchronized boolean handleReceiveDelete(AbstractClient client, Job job) {
		Path file = getLocalizedFile(job);
		boolean success = false;
		try {
			success = Files.deleteIfExists(file);
		} catch (IOException e) {
			System.out.println("Error deleting.");
			success = false;
		}
		
		return success;
	}
	
	
	protected synchronized boolean handleReceiveRequest(AbstractClient client, Job job) {
		if (job.isForSending()) {
			client.sendJob(job);
			return true;
		}
		
		// No existence checks since we are the ones who sent the initial job
		try {
			Path requestedFile = getLocalizedFile(job);
			FileInputStream inputStream = new FileInputStream(requestedFile.toString());
			OutputStream outputStream = client.getSocket().getOutputStream();
			outputStream.flush();
			DataOutputStream dos = new DataOutputStream(outputStream);
			
			byte[] buffer = new byte[BUFFER_SIZE];
            
			int bytesRead;
			System.out.println("Sending.");
            while((bytesRead = inputStream.read(buffer)) != -1){
            	dos.write(buffer, 0, bytesRead);
            }
			
			System.out.println("File sent.");
			
			dos.write(Constants.EOF, 0, Constants.EOF.length);
			dos.flush();
			inputStream.close();
		} catch (IOException ex) {
			ex.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	
	private synchronized void handleDone(AbstractClient client, Job job) {
		if (job.isForSending()) {
			client.sendJob(job);
		} else { // received
			isDone = true;
			finishJob();
		}
	}


	private synchronized Job constructJob(JobType type, Path path) {
		try {
			byte[] checksum = Util.getChecksum(path.toString());
			long lastModified = 0;
			
			// Remove top level folder from filename
			String filename = path.subpath(1, path.getNameCount()).toString();
			if (type != JobType.DELETE) {
				lastModified = Files.getLastModifiedTime(path).toMillis();
			}
			return new Job(type, filename, lastModified, checksum);

		} catch (IOException ex) {
			System.out.println("Error accessing file while creating Job.");
			ex.printStackTrace();
		}
		
		return null;
	}


	private Path getLocalizedFile(Job job) {
		return Paths.get(folder.toString(), job.getFilename());
	}


	private class JobStruct {
		Job job;
		AbstractClient client;
		JobStruct(AbstractClient client, Job job) {
			this.client = client;
			this.job = job;
		}
	}
	
	
}
