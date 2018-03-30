package Server;

import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.List;

public class CIServer implements Runnable {

	Socket clientSocket;
	List<Peer> peerList;
	List<RFC> rfcList;
	
	CIServer(Socket clientSocket, List<Peer> peerList, List<RFC> rfcList){
		this.clientSocket = clientSocket;
		this.peerList = peerList;
		this.rfcList = rfcList;
	}
	
	@Override
	public void run() {
		
		try {
			
			// Set Input Stream
			ObjectInputStream  clientInputStream = new ObjectInputStream (clientSocket.getInputStream());
			
			// Add new client to Peer List
			String clientIpAddress = clientSocket.getInetAddress().getHostName();
			int clientPort = clientInputStream.readInt();
			String clientHostName = (String)clientInputStream.readObject();
			Peer peerClient = new Peer(clientHostName,clientPort,clientIpAddress);
			peerList.add(peerClient);
		}catch(Exception exp) {
			System.out.println("Error occured while communication.");
			exp.printStackTrace();
		}
	}
}
