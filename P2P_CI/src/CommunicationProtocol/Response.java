package CommunicationProtocol;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;

import Constants.Constant;
import Constants.FormatCharacter;
import Constants.Header;
import Constants.Method;
import Constants.NoOfLines;
import Constants.StatusCode;
import Server.Peer;
import Server.RFC;

public class Response {
	
	private final String tab = FormatCharacter.TAB.getValue();
	private final String cr = FormatCharacter.CR.getValue();
	private final String lf = FormatCharacter.LF.getValue();
	
	public boolean isNumeric(String str) {  
	  try {  
	    Integer.parseInt(str);  
	  }catch(NumberFormatException exp) {  
	    return false;  
	  }  
	  return true;
	}
	
	public void addBadRequestStatusCode(HashMap<String,String> resParams) {
		resParams.put(Constant.STATUS_CODE.getValue(), StatusCode.BAD_REQUEST.getCode());
		resParams.put(Constant.STATUS_PHRASE.getValue(), StatusCode.BAD_REQUEST.getPhrase());
	}
	
	public void addVersionNotSupportedStatusCode(HashMap<String,String> resParams) {
		resParams.put(Constant.STATUS_CODE.getValue(), StatusCode.VERSION_NOT_SUPPORTED.getCode());
		resParams.put(Constant.STATUS_PHRASE.getValue(), StatusCode.VERSION_NOT_SUPPORTED.getPhrase());
	}
	
	public void addOkStatusCode(HashMap<String,String> resParams) {
		resParams.put(Constant.STATUS_CODE.getValue(), StatusCode.OK.getCode());
		resParams.put(Constant.STATUS_PHRASE.getValue(), StatusCode.OK.getPhrase());
	}
	
	public void addHeaders(HashMap<String,String> headers, HashMap<String,String> resParams) {
		headers.forEach((key,value) -> {
			resParams.put(key,value);
		});
	}
	
	public boolean noOfLinesValidated(String reqLines[], int expextedLines, HashMap<String,String> resParams) {
		if(reqLines.length != expextedLines) {
			addBadRequestStatusCode(resParams);
			return false;
		}
		return true;
	}
	
	public boolean versionValidated(String versionParam,HashMap<String,String> resParams) {
		if(!versionParam.equals(Constant.VERSION.getValue())) {
			addVersionNotSupportedStatusCode(resParams);
			return false;
		}
		return true;
	}
	
	public boolean headersValidated(int endIndex, String[] reqLines, HashMap<String,String> headers, HashMap<String,String> resParams) {
		for(int i = 1; i < endIndex; i++) {
			String headerParams[] = reqLines[i].split(":");
			String headerName = headerParams[0].trim();
			if(!Header.contains(headerName)) {
				addBadRequestStatusCode(resParams);
				return false;
			}else {
				if(headers != null)
					headers.put(headerName, headerParams[1].trim());
			}
		}
		return true;
	}
	
	public HashMap<String,String> parseAddRequest(String addRequest){
		
		HashMap<String,String> resParams = new HashMap<String,String>();
		String lineSeparator = FormatCharacter.CR.getValue() + FormatCharacter.LF.getValue();
		String reqLines[] = addRequest.split(lineSeparator);
		final int expectedLines = NoOfLines.ADD.getLines();
		HashMap<String,String> headers = new HashMap<String,String>();
		String reqParams[] = reqLines[0].split(FormatCharacter.TAB.getValue());
		
		// Validate Number of Lines, Headers and Version
		if(!noOfLinesValidated(reqLines, expectedLines, resParams) ||
		   !versionValidated(reqParams[3], resParams) ||
		   !headersValidated(expectedLines, reqLines, headers, resParams)){
			return resParams;
		}
		
		// Validate Request Parameters
		if(Method.contains(reqParams[0]) &&
		   reqParams[0].equals(Method.ADD.name()) &&
		   reqParams[1].equals(Constant.RFC.getValue()) &&
		   isNumeric(reqParams[2])){
			addOkStatusCode(resParams);
			resParams.put(Constant.RFC_NUM.getValue(), reqParams[2]);
			addHeaders(headers, resParams);
		}else {
			addBadRequestStatusCode(resParams);
			return resParams;
		}
		
		return resParams;
	}
	
	public HashMap<String,String> parseLookupRequest(String lookupRequest){
		
		HashMap<String,String> resParams = new HashMap<String,String>();
		String lineSeparator = FormatCharacter.CR.getValue() + FormatCharacter.LF.getValue();
		String reqLines[] = lookupRequest.split(lineSeparator);
		final int expectedLines = NoOfLines.LOOKUP.getLines();
		HashMap<String,String> headers = new HashMap<String,String>();
		String reqParams[] = reqLines[0].split(FormatCharacter.TAB.getValue());
		
		// Validate Number of Lines, Headers and Version
		if(!noOfLinesValidated(reqLines, expectedLines, resParams) ||
		   !versionValidated(reqParams[3], resParams) ||
		   !headersValidated(expectedLines, reqLines, headers, resParams)){
			return resParams;
		}
		
		// Validate Request Parameters
		if(Method.contains(reqParams[0]) &&
		   reqParams[0].equals(Method.LOOKUP.name()) &&
		   reqParams[1].equals(Constant.RFC.getValue()) &&
		   isNumeric(reqParams[2])){
			addOkStatusCode(resParams);
			resParams.put(Constant.RFC_NUM.getValue(), reqParams[2]);
			addHeaders(headers, resParams);
		}else {
			addBadRequestStatusCode(resParams);
			return resParams;
		}
		
		return resParams;
	}

