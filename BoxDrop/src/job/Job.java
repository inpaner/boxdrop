package job;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import client.AbstractClient;

public class Job implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -738155227715836990L;
	private final long jobTime = System.currentTimeMillis();
	private String filename;
	private ArrayList<String> filenames;
	private long lastModified = 0;
	private JobType type;
	private boolean toSend = true;
	
	
	public Job(Path path, JobType type) {
		filename = path.toString();
		try {
			lastModified = Files.getLastModifiedTime(path).toMillis();
		} catch (IOException ex) {
			System.out.println("Error accessing file while creating Job.");
			ex.printStackTrace();
		}
		
		this.type = type;
	}
	
	
	/**
	 * Constructor for Done Job.
	 */
	public Job() {
		type = JobType.DONE;
	}


	public Job(JobType type, String filename, long lastModified) {
		this.type = type;
		this.filename = filename;
		this.lastModified = lastModified;
		
	}

	
	public long getJobTime() {
		return jobTime;
	}

	
	public String getFilename() {
		return filename;
	}

	
	public ArrayList<String> getFilenames() {
		return filenames;
	}

	
	public long getLastModified() {
		return lastModified;
	}
	
	
	public JobType getType() {
		return type;
	}
	
	public boolean isForSending() {
		return toSend;
	}
	
	public void setAsReceived() {
		toSend = false;
	}
	
	public void setForSending() {
		toSend = true;
	}
	
	
	public boolean isEquivalent(Job other, JobType type) {
		return filename.equals(other.filename)
				&& lastModified == other.lastModified
				&& other.getType().equals(type);
	}
	
	
	@Override
	public String toString() {
		return(type + ": " + filename + " @ " + lastModified);
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Job))
			return false;
		
		Job other = (Job) obj;
		
		return jobTime == other.jobTime
				&& filename.equals(other.filename)
				&& lastModified == other.lastModified
				&& type.equals(other.type)
				&& toSend == other.toSend;
	}
}
