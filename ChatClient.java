package Mike;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import Mike.ChatServer;

public class ChatClient implements Runnable{
	private Socket socket = null;
	BufferedReader console = null;
	private DataOutputStream streamOut = null;
	private ChatClientThread client = null;// fix in ver4
	private boolean done = false;
	private String line = "";
	private Thread thread = null;
	
	public ChatClient(String serverName, int serverPort){
		try{
			socket = new Socket(serverName, serverPort);//connect to a server using their know port
			System.out.println("Got connected to soclet : " + socket+ " on port " + serverPort);
			start(); // we're streaming
			String line = "";
			while(!line.equalsIgnoreCase("bye:")){
				line = console.readLine();//read the input
				streamOut.writeUTF(line);
				streamOut.flush();
			}
		}catch(UnknownHostException uhe){
			System.out.println("unknown host: OOPS! " + uhe.getMessage());
		}catch(IOException ioe){
			System.out.println("IO problem client: " + ioe.getMessage());
			
		}
	}
	public void start() throws IOException{
		console = new BufferedReader(new InputStreamReader(System.in));
		streamOut = new DataOutputStream(socket.getOutputStream());
		if(thread == null){
			client = new ChatClientThread(this, socket);
			thread = new Thread(this);
			thread.start();
		}
	}
	public void stop(){
		done = true;//set flag to done
		if(thread != null){
			thread = null;
		}
		try{
			if(console != null)
				console.close();
			if(streamOut != null)
				streamOut.close();
			if(client != null)
				client = null;
			if(socket != null)
				socket.close();
		}catch(IOException ioe){
			System.out.println("Error closing ..." + ioe.getMessage());
		}
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while((thread != null) && ( !line.equalsIgnoreCase("bye"))){
			try{
				line = console.readLine();
				streamOut.writeUTF(line);
				streamOut.flush();
			}catch(IOException ioe){
				System.out.println("error sending the message "+ioe.getMessage());
			}
		}
	}
	public void handle(String msg){
		if(msg.equalsIgnoreCase("bye")){
			line = "bye";
			stop();
		}
	}
	public static void main(String[] args){
		//ChatServer server = new ChatServer(8081);
		
		ChatServer server = null;
		if(args.length !=1){
			System.out.println("To run a chat server you need to specifya port");
		}else{
			server = new ChatServer(Integer.parseInt(args[0]));//first argument is port
		}
		
	}
	
	
}
