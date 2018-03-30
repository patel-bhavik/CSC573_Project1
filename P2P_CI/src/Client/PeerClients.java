package Client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

public class PeerClients {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Socket peerClient;
		ServerSocket peerServer;
		Scanner sc = new Scanner(System.in);
		ObjectOutputStream  clientOutputStream;
		
		try {
			
			Random randNumber = new Random();
			int peerServerPort = randNumber.nextInt(15000)+50000;
			// Start Upload Process
			peerServer = new ServerSocket(peerServerPort);
			
			// Get Server Details
			System.out.print("Enter IP Address of server to connect: ");
			String serverAddress = InetAddress.getByName(sc.next()).getHostName();
			System.out.print("Enter Port of server to connect: ");
			int serverPort = sc.nextInt();
			
			// Connect with server
			peerClient = new Socket(serverAddress,serverPort);
			System.out.println("Connection established with server running at "+serverAddress+":"+serverPort+" successfully.");
			
			// Send Client Info
			clientOutputStream = new ObjectOutputStream (peerClient.getOutputStream());
			clientOutputStream.writeInt(peerServerPort);
			clientOutputStream.writeObject("Client_"+peerServerPort);
			
			// Send Requests
			while(true) {
			
			}
		}catch(Exception exp) {
			System.out.println("Error occured while starting a peer client.");
			exp.printStackTrace();
		}
	}

}
