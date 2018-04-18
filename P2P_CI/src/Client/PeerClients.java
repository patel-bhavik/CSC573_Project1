package Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;
import Constants.*;

public class PeerClients {
	
	private static String HOST_NAME;
	private static String IP_ADDRESS;
	private static String UPLOAD_PORT;
	private static String OS = System.getProperty("os.name") ;
	
	public static String getHeader(String headerName, String headerValue) {
		return headerName +
			   FormatCharacter.COL.getValue() +
			   FormatCharacter.TAB.getValue() +
			   headerValue +
			   FormatCharacter.CR.getValue() +
			   FormatCharacter.LF.getValue();
	}
	
	public static String generateAddRequest(String rfcNumber, String title) {
		
		String tab = FormatCharacter.TAB.getValue();
		String cr = FormatCharacter.CR.getValue();
		String lf = FormatCharacter.LF.getValue();
		
		return Method.ADD + tab + Constant.RFC.getValue() + tab + rfcNumber + tab + Constant.VERSION.getValue() + cr + lf +
			   getHeader(Header.HOST.getValue(),HOST_NAME) +
			   getHeader(Header.PORT.getValue(),UPLOAD_PORT) +
			   getHeader(Header.TITLE.getValue(),title) +
			   cr + lf;
	}
	
	public static String generateLookUpRequest(String rfcNumber, String title) {
		
		String tab = FormatCharacter.TAB.getValue();
		String cr = FormatCharacter.CR.getValue();
		String lf = FormatCharacter.LF.getValue();
		
		return Method.LOOKUP + tab + Constant.RFC.getValue() + tab + rfcNumber + tab + Constant.VERSION.getValue() + cr + lf +
			   getHeader(Header.HOST.getValue(),HOST_NAME) +
			   getHeader(Header.PORT.getValue(),UPLOAD_PORT) +
			   getHeader(Header.TITLE.getValue(),title) +
			   cr + lf;
	}
	
	public static String generateListRequest() {
		
		String tab = FormatCharacter.TAB.getValue();
		String cr = FormatCharacter.CR.getValue();
		String lf = FormatCharacter.LF.getValue();
		
		return Method.LIST + tab + Constant.ALL.getValue() + tab + Constant.VERSION.getValue() + cr + lf +
			   getHeader(Header.HOST.getValue(),HOST_NAME) +
			   getHeader(Header.PORT.getValue(),UPLOAD_PORT) +
			   cr + lf;
	}
	
	public static String generateDownloadRequest(String rfcNumber) {
		
		String tab = FormatCharacter.TAB.getValue();
		String cr = FormatCharacter.CR.getValue();
		String lf = FormatCharacter.LF.getValue();
		
		return Method.GET + tab + Constant.RFC.getValue() + tab + rfcNumber + tab + Constant.VERSION.getValue() + cr + lf +
			   getHeader(Header.HOST.getValue(),HOST_NAME) +
			   getHeader(Header.OS.getValue(),OS) +
			   cr + lf;
	}
	
	public static String generateExitRequest() {
		
		String tab = FormatCharacter.TAB.getValue();
		String cr = FormatCharacter.CR.getValue();
		String lf = FormatCharacter.LF.getValue();
		
		return Method.EXIT + tab + Constant.VERSION.getValue() + cr + lf +
			   getHeader(Header.HOST.getValue(),HOST_NAME) +
			   cr + lf;
	}
	
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
		// TODO Auto-generated method stub
		Socket peerClient = null;
		Socket rfcClient = null;
		ServerSocket peerServer = null;
		Scanner sc = new Scanner(System.in);
		ObjectInputStream clientInputStream = null;
		ObjectOutputStream clientOutputStream = null;
		
		try {
			
			Random randNumber = new Random();
			int peerServerPort = randNumber.nextInt(15000)+50000;
			
			// Start Upload Process
			peerServer = new ServerSocket(peerServerPort);
			
			// Set Host Details
			HOST_NAME = InetAddress.getLocalHost().getHostName() + FormatCharacter.US.getValue() + peerServerPort;
			IP_ADDRESS = InetAddress.getLocalHost().getHostAddress();
			UPLOAD_PORT = Integer.toString(peerServerPort);
			System.out.println();
			
			// Get Server Details
			System.out.print("Enter IP Address of server to connect: ");
			String serverAddress = sc.next();
			System.out.print("Enter Port of server to connect: ");
			int serverPort = sc.nextInt();
			
			// Connect with server
			peerClient = new Socket(serverAddress,serverPort);
			System.out.println("Connection established with server running at "+serverAddress+":"+serverPort+" successfully.");
			
			// Set Input Output Streams
			clientOutputStream = new ObjectOutputStream (peerClient.getOutputStream());
			
			// Send host details
			clientOutputStream.writeObject(HOST_NAME);
			clientOutputStream.writeObject(IP_ADDRESS);
			
			// Start Server Communication
			int choice;
			boolean notExit = true;
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
							String addRequest = generateAddRequest(rfcNumber,rfcTitle);
						    clientOutputStream.writeObject(addRequest);
							System.out.print(addRequest);
							break;
					
					case 2: System.out.print("Enter RFC number: ");
							rfcNumber = sc.next();
							sc.nextLine();
							System.out.print("Enter RFC title: ");
							rfcTitle = sc.nextLine();
							String lookupRequest = generateLookUpRequest(rfcNumber,rfcTitle);
							clientOutputStream.writeObject(lookupRequest);
							System.out.print(lookupRequest);
							break;
					
					case 3: String listRequest = generateListRequest();
							clientOutputStream.writeObject(listRequest);
							System.out.print(listRequest);
							break;
					
					case 4: System.out.print("Enter RFC number: ");
							rfcNumber = sc.next();
							String getRequest = generateDownloadRequest(rfcNumber);
							clientOutputStream.writeObject(getRequest);
							System.out.print(getRequest);
							break;
					
					case 5: System.out.print("Are you sure you want to exit? (Y/N): ");
							String confirmation = sc.next();
							if(confirmation.length() == 1 && confirmation.equalsIgnoreCase("Y")) {
								notExit = false;
								String exitRequest = generateExitRequest();
								clientOutputStream.writeObject(exitRequest);
								System.out.println(exitRequest);
								cleanUp(clientInputStream,clientOutputStream,sc,peerClient,peerServer,rfcClient);
							}
							break;
					
					default: System.out.println("Invalid Choice. Please try again.");
							 break;
				}
			}while(notExit);
		}catch(Exception exp) {
			System.out.println("Error occured while starting a peer client.");
			exp.printStackTrace();
		}finally {
			try {
				if(clientInputStream != null)
					clientInputStream.close();
				if(clientInputStream != null)
					clientInputStream.close();
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
	}

}
