import java.net.InetAddress;

public class ChatRoomInfo {
	InetAddress address;
	int portNum;
	String name;
	
	public ChatRoomInfo(InetAddress address, int portNum,String name) {
		this.address = address;
		this.portNum = portNum;
		this.name = name;
	}

	public InetAddress getAddress() {
		return address;
	}

	public void setAddress(InetAddress address) {
		this.address = address;
	}

	public int getPortNum() {
		return portNum;
	}

	public void setPortNum(int portNum) {
		this.portNum = portNum;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	

	
}
