package Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;
import Constants.FormatCharacter;

public class PeerClients {
	
	public static void cleanUp(ObjectInputStream inStream, ObjectOutputStream outStream, Scanner sc, Socket peerClient, ServerSocket peerServer, Socket rfcClient) {
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
			System.out.println("Error Occured while closing connections");
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
			System.out.println("Connection established with server running at "+serverAddress+":"+serverPort+" successfully.");
			
			// Create Output Streams
			clientOutputStream = new ObjectOutputStream (peerClient.getOutputStream());
			
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
							System.out.print(addRequest);
							break;
					
					case 2: System.out.print("Enter RFC number: ");
							rfcNumber = sc.next();
							sc.nextLine();
							System.out.print("Enter RFC title: ");
							rfcTitle = sc.nextLine();
							String lookupRequest = createRequest.getLookUpRequest(rfcNumber,rfcTitle);
							clientOutputStream.writeObject(lookupRequest);
							System.out.print(lookupRequest);
							break;
					
					case 3: String listRequest = createRequest.getListRequest();
							clientOutputStream.writeObject(listRequest);
							System.out.print(listRequest);
							break;
					
					case 4: System.out.print("Enter RFC number: ");
							rfcNumber = sc.next();
							String getRequest = createRequest.getDownloadRequest(rfcNumber);
							System.out.print(getRequest);
							break;
					
					case 5: System.out.print("Are you sure you want to exit? (Y/N): ");
							String confirmation = sc.next();
							if(confirmation.length() == 1 && confirmation.equalsIgnoreCase("Y")) {
								notExit = false;
								String exitRequest = createRequest.getExitRequest();
								clientOutputStream.writeObject(exitRequest);
								System.out.println(exitRequest);
								cleanUp(clientInputStream,clientOutputStream,sc,peerClient,peerServer,rfcClient);
							}
							break;
					
					default: System.out.println("Invalid Choice. Please try again.");
							 break;
				}
			}while(notExit);
			
			System.out.println("Connection with server running at "+serverAddress+":"+serverPort+" terminated successfully.");
			
		}catch(Exception exp) {
			System.out.println("Error occured while starting a peer client.");
			exp.printStackTrace();
		}finally {
			cleanUp(clientInputStream,clientOutputStream,sc,peerClient,peerServer,rfcClient);
		}
	}

}
