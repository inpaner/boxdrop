package job;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

import commons.Util;

public class Job implements Serializable, Comparable<Job> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -738155227715836990L;
	private final long jobTime = System.currentTimeMillis();
	private String filename;
	private ArrayList<String> filenames;
	private long lastModified = 0;
	private JobType type;
	private boolean isDirectory = false;
	private boolean toSend = true;
	private byte[] checksum;
	

	/**
	 * Constructor for Done Job.
	 */
	public Job() {
		type = JobType.DONE;
	}


	public Job(JobType type, String filename, long lastModified, byte[] checksum) {
		this.type = type;
		this.filename = filename;
		this.lastModified = lastModified;
		this.checksum = checksum;
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
	
	
	public boolean isDirectory() {
		return isDirectory;
	}
	
	public void setAsDirectory() {
		isDirectory = true;
	}
	
	public byte[] getChecksum() {
		return checksum;
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


	public boolean hasSameContents(Path toCreate) {
		boolean hasSame = false;
		byte[] otherChecksum = Util.getChecksum(toCreate.toString());
		hasSame = Arrays.equals(checksum, otherChecksum);
		System.out.println("Checksum same: " + hasSame);
		return hasSame;
	}


	@Override
	public int compareTo(Job other) {
		int comparison = 0;
		if (lastModified > other.lastModified) {
			comparison = 1;
		} else if (lastModified < other.lastModified) {
			comparison = -1;
		}
		return comparison;
	}
}
