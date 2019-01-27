import java.io.Serializable;

public class EchoDataToServer implements Serializable {
	int action; //0. register 1.login 2.request chatRoom
	//Once Register broker will send back a confirmation
	//Once login; broker will send back the list of chatroom
	int chatRoomNum; //indicate which chatRoomNum it wants
	String userName;
	String passWord;
	
	public EchoDataToServer() {
		
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}

	public int getChatRoomNum() {
		return chatRoomNum;
	}

	public void setChatRoomNum(int chatRoomNum) {
		this.chatRoomNum = chatRoomNum;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
	
	
}
