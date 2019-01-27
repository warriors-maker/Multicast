import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class ClientThread implements Runnable{
	boolean login = false;
	List<String> roomChoices = new ArrayList<>();
	
	//Helper function to print or checkValid Input
	private void printRoomChoices(List<String> roomChoices) {
		System.out.println("Here are the ChatRoom you can go to");
		for (int i = 0; i < roomChoices.size(); i++) {
			System.out.println(i + ":" + roomChoices.get(i));
		}
	}
	
	private String readFromInput() {
		BufferedReader fromKeyboard = new BufferedReader(new InputStreamReader(System.in)); // from keyboard
		try {
			return fromKeyboard.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private boolean checkValidStr(String input) {
		if (input == null || input.length() == 0) {
			System.out.println("Cannot be Empty");
			return false;
		} else if (input.length() < 6) {
			System.out.println("Length must be at least 6");
			return false;
		}
		return true;
	}
	
	private boolean checkValidNum(String input, int maxLimit, int minLimit) {
		try {
			int num = Integer.parseInt(input);
			if (num >= minLimit && num <= maxLimit) {
				return true;
			}
		} catch (NumberFormatException e) {
			return false;
		}
		return false;
	}
	
	//can use it for actionChoice
	//can use it for choosing room
	
	private int readChoice(int maxLimit, int minLimit) {
		String choice = readFromInput();
		while (choice == null || ! checkValidNum(choice, maxLimit, minLimit)) {
			System.out.println("Invalid number, please rechoose Another");
			choice = readFromInput();
		}
		return Integer.parseInt(choice);
	}
	
	private void printActionChoice() {
		System.out.println("Input Number to indicate which actions you want to take:");
		System.out.println("0:Register an Acoount");
		System.out.println("1:Login to an Account");
		System.out.println("2:Request the ChatRoom Info you want to talk with");
	}
	
	
	
	
	
	//This part is to handle Send 
	private void handleSendCoordination(EchoDataToServer dataToServer) {
		System.out.println("------------------------------------------------------------");
		printActionChoice();
		//Middle Logic
		int choice = readChoice(2,0);
		
		if (choice == 0) {
			handleRegisterOrLogin(dataToServer,choice);
		}
		
		if (choice == 1) {
			if (!login) {
				handleRegisterOrLogin(dataToServer,choice);
			} else {
				dataToServer.setAction(1);
				System.out.println("You have Log in Already");
				//=> we know that dataToServer for userName and PassWord is Empty;
			}
		}
		
		if (choice == 2) {
			handleRequest(dataToServer);
		}
	}
	
	private void handleRegisterOrLogin(EchoDataToServer dataToServer, int choice) {
		System.out.println("Input Your UserName:");
		String userName = readFromInput();
		while (!checkValidStr(userName)) {
			System.out.println("Input Your UserName:");
			userName = readFromInput();
		}
		
		System.out.println("Input Your PassWord:");
		String passWord = readFromInput();
		while (!checkValidStr(passWord)) {
			System.out.println("Input Your PassWord:");
			passWord = readFromInput();
		}
		
		dataToServer.setAction(choice);
		dataToServer.setUserName(userName);
		dataToServer.setPassWord(passWord);
	}
	
	private void handleRequest(EchoDataToServer dataToServer) {
		dataToServer.setAction(2);
		printRoomChoices(roomChoices);
		if (roomChoices.size() == 0) {
			return;
		}
		int roomChoice = readChoice(roomChoices.size() - 1, 0);
		dataToServer.setChatRoomNum(roomChoice);
	}
	
	
	private String inputName() {
		System.out.println("Input the name you want to use in the chatRoom");
		String name = readFromInput();
		while (name.length() == 0) {
			System.out.println("Name cannot be empty");
			name = readFromInput();
		}
		return name;
	}
	
	//This part is to handleReceive
	private void handleReceiveCoordination(EchoDataToClient dataToClient) {
		int action = dataToClient.getAction();
		if (action == 0) {
			//register
			String message = dataToClient.getMessage();
			System.out.println(message);
		} else if (action == 1) {
			//login
			String message = dataToClient.getMessage();
			System.out.println(message);
			if (dataToClient.getOptions() != null) {
				roomChoices = dataToClient.getOptions();
				login = true;
			}
		} else if (action == 2) {
			//start the chatRoom;
			InetAddress address = dataToClient.getAddress();
			int portNum = dataToClient.getPortNum();
			String sAddress = address.toString();
			int index = sAddress.indexOf('/');
			sAddress = sAddress.substring(index + 1);
			String[] args = new String[3];
			
			args[0] = sAddress;
			args[1] = portNum + "";
			args[2] = inputName();
			setUpRoom(args);
		}
	}
	
	
	private void setUpRoom(String[] args) {
		ppmc_new app = new ppmc_new("Peer-to-Peer Multicast Chat for csci3363");
		System.setProperty("java.net.preferIPv4Stack", "true"); //important
		app.connect(args);
		app.setVisible(true);
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		InetAddress serverAddress = null;
		try {
			serverAddress = InetAddress.getByName("127.0.0.1");
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} // Server address
		int servPort = 8000; // get port number
		
		ClientNetworkUtil nu = new ClientNetworkUtil(serverAddress, servPort); //create a connection to a server
		
		try {
			nu.createClientSocket();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		while (true) {
			EchoDataToServer dataToServer = new EchoDataToServer();
			EchoDataToClient dataToClient = null;
			
			//Sending Prepartation()
			handleSendCoordination(dataToServer);
			
			//inValid in Three Cases: 
			//1.Server dies 2. Client already login but tries to login again 3. Request the roomChat when not login
			boolean valid = true; 
			
			//Sending
			
			//Have not get the chatRoom Needed
			if (dataToServer.getAction() == 2 && !login) {
				System.out.println("You have no access to the table of the chatRoom, Please Login First");
				valid = false;
			} 
				//have login already, no need to send
			else if(dataToServer.getAction() == 1 && login) {
				valid = false;
			}
			else {
				try {
					nu.sendSerializedEchoData(dataToServer);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// ToDO Auto-generated catch block
					e.printStackTrace();
					valid = false;
					System.out.println("The Server seems not responding, Come Back Later.");
				}
			}
			
			
			//check if the server has responded or not;
			//Only receive if the server has responded else go to the top
			if (valid) {
				//getting data
				try {
					dataToClient = nu.getSerializedEchoData();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (dataToClient != null) {
					handleReceiveCoordination(dataToClient);
				} else {
					System.out.println("The Server is not Responding, try again later");
				}
			}
			
		}
	}

}
