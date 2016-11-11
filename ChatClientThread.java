package Mike;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatClientThread extends Thread{
	private Socket socket = null;
	private ChatClient client = null;
	private DataInputStream streamIn = null;
	private boolean done = true;
	
	public ChatClientThread(ChatClient _client, Socket _socket){
		client = _client;
		socket = _socket;
		open();
		start();
	}
	public void open(){
		try{
			streamIn = new DataInputStream(socket.getInputStream());
		}catch(IOException ioe){
			System.out.println("Error getting input stream " + ioe.getMessage());
			client.stop();
		}
	}
	@Override
	public void run(){
		done = false;
		while(!done){
			try{
				client.handle(streamIn.readUTF());
			}catch(IOException ioe){
				client.stop();
				done = true;
			}
		}
	}
	public void close(){
		try{
			if(streamIn != null){
				streamIn.close();
			}
		}catch(IOException ioe){
			client.stop();
			done = true;
		}
	}

}
