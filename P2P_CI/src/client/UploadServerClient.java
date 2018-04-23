package client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

import communication.protocol.Response;
import constants.Constant;
import constants.FormatCharacter;
import constants.Method;
import constants.StatusCode;
import utility.DisplayOnConsole;

public class UploadServerClient implements Runnable {
	
	private Socket clientSocket;
	private String rfcDirPath;
	private String clientHostName;
	private String os = System.getProperty("os.name");
	
	public UploadServerClient(Socket clientSocket, String rfcDirPath){
		this.clientSocket = clientSocket;
		this.rfcDirPath = rfcDirPath;
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
			print.errorMessage(Constant.UPLOAD_SERVER.getValue(), Constant.CLEANUP.getValue(), exp.getMessage());
		}
	}
	
	public void run() {
		
		// Set IO Stream
		ObjectOutputStream  uploadServerOutputStream = null;
		ObjectInputStream  uploadServerInputStream = null;
		DisplayOnConsole print = new DisplayOnConsole();
					
		try {
			
			// Set IO Stream
			uploadServerOutputStream = new ObjectOutputStream (clientSocket.getOutputStream());
			uploadServerInputStream = new ObjectInputStream (clientSocket.getInputStream());
			
			// Connection Established Message
			System.out.println();
			clientHostName = (String)uploadServerInputStream.readObject();
			
			print.connectionMessage(Constant.ESTABLISH.getValue(), Constant.CLIENT.getValue(), clientHostName);
			
			// Start serving Client
			String method = null;
			boolean isCleanup = false;
			do {
				String request = (String)uploadServerInputStream.readObject();
				Response createResposne = new Response();
				method = request.substring(0, request.indexOf(FormatCharacter.TAB.getValue()));
				HashMap<String,String> resParams=null;
				String response = null;
				String statusCode = null;
				String statusPhrase = null;
				switch (method) {
					case "GET" : print.communicationMessage(Constant.REQ.getValue(), request, Method.GET.name(), Constant.RCVD.getValue(), Constant.CLIENT.getValue() + FormatCharacter.COL.getValue() + FormatCharacter.SP.getValue() + clientHostName);
								 resParams = createResposne.parseDownloadRequest(request);
				 				 statusCode = resParams.get(Constant.STATUS_CODE.getValue());
				 				 statusPhrase = resParams.get(Constant.STATUS_PHRASE.getValue());
				 				 if(statusCode.equals(StatusCode.OK.getCode())) {
				 					String rfcNumber = resParams.get(Constant.RFC_NUM.name());
				 					String filePath = rfcDirPath + FormatCharacter.FSL.getValue() + rfcNumber + Constant.FILE_EXT.getValue();
				 					try {
										File rfcFile = new File(filePath);
										if(rfcFile.exists() && !rfcFile.isDirectory()) {
											long lastModifiedDateTime = rfcFile.lastModified(); 
											long contentLength = rfcFile.length();
											Scanner rf = new Scanner(rfcFile) ;
											StringBuilder fileContents = new StringBuilder();
											String cr = FormatCharacter.CR.getValue();
											String lf = FormatCharacter.LF.getValue();
											while(rf.hasNextLine()){
												fileContents.append(rf.nextLine() + cr + lf);
											}
											rf.close() ;
											response = createResposne.getDownloadResponse(statusCode, statusPhrase, rfcDirPath, rfcNumber, os, lastModifiedDateTime, contentLength, fileContents);
										}else {
											response = createResposne.getDownloadResponseHeader(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getPhrase(), os);
										}
									} catch (FileNotFoundException exp) {
										print.errorMessage(Constant.UPLOAD_SERVER.getValue(), Constant.FNF.getValue(), exp.getMessage());
										response = createResposne.getDownloadResponseHeader(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getPhrase(), os);
									}				 						
				 				 }else {
				 					 response = createResposne.getDownloadResponseHeader(statusCode, statusPhrase, os);
								 }
				 				 uploadServerOutputStream.writeObject(response);
								 print.communicationMessage(Constant.RES.getValue(), response, Method.GET.name(), Constant.SENT.getValue(), Constant.CLIENT.getValue() + FormatCharacter.COL.getValue() + FormatCharacter.SP.getValue() + clientHostName);
	 							 break;
					
					case "EXIT" : print.communicationMessage(Constant.REQ.getValue(), request, Method.EXIT.name(), Constant.RCVD.getValue(), Constant.CLIENT.getValue() + FormatCharacter.COL.getValue() + FormatCharacter.SP.getValue() + clientHostName);
								  resParams = createResposne.parseExitRequest(request);
								  statusCode = resParams.get(Constant.STATUS_CODE.getValue());
								  statusPhrase = resParams.get(Constant.STATUS_PHRASE.getValue());
								  response = createResposne.getResponseHeader(statusCode, statusPhrase);
								  if(statusCode.equals(StatusCode.OK.getCode())) {
									  isCleanup = true;
								  }
								  uploadServerOutputStream.writeObject(response);
								  print.communicationMessage(Constant.RES.getValue(), response, Method.EXIT.name(), Constant.SENT.getValue(), Constant.CLIENT.getValue() + FormatCharacter.COL.getValue() + FormatCharacter.SP.getValue() + clientHostName);
								  if(isCleanup) {
									  cleanUp(uploadServerInputStream, uploadServerOutputStream);  
								  }
								  break;
					
					default : print.communicationMessage(Constant.REQ.getValue(), request, Method.INVALID.name(), Constant.RCVD.getValue(), Constant.CLIENT.getValue() + FormatCharacter.COL.getValue() + FormatCharacter.SP.getValue() + clientHostName);
							  response = createResposne.getResponseHeader(StatusCode.BAD_REQUEST.getCode(), StatusCode.BAD_REQUEST.getPhrase());
							  uploadServerOutputStream.writeObject(response);
							  print.communicationMessage(Constant.RES.getValue(), response, Method.INVALID.name(), Constant.SENT.getValue(), Constant.CLIENT.getValue() + FormatCharacter.COL.getValue() + FormatCharacter.SP.getValue() + clientHostName);
				}
			}while(!isCleanup);
			
			// Connection Termination Message
			print.connectionMessage(Constant.TERMINATE.getValue(), Constant.CLIENT.getValue(), clientHostName);
						
		}catch(Exception exp) {
			print.errorMessage(Constant.UPLOAD_SERVER.getValue(), Constant.COMMUNICATION.getValue(), exp.getMessage());
		}finally {
			cleanUp(uploadServerInputStream,uploadServerOutputStream);
		}
	}

}
