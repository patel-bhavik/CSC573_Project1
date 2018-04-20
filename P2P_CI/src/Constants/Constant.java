package Constants;

public enum Constant{
	RFC("RFC"),
	VERSION("P2P-CI/1.0"),
	ALL("ALL"),
	STATUS_CODE("STATUS_CODE"),
	STATUS_PHRASE("PHRASE"),
	RFC_NUM("RFC_NUM"),
	RCVD("received"),
	SENT("sent"),
	SERVER("server"),
	CLIENT("client"),
	FROM("from"),
	TO("to"),
	SRVR_TARGET_NAME("server running at"),
	ESTABLISH("established"),
	TERMINATE("terminated"),
	REQ("Request"),
	RES("Response"),
	CI_SERVER("Centralized Index server"),
	UPLOAD_SERVER("Upload server"),
	COMMUNICATION("communication"),
	INITIALIZATION("initialization"),
	CLEANUP("clean up");
	
	private final String constValue;

	Constant(String constValue) {
        this.constValue = constValue;
    }
    
    public String getValue() {
        return this.constValue;
    }
    
    public static boolean contains(String key) {
    	
    	for (Constant c : Constant.values()) {
            if (c.getValue().equals(key)) {
                return true;
            }
        }

        return false;
    }
}
