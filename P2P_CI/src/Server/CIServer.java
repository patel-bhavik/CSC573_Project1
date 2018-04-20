package Server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;

import CommunicationProtocol.Response;
import Constants.Constant;
import Constants.FormatCharacter;
import Constants.Header;
import Constants.Method;
import Constants.StatusCode;
import Utility.DisplayOnConsole;

public class CIServer implements Runnable {

	Socket clientSocket;
	Hashtable<RFC,LinkedList<Peer>> rfcData;
	Hashtable<String,String> hostToIpMap;
	
	CIServer(Socket clientSocket, Hashtable<RFC,LinkedList<Peer>> rfcData, Hashtable<String,String> hostToIpMap){
		this.clientSocket = clientSocket;
		this.rfcData = rfcData;
		this.hostToIpMap = hostToIpMap;
	}
	
	public boolean addRFC(String rfcNumber, String rfcTitle, String hostName, String port) {
		RFC rfcToAdd = new RFC(Integer.parseInt(rfcNumber), rfcTitle);
		Peer peerWithRfc = new Peer(hostName, Integer.parseInt(port), hostToIpMap.get(hostName));
		LinkedList<Peer> peerList;
		if(rfcData.containsKey(rfcToAdd)) {
			peerList = rfcData.get(rfcToAdd);
			if(peerList.contains(peerWithRfc))
				return false;
			peerList.add(peerWithRfc);
		}else {
			peerList = new LinkedList<Peer>();
			peerList.add(peerWithRfc);
			rfcData.put(rfcToAdd, peerList);
		}
		return true;
	}
	
	@Override
	public void run() {
		
		// Set IO Stream
		ObjectOutputStream  serverOutputStream = null;
		ObjectInputStream  serverInputStream = null;
		DisplayOnConsole print = new DisplayOnConsole();
					
		try {
			
			// Set IO Stream
			serverOutputStream = new ObjectOutputStream (clientSocket.getOutputStream());
			serverInputStream = new ObjectInputStream (clientSocket.getInputStream());
			
			// Add new client to Peer List
			String clientHostName = (String)serverInputStream.readObject();
			String clientIpAddress = (String)serverInputStream.readObject();
			hostToIpMap.put(clientHostName, clientIpAddress);
			
			// Connection Established Message
			print.displayConnectionMessage(Constant.ESTABLISH.getValue(), Constant.CLIENT.getValue(), clientHostName);
			
			// Start serving Client
			String method = null;
			do {
				String request = (String)serverInputStream.readObject();
				Response createResposne = new Response();
				method = request.substring(0, request.indexOf(FormatCharacter.TAB.getValue()));
				HashMap<String,String> resParams=null;
				String response = null;
				
				switch (method) {
					case "ADD" : print.displayMessage(Constant.REQ.getValue(), request, Method.ADD.name(), Constant.RCVD.getValue(), Constant.CLIENT.getValue() + FormatCharacter.COL.getValue() + FormatCharacter.SP.getValue() + clientHostName);
								 resParams = createResposne.parseAddRequest(request);
								 String statusCode = resParams.get(Constant.STATUS_CODE.getValue());
								 String statusPhrase = resParams.get(Constant.STATUS_PHRASE.getValue());
								 if(statusCode.equals(StatusCode.OK.getCode())) {
									 String rfcNumber = resParams.get(Constant.RFC_NUM.name());
									 String rfcTitle = resParams.get(Header.TITLE.getValue());
									 String hostName = resParams.get(Header.HOST.getValue());
									 String port = resParams.get(Header.PORT.getValue());
									 boolean isAdded = addRFC(rfcNumber, rfcTitle, hostName, port);
									 if(isAdded)
										 response = createResposne.getAddResponse(statusCode, statusPhrase, rfcNumber, rfcTitle, hostName, port);
									 else
										 response = createResposne.getAddResponse(StatusCode.BAD_REQUEST.getCode(), StatusCode.BAD_REQUEST.getPhrase());
								 }else {
									 response = createResposne.getAddResponse(statusCode, statusPhrase);
								 }
								 print.displayMessage(Constant.RES.getValue(), response, Method.ADD.name(), Constant.SENT.getValue(), Constant.CLIENT.getValue() + FormatCharacter.COL.getValue() + FormatCharacter.SP.getValue() + clientHostName);
								 break;
					
					case "LOOKUP" : System.out.println("Received LOOKUP request from Host: "+clientHostName);
									resParams = createResposne.parseLookupRequest(request);
					 				System.out.println(resParams);
					 				System.out.println("Served LOOKUP request from Host: "+clientHostName);
					 				break;
					
					case "LIST" : System.out.println("Received LIST request from Host: "+clientHostName);
								  resParams = createResposne.parseListRequest(request);
	 							  System.out.println(resParams);
	 							  System.out.println("Served LIST request from Host: "+clientHostName);
	 							  break;
					
					case "EXIT" : System.out.println("Received EXIT request from Host: "+clientHostName);
								  resParams = createResposne.parseExitRequest(request);
					  			  System.out.println(resParams);
					  			  System.out.println("Served EXIT request from Host: "+clientHostName);
					  			  break;
					
					default : System.out.println("Received INVALID request from Host: "+clientHostName);
							  resParams = new HashMap<String,String>();
							  createResposne.addBadRequestStatusCode(resParams);
							  System.out.println("Served INVALID request from Host: "+clientHostName);
							  System.out.println(resParams);
				}
				
				serverOutputStream.writeObject(response);
				
			}while(!method.equals(Method.EXIT.name()));
			
			// Connection Termination Message
			print.displayConnectionMessage(Constant.TERMINATE.getValue(), Constant.CLIENT.getValue(), clientHostName);
						
		}catch(Exception exp) {
			System.out.println("Error occured while communication.");
			exp.printStackTrace();
		}
	}
}
