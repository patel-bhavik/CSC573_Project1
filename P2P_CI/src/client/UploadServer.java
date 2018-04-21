package client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import constants.Constant;
import constants.FormatCharacter;
import utility.DisplayOnConsole;

public class UploadServer implements Runnable {
	
	private int uploadPort;
	private String rfcDirPath;
	
	public UploadServer(int uploadPort, String rfcDirPath) {
		this.uploadPort = uploadPort;
		this.rfcDirPath = rfcDirPath;
	}
	
	public void run() {
		
		ServerSocket uploadServer = null;
		DisplayOnConsole print = new DisplayOnConsole();
		Socket uploadClientSocket = null;
		
		try {
			
			// Starting Upload Server
			uploadServer = new ServerSocket(uploadPort);
			print.serverInitializationMessage(Constant.UPLOAD_SERVER.getValue(), InetAddress.getLocalHost().getHostAddress() + FormatCharacter.COL.getValue() + uploadPort);
			
			// Accepting Client Connections
			while(true) {
				uploadClientSocket = uploadServer.accept();
				UploadServerClient newClient = new UploadServerClient(uploadClientSocket, rfcDirPath);
				Thread t = new Thread(newClient);
				t.start();
			}
		}catch(Exception exp) {
			print.errorMessage(Constant.UPLOAD_SERVER.getValue(), Constant.INITIALIZATION.getValue(), exp.getMessage());
		}finally {
			try {
				if(uploadClientSocket != null)
					uploadClientSocket.close();
				if(uploadServer != null)
					uploadServer.close();
			}catch(IOException exp) {
				print.errorMessage(Constant.CI_SERVER.getValue(), Constant.CLEANUP.getValue(), exp.getMessage());
			}
		}
	}
}
