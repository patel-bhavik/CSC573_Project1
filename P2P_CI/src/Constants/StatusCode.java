package Constants;

public enum StatusCode {
	OK("200","OK"),
	BAD_REQUEST("400","Bad Request"),
	NOT_FOUND("404","Not Found"),
	VERSION_NOT_SUPPORTED("505","P2P-CI Version Not Supported");
	
	private final String phrase;
	private final String code;
	
	StatusCode(String code, String phrase) {
		this.code = code;
		this.phrase = phrase;
	}
	
	public String getCode() {
        return this.code;
    }
	
	public String getPhrase() {
        return this.phrase;
    }
	
	public static boolean containsCode(String key) {
    	
    	for (StatusCode s : StatusCode.values()) {
            if (s.getCode().equals(key)) {
                return true;
            }
        }

        return false;
    }
	
	public static boolean containsPhrase(String key) {
    	
    	for (StatusCode s : StatusCode.values()) {
            if (s.getPhrase().equals(key)) {
                return true;
            }
        }

        return false;
    }
}
