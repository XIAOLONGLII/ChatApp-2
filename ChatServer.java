package Mike;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


//import chat_server_client_4.ChatServerThread;

public class ChatServer implements Runnable{

	private ServerSocket server = null;
	private static ChatServerThread clients [] = new ChatServerThread[5]; //changes
	private Thread thread = null;
	private static int clientCount = 0;
	
	public static synchronized void handle(int ID, String input){
		String privMsg = "private message"; //can customize this further
		if (input.startsWith(privMsg)){
			//private message63536... example of the secret prefix
			//5 is always the length of an id.... it matches the # of digits in port
			int ID_SendTO = Integer.parseInt(input.substring(privMsg.length(),privMsg.length()+5));
			String msg = input.substring(privMsg.length()+6);//1 more so we start the reading of the message
			if(findClient(ID_SendTO) !=-1){
				clients[findClient(ID_SendTO)].send("from " + ID + ": " +input);
				
			}
		}
		else{
			for(int i=0; i<clientCount; i++){
				clients[i].send("USER: "+ ID+"    said " + input);
				
			}
		}
		if(input.equalsIgnoreCase("bye")){
			remove(ID);
		}
	}
	
	
	public static synchronized void remove(int ID){
		int pos = findClient(ID);
		if(pos>=0){
			ChatServerThread toTerminate = clients[pos];
			System.out.println("Removing client thread " + ID + " at " +pos);
			if(pos < clientCount -1){
				for (int i = pos+1; i<clientCount; i++){
					clients[i-1] = clients[i];
				}
				clientCount--;
			}
		
		try{
			toTerminate.close();
		}catch(IOException ioe){
			System.out.println("Error closing the thread "+ ioe.getMessage());
		}
		}
	}
	private static int findClient(int ID){
		for(int i=0; i<clientCount; i++){
			if(clients[i].getID() == ID){
				return i;
			}
		}
		return -1;// ID not found
	}
	private synchronized void addThread(Socket socket){
		//create a new CHatServerThread client using the constructor
		if(clientCount < clients.length){
			clients[clientCount] = new ChatServerThread(this, socket);
			//try to open the stream and start running the ChatServerThread client
			try{
				clients[clientCount].open();//open the stream for the ChatServerThread client
				clients[clientCount].start();//start runing the ChatServerThread client
				clientCount++;
			}catch(IOException ioe){
				System.out.println("OOPS! tried to add thread client" +ioe.getMessage());
			}
		}
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(thread != null){
			try{
				System.out.println("Waiting for a client.... inside run");
				addThread(server.accept());
				EarlyBird();
			}catch(IOException ioe){
				System.out.println("OOPSY" + ioe.getMessage());
			}
		}
	}
	public ChatServer(int port){
		try{
			System.out.println("Binding to port "+port+ "..wait !!!");
			server = new ServerSocket(port);
			start();
		}catch(IOException ioe){
			System.out.println("OOPS! : " + ioe.getMessage());
		}
	}
	public void start(){
		if(thread ==null ){
			thread = new Thread(this);
			thread.start();// will call the thread's run method
		}
	}
	public void stop(){
		if(thread !=null){
			thread = null;
			//done = true; // we don't say thread.stop anymore.. that is deprecated
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
	public void EarlyBird(){	
		if(clientCount>=5){
		for(int i = 0; i<4; i++){
			clients[i].send("you won");
		}
		}
	}

}
