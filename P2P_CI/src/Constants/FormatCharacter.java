package Constants;

public enum FormatCharacter{
	CR("\r"),
	LF("\n"),
	SP(" "),
	TAB("\t"),
	COL(":"),
	US("_");
	
	private final String charValue;

	FormatCharacter(String charValue) {
        this.charValue = charValue;
    }
    
    public String getValue() {
        return this.charValue;
    }
    
    public static boolean contains(String key) {
    	
    	for (FormatCharacter fc : FormatCharacter.values()) {
            if (fc.getValue().equals(key)) {
                return true;
            }
        }

        return false;
    }
	
}
