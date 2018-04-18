package Server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Hashtable;
import java.util.List;

import Constants.Method;

public class CIServer implements Runnable {

	Socket clientSocket;
	Hashtable<RFC,List<Peer>> rfcData;
	Hashtable<String,String> hostToIpMap;
	
	CIServer(Socket clientSocket, Hashtable<RFC,List<Peer>> rfcData, Hashtable<String,String> hostToIpMap){
		this.clientSocket = clientSocket;
		this.rfcData = rfcData;
		this.hostToIpMap = hostToIpMap;
	}
	
	@Override
	public void run() {
		
		try {
			
			// Set IO Stream
			ObjectInputStream  serverInputStream = new ObjectInputStream (clientSocket.getInputStream());
			
			// Add new client to Peer List
			String clientHostName = (String)serverInputStream.readObject();
			String clientIpAddress = (String)serverInputStream.readObject();
			hostToIpMap.put(clientHostName, clientIpAddress);
			
			// Connection Established Message
			System.out.println("Connection establised with " + clientHostName + " client.");
			
			// Start serving Client
			String method = null;
			do {
				String request = (String)serverInputStream.readObject();
				method = request.substring(0, request.indexOf("\t"));
				System.out.println(method);
				if(!method.equals(Method.GET.name()) && Method.contains(method))
					System.out.println("Valid Request");
				else
					System.out.println("Invalid Request");
			}while(!method.equals(Method.EXIT.name()));
			
			// Connection Termination Message
			System.out.println("Connection with " + clientHostName + " client terminated.");
						
		}catch(Exception exp) {
			System.out.println("Error occured while communication.");
			exp.printStackTrace();
		}
	}
}
