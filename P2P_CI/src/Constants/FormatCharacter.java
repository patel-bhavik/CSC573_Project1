package Constants;

public enum FormatCharacter{
	CR("\r"),
	LF("\n"),
	SP(" "),
	TAB("\t"),
	COL(":");
	
	private final String charValue;

	FormatCharacter(String charValue) {
        this.charValue = charValue;
    }
    
    public String getValue() {
        return this.charValue;
    }
	
}