	public HashMap<String,String> parseListRequest(String listRequest){
		
		HashMap<String,String> resParams = new HashMap<String,String>();
		String lineSeparator = FormatCharacter.CR.getValue() + FormatCharacter.LF.getValue();
		String reqLines[] = listRequest.split(lineSeparator);
		final int expectedLines = NoOfLines.LIST.getLines();
		String reqParams[] = reqLines[0].split(FormatCharacter.TAB.getValue());
		
		// Validate Number of Lines, Headers and Version
		if(!noOfLinesValidated(reqLines, expectedLines, resParams) ||
		   !versionValidated(reqParams[2], resParams) ||
		   !headersValidated(expectedLines, reqLines, null, resParams)){
			return resParams;
		}
		
		// Validate Request Parameters
		if(Method.contains(reqParams[0]) &&
		   reqParams[0].equals(Method.LIST.name()) &&
		   reqParams[1].equals(Constant.ALL.getValue())){
			addOkStatusCode(resParams);
		}else {
			addBadRequestStatusCode(resParams);
			return resParams;
		}
		
		return resParams;
	}
	
	public HashMap<String,String> parseDownloadRequest(String downloadRequest){
		
		HashMap<String,String> resParams = new HashMap<String,String>();
		String lineSeparator = FormatCharacter.CR.getValue() + FormatCharacter.LF.getValue();
		String reqLines[] = downloadRequest.split(lineSeparator);
		final int expectedLines = NoOfLines.GET.getLines();
		HashMap<String,String> headers = new HashMap<String,String>();
		String reqParams[] = reqLines[0].split(FormatCharacter.TAB.getValue());
		
		// Validate Number of Lines, Headers and Version
		if(!noOfLinesValidated(reqLines, expectedLines, resParams) ||
		   !versionValidated(reqParams[3], resParams) ||
		   !headersValidated(expectedLines, reqLines, headers, resParams)){
			return resParams;
		}
		
		// Validate Request Parameters
		if(Method.contains(reqParams[0]) &&
		   reqParams[0].equals(Method.GET.name()) &&
		   reqParams[1].equals(Constant.RFC.getValue()) &&
		   isNumeric(reqParams[2])){
			addOkStatusCode(resParams);
			addHeaders(headers, resParams);
		}else {
			addBadRequestStatusCode(resParams);
			return resParams;
		}
		
		return resParams;
	}
	
	public HashMap<String,String> parseExitRequest(String exitRequest){
		
		HashMap<String,String> resParams = new HashMap<String,String>();
		String lineSeparator = FormatCharacter.CR.getValue() + FormatCharacter.LF.getValue();
		String reqLines[] = exitRequest.split(lineSeparator);
		final int expectedLines = NoOfLines.EXIT.getLines();
		HashMap<String,String> headers = new HashMap<String,String>();
		String reqParams[] = reqLines[0].split(FormatCharacter.TAB.getValue());
		
		// Validate Number of Lines, Headers and Version
		if(!noOfLinesValidated(reqLines, expectedLines, resParams) ||
		   !versionValidated(reqParams[1], resParams) ||
		   !headersValidated(expectedLines, reqLines, headers, resParams)){
			return resParams;
		}
		
		// Validate Request Parameters
		if(Method.contains(reqParams[0]) &&
		   reqParams[0].equals(Method.EXIT.name())){
			addOkStatusCode(resParams);
			addHeaders(headers, resParams);
		}else {
			addBadRequestStatusCode(resParams);
			return resParams;
		}
		
		return resParams;
	}
	
	public String getResponseHeader(String statusCode, String statusPhrase) {
		
		return Constant.VERSION.getValue() + tab + statusCode + tab + statusPhrase + cr + lf +
			   cr + lf;
	}
	
	public String getAddResponse(String statusCode, String statusPhrase, String rfcNumber, String rfcTitle, String hostName, String port) {
		
		return getResponseHeader(statusCode, statusPhrase) +
			   Constant.RFC.getValue() + tab + rfcNumber + tab + rfcTitle + tab + hostName + tab + port + cr + lf +
			   cr + lf;
	}
	
	public String getLookupResponse(String statusCode, String statusPhrase, String rfcNumber, String rfcTitle, LinkedList<Peer> peerList) {
		
		StringBuilder response = new StringBuilder(getResponseHeader(statusCode, statusPhrase));
		peerList.forEach((peer) -> {
			response.append(Constant.RFC.getValue() + tab + rfcNumber + tab + rfcTitle + tab + peer.getHostName() + tab + peer.getIpAddress() + tab + peer.getPort() + cr + lf);
		});
		return response.append(cr + lf).toString();
	}
	
	public String getListResponse(String statusCode, String statusPhrase, Hashtable<RFC,LinkedList<Peer>> rfcData) {
		
		StringBuilder response = new StringBuilder(getResponseHeader(statusCode, statusPhrase));
		
		rfcData.forEach((rfc,peerList) -> {
			String rfcNumber = Integer.toString(rfc.getRfcNumber());
			String rfcTitle = rfc.getRfcTitle();
			peerList.forEach((peer) -> {
				response.append(Constant.RFC.getValue() + tab + rfcNumber + tab + rfcTitle + tab + peer.getHostName() + tab + peer.getIpAddress() + tab + peer.getPort() + cr + lf);
			});
		});
		return response.append(cr + lf).toString();
	}
}
