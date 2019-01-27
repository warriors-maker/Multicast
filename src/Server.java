import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {
	private static Map<String, String> userInfo = new HashMap<>();
	private static Map<Integer, ChatRoomInfo> chatRoomInfo = new HashMap<>(); //Integer is the choice, 
	static List<String> options;
	
	public Server() {
		
	}
	
	private static void initServer() {
		options = new ArrayList<>();
		try {
			chatRoomInfo.put(0, new ChatRoomInfo(InetAddress.getByName("233.1.2.3"), 5555, "BC Tech"));
			chatRoomInfo.put(1, new ChatRoomInfo(InetAddress.getByName("233.1.2.4"), 5556, "NBA Today"));
			chatRoomInfo.put(2, new ChatRoomInfo(InetAddress.getByName("233.1.2.5"), 5557, "Distributed System Conference"));
			
			for (int i = 0; i < chatRoomInfo.size(); i++) {
				options.add(chatRoomInfo.get(i).getName());
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void handleCoordination(int action, EchoDataToClient dataToClient, EchoDataToServer dataToServer) {
		if (action == 0) {
			handleRegister(dataToClient, dataToServer);
		}
		
		if (action == 1) {
			handleLogin(dataToClient, dataToServer);
		}
		
		if (action == 2) {
			handleRequest(dataToClient, dataToServer);
		}
	}
	
	private static void handleRegister(EchoDataToClient dataToClient, EchoDataToServer dataToServer) {
		dataToClient.setAction(0);
		String userName = dataToServer.getUserName();
		String passWord = dataToServer.getPassWord();
		if (userInfo.containsKey(userName)) {
			dataToClient.setMessage("Account has already created");
			System.out.println(userName + " fail to register.");
		} else {
			userInfo.put(userName, passWord);
			dataToClient.setMessage("Successfully Register");
			System.out.println(userName + " successfully register.");
		}
	}
	
	private static void handleLogin(EchoDataToClient dataToClient, EchoDataToServer dataToServer) {
		dataToClient.setAction(1);
		String userName = dataToServer.getUserName();
		String passWord = dataToServer.getPassWord();
		
		if (userInfo.containsKey(userName) && userInfo.get(userName).equals(passWord)) {
			dataToClient.setOptions(options);
			dataToClient.setMessage("Successfully Login");
			System.out.println(userName + " successfully login.");
		} else {
			if (!userInfo.containsKey(userName)) {
				dataToClient.setMessage("UserName does not exist");
			} else if (! userInfo.get(userName).equals(passWord)) {
				dataToClient.setMessage("PassWord does not match");
			}
			System.out.println(userName + " fail to login.");
		}
	}
	
	private static void handleRequest(EchoDataToClient dataToClient, EchoDataToServer dataToServer) {
		dataToClient.setAction(2);
		//From Client information
		int roomChoice = dataToServer.getChatRoomNum();
		
		//Set up the information for the clients
		ChatRoomInfo info = chatRoomInfo.get(roomChoice);
		InetAddress address = info.getAddress();
		int portNum = info.getPortNum();
		dataToClient.setAddress(address);
		dataToClient.setPortNum(portNum);
		System.out.println("Send: " + address + ":" + portNum);
	}
	
	public static void main (String[] args) {
		int servPort = 8000;
		initServer();
		
		ServerNetworkUtil nu = new ServerNetworkUtil(servPort);
		
		try {
			nu.createServerSocket();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			nu.setReceivePacket();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("I am an Broker Server and I am waiting on port # " + servPort);
		
		//The server is always listening unless I deliberately shut it down
		while (true) {
			EchoDataToClient dataToClient = new EchoDataToClient();
			EchoDataToServer dataToServer = null;
			
			//Get Data from Client;
			try {
				dataToServer = nu.getSerializedEchoData();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//Middle logic
			int action = dataToServer.getAction();
			handleCoordination(action, dataToClient, dataToServer);
			
			
			//Send Data to Client;
			try {
				nu.sendSerializedEchoData(dataToClient);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
}
