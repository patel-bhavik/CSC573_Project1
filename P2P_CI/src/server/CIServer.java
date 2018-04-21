package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

import communication.protocol.Response;
import constants.Constant;
import constants.FormatCharacter;
import constants.Header;
import constants.Method;
import constants.StatusCode;
import utility.DisplayOnConsole;

public class CIServer implements Runnable {

	private Socket clientSocket;
	private Hashtable<RFC,LinkedList<Peer>> rfcData;
	private Hashtable<String,String> hostToIpMap;
	
	CIServer(Socket clientSocket, Hashtable<RFC,LinkedList<Peer>> rfcData, Hashtable<String,String> hostToIpMap){
		this.clientSocket = clientSocket;
		this.rfcData = rfcData;
		this.hostToIpMap = hostToIpMap;
	}
	
	public void cleanUp(ObjectInputStream inStream, ObjectOutputStream outStream) {
		DisplayOnConsole print = new DisplayOnConsole();
		try {
			if(inStream != null)
				inStream.close();
			if(outStream != null)
				outStream.close();
			if(clientSocket != null)
				clientSocket.close();
		}catch(IOException exp) {
			print.errorMessage(Constant.CI_SERVER.getValue(), Constant.CLEANUP.getValue(), exp.getMessage());
		}
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
	
	public void removePeer(String hostName, String port) {
		Peer peerToRemove = new Peer(hostName, Integer.parseInt(port), hostToIpMap.get(hostName));
		Iterator<RFC> keyIterator = rfcData.keySet().iterator();
		
		while(keyIterator.hasNext()) {
			LinkedList<Peer> peerList = rfcData.get(keyIterator.next());
			if(peerList.contains(peerToRemove)) {
				peerList.remove(peerToRemove);
				if(peerList.isEmpty()) {
					keyIterator.remove();
				}
			}
		}
	}
	
	public RFC getSpecificRFC(RFC rfcToSearch) {
		RFC foundRFC = null;
		Iterator<RFC> keyIterator = rfcData.keySet().iterator();
		while(keyIterator.hasNext()) {
			foundRFC = keyIterator.next();
			if(foundRFC.getRfcNumber() == rfcToSearch.getRfcNumber())
				return foundRFC;
		}
		return foundRFC;
	}
	
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
			print.connectionMessage(Constant.ESTABLISH.getValue(), Constant.CLIENT.getValue(), clientHostName);
			
			// Start serving Client
			String method = null;
			boolean isCleanup = false;
			do {
				String request = (String)serverInputStream.readObject();
				Response createResposne = new Response();
				method = request.substring(0, request.indexOf(FormatCharacter.TAB.getValue()));
				HashMap<String,String> resParams=null;
				String response = null;
				String statusCode = null;
				String statusPhrase = null;
				switch (method) {
					case "ADD" : print.communicationMessage(Constant.REQ.getValue(), request, Method.ADD.name(), Constant.RCVD.getValue(), Constant.CLIENT.getValue() + FormatCharacter.COL.getValue() + FormatCharacter.SP.getValue() + clientHostName);
								 resParams = createResposne.parseAddRequest(request);
								 statusCode = resParams.get(Constant.STATUS_CODE.getValue());
								 statusPhrase = resParams.get(Constant.STATUS_PHRASE.getValue());
								 if(statusCode.equals(StatusCode.OK.getCode())) {
									 String rfcNumber = resParams.get(Constant.RFC_NUM.name());
									 String rfcTitle = resParams.get(Header.TITLE.getValue());
									 String hostName = resParams.get(Header.HOST.getValue());
									 String port = resParams.get(Header.PORT.getValue());
									 if(addRFC(rfcNumber, rfcTitle, hostName, port)) {
										 RFC rfcToLookup = new RFC(Integer.parseInt(rfcNumber), rfcTitle);
										 RFC addedRFC = getSpecificRFC(rfcToLookup);
										 response = createResposne.getAddResponse(statusCode, statusPhrase, rfcNumber, addedRFC.getRfcTitle(), hostName, port); 
									 }
									 else {
										 response = createResposne.getResponseHeader(StatusCode.BAD_REQUEST.getCode(), StatusCode.BAD_REQUEST.getPhrase());
									 }
								 }else {
									 response = createResposne.getResponseHeader(statusCode, statusPhrase);
								 }
								 serverOutputStream.writeObject(response);
								 print.communicationMessage(Constant.RES.getValue(), response, Method.ADD.name(), Constant.SENT.getValue(), Constant.CLIENT.getValue() + FormatCharacter.COL.getValue() + FormatCharacter.SP.getValue() + clientHostName);
								 break;
					
					case "LOOKUP" : print.communicationMessage(Constant.REQ.getValue(), request, Method.LOOKUP.name(), Constant.RCVD.getValue(), Constant.CLIENT.getValue() + FormatCharacter.COL.getValue() + FormatCharacter.SP.getValue() + clientHostName);
					 				resParams = createResposne.parseLookupRequest(request);
					 				statusCode = resParams.get(Constant.STATUS_CODE.getValue());
					 				statusPhrase = resParams.get(Constant.STATUS_PHRASE.getValue());
					 				if(statusCode.equals(StatusCode.OK.getCode())) {
					 					String rfcNumber = resParams.get(Constant.RFC_NUM.name());
					 					String rfcTitle = resParams.get(Header.TITLE.getValue());
					 					RFC rfcToLookup = new RFC(Integer.parseInt(rfcNumber), rfcTitle);
					 					if(rfcData.containsKey(rfcToLookup)) {
					 						RFC lookedUpRFC = getSpecificRFC(rfcToLookup);
					 						LinkedList<Peer> peerList = rfcData.get(rfcToLookup);
					 						response = createResposne.getLookupResponse(statusCode, statusPhrase, rfcNumber, lookedUpRFC.getRfcTitle(), peerList);
					 					}else {
					 						response = createResposne.getResponseHeader(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getPhrase());
					 					}
					 				}else {
										 response = createResposne.getResponseHeader(statusCode, statusPhrase);
									}
					 				serverOutputStream.writeObject(response);
					 				print.communicationMessage(Constant.RES.getValue(), response, Method.LOOKUP.name(), Constant.SENT.getValue(), Constant.CLIENT.getValue() + FormatCharacter.COL.getValue() + FormatCharacter.SP.getValue() + clientHostName);
									break;
					
					case "LIST" : print.communicationMessage(Constant.REQ.getValue(), request, Method.LIST.name(), Constant.RCVD.getValue(), Constant.CLIENT.getValue() + FormatCharacter.COL.getValue() + FormatCharacter.SP.getValue() + clientHostName);
								  if(rfcData.size() > 0) {
									  resParams = createResposne.parseListRequest(request);
									  statusCode = resParams.get(Constant.STATUS_CODE.getValue());
									  statusPhrase = resParams.get(Constant.STATUS_PHRASE.getValue());
									  if(statusCode.equals(StatusCode.OK.getCode())) {
										  response = createResposne.getListResponse(statusCode, statusPhrase, rfcData);
									  }else {
										  response = createResposne.getResponseHeader(statusCode, statusPhrase);
									  }
								  }else {
									  response = createResposne.getResponseHeader(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getPhrase());
								  }
								  serverOutputStream.writeObject(response);
								  print.communicationMessage(Constant.RES.getValue(), response, Method.LIST.name(), Constant.SENT.getValue(), Constant.CLIENT.getValue() + FormatCharacter.COL.getValue() + FormatCharacter.SP.getValue() + clientHostName);
	 							  break;
					
					case "EXIT" : print.communicationMessage(Constant.REQ.getValue(), request, Method.EXIT.name(), Constant.RCVD.getValue(), Constant.CLIENT.getValue() + FormatCharacter.COL.getValue() + FormatCharacter.SP.getValue() + clientHostName);
								  resParams = createResposne.parseExitRequest(request);
								  statusCode = resParams.get(Constant.STATUS_CODE.getValue());
					 			  statusPhrase = resParams.get(Constant.STATUS_PHRASE.getValue());
					 			  if(statusCode.equals(StatusCode.OK.getCode())) {
					 				 String hostName = resParams.get(Header.HOST.getValue());
					 				 String port = resParams.get(Header.PORT.getValue());
					 				 if(hostToIpMap.containsKey(hostName)) {
					 					removePeer(hostName,port);
					 					response = createResposne.getResponseHeader(statusCode, statusPhrase);
					 					isCleanup = true;
					 				 }else {
					 					response = createResposne.getResponseHeader(StatusCode.BAD_REQUEST.getCode(), StatusCode.BAD_REQUEST.getPhrase());
					 				 }
					 			  }else {
					 				 response = createResposne.getResponseHeader(statusCode, statusPhrase);
					 			  }
					 			  serverOutputStream.writeObject(response);
					 			  print.communicationMessage(Constant.RES.getValue(), response, Method.EXIT.name(), Constant.SENT.getValue(), Constant.CLIENT.getValue() + FormatCharacter.COL.getValue() + FormatCharacter.SP.getValue() + clientHostName);
					 			  if(isCleanup) {
					 				 cleanUp(serverInputStream, serverOutputStream);  
					 			  }
					 			  break;
					
					default : print.communicationMessage(Constant.REQ.getValue(), request, Method.INVALID.name(), Constant.RCVD.getValue(), Constant.CLIENT.getValue() + FormatCharacter.COL.getValue() + FormatCharacter.SP.getValue() + clientHostName);
							  response = createResposne.getResponseHeader(StatusCode.BAD_REQUEST.getCode(), StatusCode.BAD_REQUEST.getPhrase());
							  serverOutputStream.writeObject(response);
							  print.communicationMessage(Constant.RES.getValue(), response, Method.INVALID.name(), Constant.SENT.getValue(), Constant.CLIENT.getValue() + FormatCharacter.COL.getValue() + FormatCharacter.SP.getValue() + clientHostName);
				}
			}while(!isCleanup);
			
			// Connection Termination Message
			print.connectionMessage(Constant.TERMINATE.getValue(), Constant.CLIENT.getValue(), clientHostName);
						
		}catch(Exception exp) {
			print.errorMessage(Constant.CI_SERVER.getValue(), Constant.COMMUNICATION.getValue(), exp.getMessage());
		}finally {
			cleanUp(serverInputStream,serverOutputStream);
		}
	}
}
