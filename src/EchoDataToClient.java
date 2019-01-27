import java.io.Serializable;
import java.net.InetAddress;
import java.util.List;

public class EchoDataToClient implements Serializable {
	String message;
	int action; //0.respond to Register //1.respond to login in //2.respond to query
	
	//are two Variable that are needed for action2
	InetAddress address;
	int portNum;
	
	List<String> options;
	
	
	public EchoDataToClient() {
		
	}

	
	public List<String> getOptions() {
		return options;
	}


	public void setOptions(List<String> options) {
		this.options = options;
	}


	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
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
	
	
}
