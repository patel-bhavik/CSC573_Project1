package Constants;

public enum Header{
	HOST("Host"),
	TITLE("Title"),
	PORT("Port"),
	OS("OS");
	
	private final String header;

    Header(String header) {
        this.header = header;
    }
    
    public String getValue() {
        return this.header;
    }
	
}
