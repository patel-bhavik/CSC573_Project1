package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

import communication.protocol.Request;
import communication.protocol.Response;
import constants.Constant;
import constants.FormatCharacter;
import constants.Method;
import constants.StatusCode;
import utility.DisplayOnConsole;

public class PeerClients {
	
	public static void cleanUp(ObjectInputStream inStream, ObjectOutputStream outStream, Scanner sc, Socket peerClient, ServerSocket peerServer, Socket rfcClient) {
		DisplayOnConsole print = new DisplayOnConsole();
		try {
			if(inStream != null)
				inStream.close();
			if(outStream != null)
				outStream.close();
			if(sc != null)
				sc.close();
			if(peerServer != null)
				peerServer.close();
			if(rfcClient != null)
				rfcClient.close();
			if(peerClient != null)
				peerClient.close();
		}catch(IOException exp) {
			print.errorMessage(Constant.CLIENT.getValue(), Constant.CLEANUP.getValue(), exp.getMessage());
		}
	}
	
	public static void cleanUpUploadServer(ObjectInputStream inStream, ObjectOutputStream outStream, Socket rfcClient){
		DisplayOnConsole print = new DisplayOnConsole();
		try {
			if(inStream != null)
				inStream.close();
			if(outStream != null)
				outStream.close();
			if(rfcClient != null)
				rfcClient.close();
		}catch(IOException exp) {
			print.errorMessage(Constant.CLIENT.getValue(), Constant.CLEANUP.getValue(), exp.getMessage());
		}
	}
	
	public static boolean saveRFCFile(String getResponse, String rfcNumber, String rfcDirPath) {
		Response response = new Response();
		String filePath = rfcDirPath + FormatCharacter.FSL.getValue() + rfcNumber + Constant.FILE_EXT.getValue();
		return response.processDownloadResponse(getResponse, filePath);
	}
	
	public static void sendGetCommunication(Socket rfcClient, String uploadServerAddress, int uploadServerPort, String getRequest, String rfcNumber, String rfcTitle, ObjectInputStream clientInputStream, ObjectOutputStream clientOutputStream, Request createRequest, String rfcDirPath) {
		DisplayOnConsole print = new DisplayOnConsole();
		ObjectInputStream rfcClientInputStream = null;
		ObjectOutputStream rfcClientOutputStream = null;
		try{
			rfcClient = new Socket(uploadServerAddress,uploadServerPort);
			print.connectionMessage(Constant.ESTABLISH.getValue(), Constant.SERVER.getValue(), uploadServerAddress+ FormatCharacter.COL.getValue() +uploadServerPort);
			rfcClientOutputStream = new ObjectOutputStream (rfcClient.getOutputStream());
			rfcClientInputStream = new ObjectInputStream (rfcClient.getInputStream());
			rfcClientOutputStream.writeObject(getRequest);
			print.communicationMessage(Constant.REQ.getValue(), getRequest, Method.GET.name(), Constant.SENT.getValue(), Constant.UPLOAD_SERVER.getValue());
			String getResponse = (String)rfcClientInputStream.readObject();
			print.communicationMessage(Constant.RES.getValue(), getResponse, Method.GET.name(), Constant.RCVD.getValue(), Constant.UPLOAD_SERVER.getValue());
			String exitRequest = createRequest.getExitRequest();
			rfcClientOutputStream.writeObject(exitRequest);
			print.communicationMessage(Constant.REQ.getValue(), exitRequest, Method.EXIT.name(), Constant.SENT.getValue(), Constant.UPLOAD_SERVER.getValue());
			String exitResponse = (String)rfcClientInputStream.readObject();
			print.communicationMessage(Constant.RES.getValue(), exitResponse, Method.EXIT.name(), Constant.RCVD.getValue(), Constant.UPLOAD_SERVER.getValue());
			cleanUpUploadServer(rfcClientInputStream, rfcClientOutputStream, rfcClient);
			if(getResponse.contains(StatusCode.OK.getCode()) && saveRFCFile(getResponse, rfcNumber, rfcDirPath)) {
				String getAddRequest = createRequest.getAddRequest(rfcNumber,rfcTitle);
				clientOutputStream.writeObject(getAddRequest);
				print.communicationMessage(Constant.REQ.getValue(), getAddRequest, Method.ADD.name(), Constant.SENT.getValue(), Constant.CI_SERVER.getValue());
				String getAddResponse = (String)clientInputStream.readObject();
				print.communicationMessage(Constant.RES.getValue(), getAddResponse, Method.ADD.name(), Constant.RCVD.getValue(), Constant.CI_SERVER.getValue());
			}
		}catch(Exception exp) {
			print.errorMessage(Constant.UPLOAD_SERVER.getValue(), Constant.INITIALIZATION.getValue(), exp.getMessage());
		}finally {
			cleanUpUploadServer(rfcClientInputStream, rfcClientOutputStream, rfcClient);
		}
	}
	
