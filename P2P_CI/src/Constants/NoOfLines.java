package Constants;

public enum NoOfLines {
	ADD(4),
	LOOKUP(4),
	LIST(3),
	GET(3),
	EXIT(2);
	
	private final int count;

	NoOfLines(int count) {
        this.count = count;
    }
    
    public int getLines() {
        return this.count;
    }
}
