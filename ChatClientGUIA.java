package Mike;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
//import chat_server_client_5.ChatClientThread;

public class ChatClientGUIA extends Applet implements ActionListener, Runnable {
	private Socket socket = null;
	private DataInputStream streamIn = null;
	private DataOutputStream streamOut = null;
	private ChatClientThread client = null;
	private TextArea display = new TextArea();
	private TextField input = new TextField();
	private Button send = new Button("Send");
	private Button connect = new Button("Connect");
	private Button quit = new Button("Bye");
	private String serverName = "localhost";//will get from browser
	private int serverPort = 8081; //wil get from browser
	Panel mainPanel = new Panel();
	Panel keys = new Panel();
	Panel south = new Panel();
	private boolean done = true;
	private String line = "";
	
	public void init(){//set up gui
		mainPanel.setLayout(new BorderLayout());
		keys.setLayout(new GridLayout(1,2));
		connect.setEnabled(true);
		connect.addActionListener(this);
		quit.setEnabled(false);
		quit.addActionListener(this);
		keys.add(quit);
		keys.add(connect);
		south.setLayout(new BorderLayout());
		south.add("West",  keys);
		south.add("Center",input);
		send.setEnabled(false);
		send.addActionListener(this);
		south.add("East", send);
		Label title = new Label("Our Lovely Chat", Label.CENTER);
		title.setFont(new Font("Helvetica", Font.BOLD, 14));
		mainPanel.add(title, BorderLayout.NORTH);
		display.setEditable(false);
		display.setBackground(Color.GREEN);
		mainPanel.add(display, BorderLayout.CENTER);
		mainPanel.add(south, BorderLayout.SOUTH);
		add(mainPanel);
	}
	public void open(){
		try{
			streamOut = new DataOutputStream(socket.getOutputStream());
			streamIn = new DataInputStream(socket.getInputStream());
			new Thread(this).start();//background thread to handle incoming stream interaction with the server
		}catch(IOException ioe){
			ioe.printStackTrace();
		 }
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try{
			while(!done){
				line = streamIn.readUTF();
				//evaluate incoming message....
				displayOutput(line);
			}
		}catch(IOException ioe){
				done = true;
				displayOutput(ioe.getMessage());
		 }
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		//can use either getActionCommand or getSource... both should work
		String btnTxt = e.getActionCommand().trim();
		if(btnTxt.equalsIgnoreCase("connect")){
			connect(serverName, serverPort);
			input.setText("");
			System.out.println("connect button hit");
		}else if(e.getSource() == quit){
			disconnect();
			System.out.println("quit button hit");
		}else if(e.getSource() == send){
			send();
			input.setText("");
			System.out.println("send button hit");
		}
	}
	public void connect(String serverName, int serverPort){
		//connect to server
		done=false;
		displayOutput("connection time...");
		try{
			socket = new Socket(serverName, serverPort);
			displayOutput("connection: " + socket);
			open();
			send.setEnabled(true);//enable the send button
			quit.setEnabled(true);//enable the quit button
			connect.setEnabled(false);//cant connect while already connected
		}catch(UnknownHostException uhe){
			displayOutput(uhe.getMessage());
			done=true;
		}catch(IOException ioe){
			displayOutput(ioe.getMessage());
			done=true;
		}
	}
	public void disconnect(){//disconnect from server.,,set flag
		done = true;
		input.setText("bye");
		send();
		quit.setEnabled(false);
		connect.setEnabled(true);
		send.setEnabled(false);
		
	}
	public void send(){
		String msg = input.getText().trim();//get text from the input textfield
		try{//try and send the info....set text in the gui
			streamOut.writeUTF(msg);
			streamOut.flush();
			
			if(msg.equalsIgnoreCase("bye")){
				quit.setEnabled(false);
				connect.setEnabled(true);
				send.setEnabled(false);
				close();
			}
		}catch(IOException ioe){
			displayOutput("problem sending..."+ioe.getMessage());
			close();
		}
	}
	public void handle(String msg){
		if(msg.equalsIgnoreCase("bye")){
			displayOutput("GOODBYE");
			close();
		}
	}
	
	public void displayOutput(String msg){
		display.append("\n" + msg +"\n-----");
	}
	public void close(){
		done = true;
		try{
			if(streamOut !=null){
				streamOut.close();
			}
			if(socket != null){
				socket.close();
			}
		}catch(IOException ioe){
			displayOutput("OOPS problem closing");
			client.close();
			client = null;
		}
	}
}

