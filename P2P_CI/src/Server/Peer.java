package Server;

public class Peer {
	
	private String hostName;
	private String ipAddress;
	private int port;
	
	Peer(String hostName, int port, String ipAddress){
		this.ipAddress = ipAddress;
		this.hostName = hostName;
		this.port = port;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostNname(String hostName) {
		this.hostName = hostName;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	
	public String toString() {
		return "[Hostname: "+this.getHostName()+", Port: "+this.getPort()+", IP Address: "+this.getIpAddress()+"]";
	}
	
}
