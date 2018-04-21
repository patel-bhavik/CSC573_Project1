package client;

import java.net.Socket;

public class UploadServerClient implements Runnable {
	
	private Socket clientSocket;
	private String rfcDirPath;
	
	public UploadServerClient(Socket clientSocket, String rfcDirPath){
		this.clientSocket = clientSocket;
		this.rfcDirPath = rfcDirPath;
	}
	
	public void run() {
		System.out.println(clientSocket.toString());
		System.out.println(rfcDirPath);

	}

}
