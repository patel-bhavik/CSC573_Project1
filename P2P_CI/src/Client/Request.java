package Client;

import Constants.Constant;
import Constants.FormatCharacter;
import Constants.Header;
import Constants.Method;

public class Request {
	private String hostName;
	private String uploadPort;
	private String os;
	
	Request(String hostName, String uploadPort, String os){
		this.hostName = hostName;
		this.uploadPort = uploadPort;
		this.os = os;
	}
	
	public String getHeader(String headerName, String headerValue) {
		return headerName +
			   FormatCharacter.COL.getValue() +
			   FormatCharacter.TAB.getValue() +
			   headerValue +
			   FormatCharacter.CR.getValue() +
			   FormatCharacter.LF.getValue();
	}
	
	public String getAddRequest(String rfcNumber, String title) {
		
		String tab = FormatCharacter.TAB.getValue();
		String cr = FormatCharacter.CR.getValue();
		String lf = FormatCharacter.LF.getValue();
		
		return Method.ADD + tab + Constant.RFC.getValue() + tab + rfcNumber + tab + Constant.VERSION.getValue() + cr + lf +
			   getHeader(Header.HOST.getValue(),this.hostName) +
			   getHeader(Header.PORT.getValue(),this.uploadPort) +
			   getHeader(Header.TITLE.getValue(),title) +
			   cr + lf;
	}
	
	public String getLookUpRequest(String rfcNumber, String title) {
		
		String tab = FormatCharacter.TAB.getValue();
		String cr = FormatCharacter.CR.getValue();
		String lf = FormatCharacter.LF.getValue();
		
		return Method.LOOKUP + tab + Constant.RFC.getValue() + tab + rfcNumber + tab + Constant.VERSION.getValue() + cr + lf +
			   getHeader(Header.HOST.getValue(),this.hostName) +
			   getHeader(Header.PORT.getValue(),this.uploadPort) +
			   getHeader(Header.TITLE.getValue(),title) +
			   cr + lf;
	}
	
	public String getListRequest() {
		
		String tab = FormatCharacter.TAB.getValue();
		String cr = FormatCharacter.CR.getValue();
		String lf = FormatCharacter.LF.getValue();
		
		return Method.LIST + tab + Constant.ALL.getValue() + tab + Constant.VERSION.getValue() + cr + lf +
			   getHeader(Header.HOST.getValue(),this.hostName) +
			   getHeader(Header.PORT.getValue(),this.uploadPort) +
			   cr + lf;
	}
	
	public String getDownloadRequest(String rfcNumber) {
		
		String tab = FormatCharacter.TAB.getValue();
		String cr = FormatCharacter.CR.getValue();
		String lf = FormatCharacter.LF.getValue();
		
		return Method.GET + tab + Constant.RFC.getValue() + tab + rfcNumber + tab + Constant.VERSION.getValue() + cr + lf +
			   getHeader(Header.HOST.getValue(),this.hostName) +
			   getHeader(Header.OS.getValue(),this.os) +
			   cr + lf;
	}
	
	public String getExitRequest() {
		
		String tab = FormatCharacter.TAB.getValue();
		String cr = FormatCharacter.CR.getValue();
		String lf = FormatCharacter.LF.getValue();
		
		return Method.EXIT + tab + Constant.VERSION.getValue() + cr + lf +
			   getHeader(Header.HOST.getValue(),this.hostName) +
			   cr + lf;
	}
}
