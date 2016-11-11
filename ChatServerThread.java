package Mike;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatServerThread extends Thread{
	private Socket socket = null;
	private ChatServer server = null;
	private DataInputStream streamIn = null;
	private DataOutputStream streamOut = null;
	boolean done = true;
	private int ID = -1;
	
	public ChatServerThread(ChatServer _server, Socket _socket){
		server = _server;
		socket = _socket;
		ID = socket.getPort();
		System.out.println("Chat server Thread Info- SERVER: " + server+ " SOCKET: "+ socket +" ID:" + ID);
	}
	public int getID(){
		return ID;
	}
	public void open() throws IOException{
		streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		streamOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
	}
	public void close() throws IOException{
		if(streamIn != null){
			streamIn.close();
		}
		if(streamOut != null){
			streamOut.close();
		}
		if(socket != null){
			socket.close();
		}
	}
	public void send(String msg){
		try{
			streamOut.writeUTF(msg);
			streamOut.flush();
		}catch(IOException ioe){
			System.out.println(ID + "Error sending" + ioe.getMessage());
			server.remove(ID);
			ID =- 1; // set the ID to -1 for the client
		}
	}
	public void run(){
		while(ID != -1){
			try{
				server.handle(ID, streamIn.readUTF());
			}catch(IOException ioe){
				server.remove(ID);;
				ID =- 1;
				System.out.println("Had to remove " + ID + ioe.getMessage());
			}
		}
	}

}
