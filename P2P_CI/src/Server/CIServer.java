package Server;

import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import CommunicationProtocol.Response;
import Constants.FormatCharacter;
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
				Response createResposne = new Response();
				method = request.substring(0, request.indexOf(FormatCharacter.TAB.getValue()));
				HashMap<String,String> resParams=null;
				switch (method) {
					case "ADD" : resParams = createResposne.parseAddRequest(request);
								 System.out.println(resParams);
								 break;
					case "LOOKUP" : resParams = createResposne.parseLookupRequest(request);
					 				System.out.println(resParams);
					 				break;
					case "LIST" : resParams = createResposne.parseListRequest(request);
	 							  System.out.println(resParams);
	 							  break;
					case "EXIT" : resParams = createResposne.parseExitRequest(request);
					  			  System.out.println(resParams);
					  			  break;
					default : resParams = new HashMap<String,String>();
							  createResposne.addBadRequestStatusCode(resParams);
							  System.out.println(resParams);
				}
				
			}while(!method.equals(Method.EXIT.name()));
			
			// Connection Termination Message
			System.out.println("Connection with " + clientHostName + " client terminated.");
						
		}catch(Exception exp) {
			System.out.println("Error occured while communication.");
			exp.printStackTrace();
		}
	}
}
