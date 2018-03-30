package Server;

public class RFC {

	private int rfcNumber;
	private String rfcTitle;
	private String hostName;
	
	RFC(int rfcNumber, String rfcTitle, String hostName){
		this.rfcNumber = rfcNumber;
		this.rfcTitle = rfcTitle;
		this.hostName = hostName;
	}

	public int getRfcNumber() {
		return rfcNumber;
	}

	public void setRfcNumber(int rfcNumber) {
		this.rfcNumber = rfcNumber;
	}

	public String getRfcTitle() {
		return rfcTitle;
	}

	public void setRfcTitle(String rfcTitle) {
		this.rfcTitle = rfcTitle;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	
	public String toString() {
		return "[Hostname: "+this.getHostName()+", RFC Number: "+this.rfcNumber+", RFC Title: "+this.rfcTitle+"]";
	}
}
