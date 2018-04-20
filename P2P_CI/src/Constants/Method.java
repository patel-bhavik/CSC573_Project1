package Constants;

public enum Method{
	ADD,
	LOOKUP,
	LIST,
	GET,
	EXIT,
	INVALID;
	
	public static boolean contains(String key) {
    	
    	for (Method m : Method.values()) {
            if (m.name().equals(key)) {
                return true;
            }
        }

        return false;
    }
}
