package commons;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class Job implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -738155227715836990L;
	private long jobTime;
	private String filename;
	private ArrayList<String> filenames = null;
	private long lastModified;
	private JobType type;
	
	public Job(Path path, JobType type) {
		jobTime = System.currentTimeMillis();
		filename = path.toString();
		try {
			lastModified = Files.getLastModifiedTime(path).toMillis();
		} catch (IOException ex) {
			System.out.println("Error accessing file while creating Job.");
			ex.printStackTrace();
		}
		
		this.type = type;
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
	
	
	public String toString() {
		return(type + ": " + filename + "@" + lastModified);
	}
}
