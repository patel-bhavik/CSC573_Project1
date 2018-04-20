package Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

import CommunicationProtocol.Request;
import Constants.Constant;
import Constants.FormatCharacter;
import Constants.Method;
import Constants.StatusCode;
import Utility.DisplayOnConsole;

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
			
			// Start Upload Process
			Random randNumber = new Random();
			int peerServerPort = randNumber.nextInt(15000)+50000;
			peerServer = new ServerSocket(peerServerPort);
			
			// Set Host Details
			String hostName = InetAddress.getLocalHost().getHostName() + FormatCharacter.US.getValue() + peerServerPort;
			String ipAddress = InetAddress.getLocalHost().getHostAddress();
			String uploadPort = Integer.toString(peerServerPort);
			String os = System.getProperty("os.name");
			
			// Get Server Details
			System.out.print("Enter IP Address of server to connect: ");
			String serverAddress = sc.next();
			System.out.print("Enter Port of server to connect: ");
			int serverPort = sc.nextInt();
			
			// Connect with server
			peerClient = new Socket(serverAddress,serverPort);
			print.connectionMessage(Constant.ESTABLISH.getValue(), Constant.SERVER.getValue(), serverAddress+ FormatCharacter.COL.getValue() +serverPort);
			
			// Create Output Streams
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
							print.communicationMessage(Constant.REQ.getValue(), addRequest, Method.ADD.name(), Constant.SENT.getValue(), Constant.SERVER.getValue());
							String addResponse = (String)clientInputStream.readObject();
							print.communicationMessage(Constant.RES.getValue(), addResponse, Method.ADD.name(), Constant.RCVD.getValue(), Constant.SERVER.getValue());
							break;
					
					case 2: System.out.print("Enter RFC number: ");
							rfcNumber = sc.next();
							sc.nextLine();
							System.out.print("Enter RFC title: ");
							rfcTitle = sc.nextLine();
							String lookupRequest = createRequest.getLookUpRequest(rfcNumber,rfcTitle);
							clientOutputStream.writeObject(lookupRequest);
							print.communicationMessage(Constant.REQ.getValue(), lookupRequest, Method.LOOKUP.name(), Constant.SENT.getValue(), Constant.SERVER.getValue());
							String lookupResponse = (String)clientInputStream.readObject();
							print.communicationMessage(Constant.RES.getValue(), lookupResponse, Method.LOOKUP.name(), Constant.RCVD.getValue(), Constant.SERVER.getValue());
							break;
					
					case 3: String listRequest = createRequest.getListRequest();
							clientOutputStream.writeObject(listRequest);
							print.communicationMessage(Constant.REQ.getValue(), listRequest, Method.LIST.name(), Constant.SENT.getValue(), Constant.SERVER.getValue());
							String listResponse = (String)clientInputStream.readObject();
							print.communicationMessage(Constant.RES.getValue(), listResponse, Method.LIST.name(), Constant.RCVD.getValue(), Constant.SERVER.getValue());
							break;
					
					case 4: System.out.print("Enter RFC number: ");
							rfcNumber = sc.next();
							String getRequest = createRequest.getDownloadRequest(rfcNumber);
							clientOutputStream.writeObject(getRequest);
							print.communicationMessage(Constant.REQ.getValue(), getRequest, Method.GET.name(), Constant.SENT.getValue(), Constant.SERVER.getValue());
							String getResponse = (String)clientInputStream.readObject();
							print.communicationMessage(Constant.RES.getValue(), getResponse, Method.GET.name(), Constant.RCVD.getValue(), Constant.SERVER.getValue());
							break;
					
					case 5: System.out.print("Are you sure you want to exit? (Y/N): ");
							String confirmation = sc.next();
							if(confirmation.length() == 1 && confirmation.equalsIgnoreCase("Y")) {
								String exitRequest = createRequest.getExitRequest();
								clientOutputStream.writeObject(exitRequest);
								print.communicationMessage(Constant.REQ.getValue(), exitRequest, Method.EXIT.name(), Constant.SENT.getValue(), Constant.SERVER.getValue());
								String exitResponse = (String)clientInputStream.readObject();
								print.communicationMessage(Constant.RES.getValue(), exitResponse, Method.EXIT.name(), Constant.RCVD.getValue(), Constant.SERVER.getValue());
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
