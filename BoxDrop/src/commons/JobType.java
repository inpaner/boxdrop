package commons;

public enum JobType {
	CREATE("Create"), MODIFY("Modify"), DELETE("Delete"), REQUEST("Request"), LIST("List");
	
	String name;
	
	JobType(String name) {
		this.name = name;
	}
	
	public String toString() {
		return name;
	}
}
