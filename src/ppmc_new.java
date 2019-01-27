import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ppmc_new extends Frame{

	private TextArea ta = new TextArea("", 20, 80, TextArea.SCROLLBARS_VERTICAL_ONLY);
	private Label l = new Label("ENTER:");
	private Label spacer1 = new Label("MULTICAST ROOM OF HORROR");
	private Label spacer2 = new Label("");
	private TextField tf = new TextField("", 60);
	private Button b = new Button("SUBMIT");

	private tfkListener tkfl;
	private bListener bl;

	private MulticastSocket socket;
    private DatagramPacket outgoing, incoming;
	private InetAddress group;
	private	int port;
	private String screenName;
	public boolean chatting = true;

	ppmc_new(String title){

		super(title);
		setSize(640, 480);
		setBackground(Color.gray);
		addWindowListener(new WL());

		ta.addFocusListener(new tafListener());
		tf.addKeyListener(tkfl = new tfkListener());
		b.addActionListener(bl = new bListener());
		
		
		ta.setEditable(false);
		tf.setEditable(true);

		ta.setBackground(Color.darkGray);
		ta.setForeground(Color.white);
		spacer1.setForeground(Color.white);
		l.setForeground(Color.white);

		GridBagLayout gbl= new GridBagLayout();
		setLayout(gbl);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = GridBagConstraints.REMAINDER; //end row
		gbl.setConstraints(ta, gbc);

		gbl.setConstraints(spacer1, gbc);
		add(spacer1);
		add(ta);
		gbl.setConstraints(spacer2, gbc);
		add(spacer2);
		add(l);
		add(tf);
		add(b);		
		
	} 
	
	public synchronized boolean getChatting() {
		return chatting;
	}
	
	public synchronized void endChat() {
		this.chatting = false;
	}
	
	public static void main(String[] args){
		
		ppmc_new app = new ppmc_new("Peer-to-Peer Multicast Chat for csci3363");
		System.setProperty("java.net.preferIPv4Stack", "true"); //important
		app.connect(args);
		app.setVisible(true);

	}
	
	
	public void connect(String[] args){

		if(args.length != 3){

			System.err.print("\nUsage: java ppmc <group> <port> ");
			System.err.print("<screen name>");

		}

		try {

			group = InetAddress.getByName(args[0]);
			port = Integer.parseInt(args[1]);
			screenName = args[2];

        } catch (UnknownHostException e) {

            System.err.println("Unknown host: " + args[0]);
            System.exit(1);

		} catch (NumberFormatException nfe){

			System.err.println("Invalid port: " + args[1]);
            System.exit(1);

		}
		
		try{

			socket = new MulticastSocket(port); 
			socket.setTimeToLive(1);
			socket.joinGroup(group);
			outgoing = new DatagramPacket(new byte[1], 1, group, port);
			incoming = new DatagramPacket(new byte[65508], 65508);

		}catch(IOException ioe1){

			System.err.println("Unable to join " + args[0]);
			System.exit(1);

		}

		

		ta.append("Joined ");
		ta.append(args[0]);
		ta.append(" on port " + args[1]);
		ta.append("\n\n");
		SocketReader reader = new SocketReader();

	}		

	class tfkListener extends KeyAdapter{

		public void keyPressed(KeyEvent e){

			if(e.getKeyCode() == KeyEvent.VK_ENTER){

				StringBuffer sb = new StringBuffer();
				sb.append(screenName);
				sb.append(": ");
				String str = tf.getText();
				if(str.equals("quit")){
					endChat();
					sb.append("i am leaving, goodbye");
					str = sb.toString();
					byte[] buf = str.getBytes();
					outgoing.setData(buf);
					outgoing.setLength(buf.length);
					try {
						socket.send(outgoing);
						//System.exit(0);
						return;
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				if(str != null && !str.equals("")){

					sb.append(str);
					str = sb.toString();

					try{

						byte[] buf = str.getBytes();
						outgoing.setData(buf);
						outgoing.setLength(buf.length);
						socket.send(outgoing);
						tf.setText("");
					
					}catch(IOException ioe){
						ta.append("\nError sending " + str);
					}
				}
	
			}
		}
	}

	class bListener implements ActionListener{

		public void actionPerformed(ActionEvent e){
			StringBuffer sb = new StringBuffer();
			sb.append(screenName);
			sb.append(": ");
			String str = tf.getText();
			if(str != null && !str.equals("quit")){

				sb.append(str);
				str = sb.toString();

				try{

						byte[] buf = str.getBytes();
						outgoing.setData(buf);
						outgoing.setLength(buf.length);
						socket.send(outgoing);
						tf.setText("");
					
					}catch(IOException ioe){
						ta.append("\nERROR: sending " + str);
					}
			}

			if(str.equals("quit")){
				endChat();
				sb.append("i am leaving, goodbye");
				str = sb.toString();

				try{
						byte[] buf = str.getBytes();
						outgoing.setData(buf);
						outgoing.setLength(buf.length);
						socket.send(outgoing);
						tf.setText("");
						return;
						//System.exit(0);
					}catch(IOException ioe){
						ta.append("\nERROR: sending " + str);
					}
			}
			
			tf.setText("");
			tf.requestFocus();

		}

	}

	class tafListener extends FocusAdapter{

		public void focusGained(FocusEvent e){

			tf.requestFocus();
			

		}

	}

	class WL extends WindowAdapter{
	
		public void windowClosing(WindowEvent e){
			try{
				socket.leaveGroup(group);
				socket.close();
			}catch(IOException ignore){}
			endChat();
		}

		public void windowDeiconified(WindowEvent e){
			ta.requestFocus();	
			
		}
	}

	class SocketReader extends Thread{

		private boolean chatting;

		public SocketReader(){
		
			start();
			
		}
		
		public void run(){

			String msg;
		
			try{
			
				while(true){

					socket.receive(incoming);
					msg = new String(incoming.getData(), 0,
					                 incoming.getLength());

					ta.append(msg + "\n");
					yield();

				}


			}catch(IOException lostGroup){

				ta.append("\nLost Group.");
				tf.setVisible(false);
				l.setVisible(false);
				b.setVisible(false);
				tf.removeKeyListener(tkfl);
				b.removeActionListener(bl);

			}finally{

				try{
					socket.leaveGroup(group);
					socket.close();
				}catch(IOException ignore){}

			}	

		}

	}

}
