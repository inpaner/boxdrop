package job;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;

import client.AbstractClient;
import server.ClientProxy;
import server.Server;
import commons.Constants;
import commons.Util;

public class JobManager {
	protected static JobManager instance = new JobManager(); 
	private final int BUFFER_SIZE = 32768;
	
	public static JobManager getInstance() {
		return instance;
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
		return new Job(JobType.REQUEST, job.getFilename(), job.getLastModified());
	}
	
	
	private ArrayList<JobStruct> jobs = new ArrayList<>(); 
	private JobStruct currentJob = null;
	
	public synchronized void enqueue(AbstractClient client, Job job) {
		// TODO order by job time creation method
		
		JobStruct jobstruct = new JobStruct(client, job);
		if (currentJob == null) {
			handle(jobstruct);
		} else {
			jobs.add(jobstruct);
			System.out.println("Jobs: " + jobs.size());
		}
	}
	
	
	private class JobStruct {
		Job job;
		AbstractClient client;
		JobStruct(AbstractClient client, Job job) {
			this.client = client;
			this.job = job;
		}
	}
	
	
	public synchronized void handle(JobStruct jobstruct) {
		AbstractClient client = jobstruct.client; 
		Job job = jobstruct.job;
		
		if (job.isForSending()) {
			client.sendJob(job);
			
		} else if (job.getType().equals(JobType.CREATE)) {
			handleCreate(client, job);
			
		} else if (job.getType().equals(JobType.REQUEST)) {
			handleRequest(client, job);
			
		} else if (job.getType().equals(JobType.DELETE)) {
			handleDelete(client, job);
		}
		
		finishJob();
	}
	
	
	public synchronized void finishJob() {
		if (jobs.isEmpty()) {
			currentJob = null;
		} else {
			JobStruct nextJob = jobs.get(0);
			handle(nextJob);
		}
	}
	
	
	protected synchronized boolean handleCreate(AbstractClient client, Job job) {
		// Ignore job if file exists and is either newer or have same last modified.
		// Same last modified is a VERY LOOSE assumption that they are the same file.
		// Else, do MD5.
		Path toCreate = getLocalizedFile(job);
		try {
			if (Files.exists(toCreate) 
					&& Files.getLastModifiedTime(toCreate).compareTo( FileTime.fromMillis(job.getLastModified()) ) >= 0) {
				return false;
			}
		} catch (IOException ex) {
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
			// TODO handle death
			e.printStackTrace();
			return false;
		} 
		
		return true;
	}
	
	
	protected synchronized boolean handleDelete(AbstractClient client, Job job) {
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
	
	protected synchronized boolean handleRequest(AbstractClient client, Job job) {
		// No existence checks since we are the ones who sent the initial job
		try {
			System.out.println("Handling request.");
			Path requestedFile = getLocalizedFile(job);
			FileInputStream inputStream = new FileInputStream(requestedFile.toString());
			OutputStream outputStream = client.getSocket().getOutputStream();
			outputStream.flush();
			DataOutputStream dos = new DataOutputStream(outputStream);
			
			byte[] buffer = new byte[BUFFER_SIZE];
            
			int bytesRead;
			System.out.println("Sending.");
            while((bytesRead = inputStream.read(buffer)) != -1){
            	Util.print(buffer);
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
	
	
	private synchronized Job constructJob(JobType type, Path path) {
		// Remove top level folder from filename
		String filename = path.subpath(1, path.getNameCount()).toString();
		
		long lastModified = 0;
		try {
			if (type != JobType.DELETE) {
				lastModified = Files.getLastModifiedTime(path).toMillis();
			}
		} catch (IOException ex) {
			System.out.println("Error accessing file while creating Job.");
			ex.printStackTrace();
			return null;
		}
		
		return new Job(type, filename, lastModified);
	}


	private Path getLocalizedFile(Job job) {
		return Paths.get(folder.toString(), job.getFilename());
	}
	
	
}
