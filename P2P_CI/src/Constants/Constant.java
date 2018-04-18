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
}
