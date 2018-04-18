package Server;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

public class ServerInitialization {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		final int serverPort = 7734;
		ServerSocket centralIndexServer;
		Socket clientSocket;
		//List<Peer> peerList = Collections.synchronizedList(new LinkedList<Peer>());
		//List<RFC> rfcList = Collections.synchronizedList(new LinkedList<RFC>());
		Hashtable<String,String> hostToIpMap = new Hashtable<String,String>();
		Hashtable<RFC,List<Peer>> rfcData = new Hashtable<RFC,List<Peer>>();
		
		try {
			
			// Starting Server
			centralIndexServer = new ServerSocket(serverPort);
			System.out.println("Centralized Index server started on "+InetAddress.getLocalHost().getHostAddress()+":"+serverPort+".");
			
			// Accepting Client Connections
			while(true) {
				clientSocket = centralIndexServer.accept();
				CIServer newClient = new CIServer(clientSocket,rfcData,hostToIpMap);
				Thread t = new Thread(newClient);
				t.start();
			}
		}catch(Exception exp) {
			System.out.println("Error occured while starting the server.");
			exp.printStackTrace();
		}
	}

}
