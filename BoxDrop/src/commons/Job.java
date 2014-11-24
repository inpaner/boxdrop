package commons;

import java.io.Serializable;
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
	
	public Job() {
		jobTime = System.currentTimeMillis();
		type = JobType.CREATE;
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
}