	public static void main(String[] args) {
		
		// Initialize Socket and IO Streams
		Socket peerClient = null;
		Socket rfcClient = null;
		ServerSocket peerServer = null;
		Scanner sc = new Scanner(System.in);
		ObjectInputStream clientInputStream = null;
		ObjectOutputStream clientOutputStream = null;
		DisplayOnConsole print = new DisplayOnConsole();
		
		try {
			
			// Get Server Details
			System.out.print("Enter IP Address of server to connect: ");
			String serverAddress = sc.next();
			final int serverPort = 7734;
			sc.nextLine();
			System.out.print("Enter RFC directory path: ");
			String rfcDirPath = sc.nextLine();
			
			// Start Upload Server
			Random randNumber = new Random();
			int peerServerPort = randNumber.nextInt(15000)+50000;
			UploadServer newServer = new UploadServer(peerServerPort, rfcDirPath);
			Thread t = new Thread(newServer);
			t.start();
			
			// Set Host Details
			String hostName = InetAddress.getLocalHost().getHostName() + FormatCharacter.US.getValue() + peerServerPort;
			String ipAddress = InetAddress.getLocalHost().getHostAddress();
			String uploadPort = Integer.toString(peerServerPort);
			String os = System.getProperty("os.name");
			
			// Connect with server
			peerClient = new Socket(serverAddress,serverPort);
			print.connectionMessage(Constant.ESTABLISH.getValue(), Constant.SERVER.getValue(), serverAddress+ FormatCharacter.COL.getValue() + serverPort);
			
			// Create IO Streams
			clientOutputStream = new ObjectOutputStream (peerClient.getOutputStream());
			clientInputStream = new ObjectInputStream (peerClient.getInputStream());
			
			// Send self details
			clientOutputStream.writeObject(hostName);
			clientOutputStream.writeObject(ipAddress);
			
			// Start Server Communication
			int choice;
			boolean notExit = true;
			Request createRequest = new Request(hostName, uploadPort, os);
			String rfcNumber;
			String rfcTitle;
			do {
				System.out.println("Menu");
				System.out.println("1. Add RFC");
				System.out.println("2. Lookup RFC");
				System.out.println("3. List All RFCs");
				System.out.println("4. Download RFC");
				System.out.println("5. Exit");
				System.out.print("Enter your choice: ");
				choice = sc.nextInt();
				
				switch (choice) {
					case 1:	System.out.print("Enter RFC number: ");
							rfcNumber = sc.next();
							sc.nextLine();
							System.out.print("Enter RFC title: ");
							rfcTitle = sc.nextLine();
							String addRequest = createRequest.getAddRequest(rfcNumber,rfcTitle);
							clientOutputStream.writeObject(addRequest);
							print.communicationMessage(Constant.REQ.getValue(), addRequest, Method.ADD.name(), Constant.SENT.getValue(), Constant.CI_SERVER.getValue());
							String addResponse = (String)clientInputStream.readObject();
							print.communicationMessage(Constant.RES.getValue(), addResponse, Method.ADD.name(), Constant.RCVD.getValue(), Constant.CI_SERVER.getValue());
							break;
					
					case 2: System.out.print("Enter RFC number: ");
							rfcNumber = sc.next();
							sc.nextLine();
							System.out.print("Enter RFC title: ");
							rfcTitle = sc.nextLine();
							String lookupRequest = createRequest.getLookUpRequest(rfcNumber,rfcTitle);
							clientOutputStream.writeObject(lookupRequest);
							print.communicationMessage(Constant.REQ.getValue(), lookupRequest, Method.LOOKUP.name(), Constant.SENT.getValue(), Constant.CI_SERVER.getValue());
							String lookupResponse = (String)clientInputStream.readObject();
							print.communicationMessage(Constant.RES.getValue(), lookupResponse, Method.LOOKUP.name(), Constant.RCVD.getValue(), Constant.CI_SERVER.getValue());
							break;
					
					case 3: String listRequest = createRequest.getListRequest();
							clientOutputStream.writeObject(listRequest);
							print.communicationMessage(Constant.REQ.getValue(), listRequest, Method.LIST.name(), Constant.SENT.getValue(), Constant.CI_SERVER.getValue());
							String listResponse = (String)clientInputStream.readObject();
							print.communicationMessage(Constant.RES.getValue(), listResponse, Method.LIST.name(), Constant.RCVD.getValue(), Constant.CI_SERVER.getValue());
							break;
					
					case 4: System.out.print("Enter RFC number: ");
							rfcNumber = sc.next();
							System.out.print("Enter RFC title: ");
							rfcTitle = sc.next();
							String getRequest = createRequest.getDownloadRequest(rfcNumber);
							sc.nextLine();
							System.out.print("Enter IP Address of server containing RFC file: ");
							String uploadServerAddress = sc.next();
							System.out.print("Enter Port of server to connect: ");
							int uploadServerPort = sc.nextInt();
							if(uploadServerAddress.equals(ipAddress) && Integer.toString(uploadServerPort).equals(uploadPort)) {
								System.out.println("You have given your own details. Please try again with valid server details");
							}else {
								sendGetCommunication(rfcClient, uploadServerAddress, uploadServerPort, getRequest, rfcNumber, rfcTitle, clientInputStream, clientOutputStream, createRequest, rfcDirPath);
							}
							break;
					
					case 5: System.out.print("Are you sure you want to exit? (Y/N): ");
							String confirmation = sc.next();
							if(confirmation.length() == 1 && confirmation.equalsIgnoreCase("Y")) {
								String exitRequest = createRequest.getExitRequest();
								clientOutputStream.writeObject(exitRequest);
								print.communicationMessage(Constant.REQ.getValue(), exitRequest, Method.EXIT.name(), Constant.SENT.getValue(), Constant.CI_SERVER.getValue());
								String exitResponse = (String)clientInputStream.readObject();
								print.communicationMessage(Constant.RES.getValue(), exitResponse, Method.EXIT.name(), Constant.RCVD.getValue(), Constant.CI_SERVER.getValue());
								if(exitResponse.contains(StatusCode.OK.getCode())) {
									notExit = false;
									cleanUp(clientInputStream,clientOutputStream,sc,peerClient,peerServer,rfcClient);
								}
							}
							break;
					
					default: System.out.println("Invalid Choice. Please try again.");
							 break;
				}
			}while(notExit);
			
			print.connectionMessage(Constant.TERMINATE.getValue(), Constant.SERVER.getValue(), serverAddress+ FormatCharacter.COL.getValue() +serverPort);
			
		}catch(Exception exp) {
			print.errorMessage(Constant.CLIENT.getValue(), Constant.COMMUNICATION.getValue(), exp.getMessage());
		}finally {
			cleanUp(clientInputStream,clientOutputStream,sc,peerClient,peerServer,rfcClient);
		}
	}

}
