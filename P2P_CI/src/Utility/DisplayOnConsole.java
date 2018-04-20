package Utility;

import Constants.Constant;
import Constants.FormatCharacter;

public class DisplayOnConsole {
	
	private final String space = FormatCharacter.SP.getValue();
	private final String fullStop = FormatCharacter.FS.getValue();
	
	public void displayMessage(String messageType, String message, String method, String reqState , String source) {
		String prepostition;
		if(reqState.equals(Constant.RCVD.getValue()))
			prepostition = Constant.FROM.getValue();
		else
			prepostition = Constant.TO.getValue();
		System.out.println("-----------------------------------------------------------------------");
		System.out.println(messageType + space + "for" + space + method + space + reqState + space + prepostition + space + source + fullStop);
		System.out.println("-----------------------------------------------------------------------");
		System.out.print(message);
		System.out.println("-----------------------------------------------------------------------");
	}
	
	public void displayConnectionMessage(String connectionType, String target, String targetAddress) {
		String targetName;
		if(target.equals(Constant.SERVER.getValue()))
			targetName = Constant.SRVR_TARGET_NAME.getValue();
		else
			targetName = Constant.CLIENT.getValue();
		System.out.println("Connection" + space + connectionType + space + "with" + space + targetName + space + targetAddress + space + "successfully" + fullStop);
	}
	
	public void serverInitializationMessage(String serverName, String serverAddress) {
		System.out.println(serverName + space + "started" + space + "on" + space + serverAddress + space + "successfully" + fullStop);
	}
}
