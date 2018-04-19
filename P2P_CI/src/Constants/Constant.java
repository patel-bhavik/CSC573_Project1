package Constants;

public enum Constant{
	RFC("RFC"),
	VERSION("P2P-CI/1.0"),
	ALL("ALL");
	
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