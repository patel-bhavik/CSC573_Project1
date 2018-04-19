package CommunicationProtocol;

import java.util.HashMap;

import Constants.Constant;
import Constants.FormatCharacter;
import Constants.Header;
import Constants.Method;
import Constants.StatusCode;

public class Response {
	
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
				headers.put(headerName, headerParams[1].trim());
			}
		}
		return true;
	}
	
	public HashMap<String,String> parseAddRequest(String addRequest){
		
		HashMap<String,String> resParams = new HashMap<String,String>();
		String lineSeparator = FormatCharacter.CR.getValue() + FormatCharacter.LF.getValue();
		String reqLines[] = addRequest.split(lineSeparator);
		final int expectedLines = 4;
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
		final int expectedLines = 4;
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
		final int expectedLines = 3;
		HashMap<String,String> headers = new HashMap<String,String>();
		String reqParams[] = reqLines[0].split(FormatCharacter.TAB.getValue());
		
		// Validate Number of Lines, Headers and Version
		if(!noOfLinesValidated(reqLines, expectedLines, resParams) ||
		   !versionValidated(reqParams[2], resParams) ||
		   !headersValidated(expectedLines, reqLines, headers, resParams)){
			return resParams;
		}
		
		// Validate Request Parameters
		if(Method.contains(reqParams[0]) &&
		   reqParams[0].equals(Method.LIST.name()) &&
		   reqParams[1].equals(Constant.ALL.getValue())){
			addOkStatusCode(resParams);
			addHeaders(headers, resParams);
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
		final int expectedLines = 3;
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
		final int expectedLines = 2;
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
}
