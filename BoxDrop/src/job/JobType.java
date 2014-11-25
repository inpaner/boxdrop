package job;

public enum JobType {
	CREATE("Create"), MODIFY("Modify"), DELETE("Delete"), REQUEST("Request"), LIST("List"), DONE("Done");
	
	String name;
	
	JobType(String name) {
		this.name = name;
	}
	
	public String toString() {
		return name;
	}
}
