package client;

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
import java.util.Arrays;

import server.ClientProxy;
import server.Server;
import commons.AbstractClient;
import commons.Constants;
import commons.Job;
import commons.JobType;
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
		return constructJob(JobType.MODIFY, path);
	}
	
	
	public Job newDelete(Path path) {
		return constructJob(JobType.DELETE, path);
	}
	
	
	public Job newRequest(Job job) {
		return new Job(JobType.REQUEST, job.getFilename(), job.getLastModified());
	}
	
	
	public synchronized void handle(AbstractClient client, Job job) {
		if (job.getType().equals(JobType.CREATE)) {
			handleCreate(client, job);
		} else if (job.getType().equals(JobType.REQUEST)) {
			handleRequest(client, job);
		} else if (job.getType().equals(JobType.DELETE)) {
			handleDelete(client, job);
		}
	}
	
	protected synchronized void handleCreate(AbstractClient client, Job job) {
		// check if exists
		
		
		// if not, send request
		Job request = newRequest(job);
		client.sendJob(request);
		System.out.println("Sent request job: " + request);
		
		// get file from opposite end
		try {
			
			Path receivedFile = getLocalizedFile(job);
			FileOutputStream outputStream = new FileOutputStream( receivedFile.toFile() );
			
			int read = 0;
			byte[] buffer = new byte[BUFFER_SIZE];
			
			InputStream inputStream = client.getSocket().getInputStream();
			DataInputStream dis = new DataInputStream(inputStream);
			
			System.out.println("Receiving: ");
			
			boolean first = true;
			while ((read = dis.read(buffer)) != -1) {
				
				// Cheap hack. Ignores first read, which seems to be
				// the unflushed ObjectStream
				if (first) {
					first = false;
					continue;
				}
				
				if (read == Constants.EOF.length) {
					byte[] actualRead = Arrays.copyOfRange(buffer, 0, read);
					if (Arrays.equals(actualRead, Constants.EOF))
						break;
				}
				
				outputStream.write(buffer, 0, read);  
			}
			
			Files.setLastModifiedTime( receivedFile, FileTime.fromMillis(job.getLastModified()) );
			System.out.println("Created new file: " + job.getFilename());
			outputStream.close();
		} catch (IOException e) {
			// TODO handle death
			e.printStackTrace();
		} 
		
		// broadcast that shit
	}
	
	
	protected synchronized void handleDelete(AbstractClient client, Job job) {
		Path file = getLocalizedFile(job);
		try {
			Files.deleteIfExists(file);
		} catch (IOException e) {
			System.out.println("Error deleting.");
			e.printStackTrace();
		}
	}
	
	protected synchronized void handleRequest(AbstractClient client, Job job) {
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
			System.out.println("Sending: ");
            while((bytesRead = inputStream.read(buffer)) != -1){
            	dos.write(buffer, 0, bytesRead);
            }
			
			System.out.println("File sent.");
			
			
			dos.write(Constants.EOF, 0, Constants.EOF.length);
			dos.flush();
			inputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private synchronized Job constructJob(JobType type, Path path) {
		// remove top level folder from filename
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
