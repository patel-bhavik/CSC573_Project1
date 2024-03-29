package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.LinkedList;

import constants.Constant;
import constants.FormatCharacter;
import utility.DisplayOnConsole;

public class ServerInitialization {

	public static void main(String[] args) {
		
		final int serverPort = 7734;
		ServerSocket centralIndexServer = null;
		Socket clientSocket = null;
		Hashtable<String,String> hostToIpMap = new Hashtable<String,String>();
		Hashtable<RFC,LinkedList<Peer>> rfcData = new Hashtable<RFC,LinkedList<Peer>>();
		DisplayOnConsole print = new DisplayOnConsole();
		
		try {
			
			// Starting Server
			centralIndexServer = new ServerSocket(serverPort);
			print.serverInitializationMessage(Constant.CI_SERVER.getValue(), InetAddress.getLocalHost().getHostAddress() + FormatCharacter.COL.getValue() + serverPort);
			
			// Accepting Client Connections
			while(true) {
				clientSocket = centralIndexServer.accept();
				CIServer newClient = new CIServer(clientSocket,rfcData,hostToIpMap);
				Thread t = new Thread(newClient);
				t.start();
			}
		}catch(Exception exp) {
			print.errorMessage(Constant.CI_SERVER.getValue(), Constant.INITIALIZATION.getValue(), exp.getMessage());
		}finally {
			try {
				if(clientSocket != null)
					clientSocket.close();
				if(centralIndexServer != null)
					centralIndexServer.close();
			}catch(IOException exp) {
				print.errorMessage(Constant.CI_SERVER.getValue(), Constant.CLEANUP.getValue(), exp.getMessage());
			}
		}
	}

}
