package Server;

public class RFC {

	private int rfcNumber;
	private String rfcTitle;
	
	RFC(int rfcNumber, String rfcTitle, String hostName){
		this.rfcNumber = rfcNumber;
		this.rfcTitle = rfcTitle;
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
	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + rfcNumber;
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RFC other = (RFC) obj;
		if (rfcNumber != other.rfcNumber)
			return false;
		return true;
	}

	public String toString() {
		return "[RFC Number: "+this.rfcNumber+", RFC Title: "+this.rfcTitle+"]";
	}
}
