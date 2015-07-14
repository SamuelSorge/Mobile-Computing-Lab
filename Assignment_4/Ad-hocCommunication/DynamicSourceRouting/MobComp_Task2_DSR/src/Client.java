import java.net.Socket;


public class Client extends Thread{

	private Server callbackServer;
	private Socket callbackSocket;
	boolean isRunning;
	
	public Client(Socket socket, Server callbackServer) {
		initialise (socket, callbackServer);
	}
	
	
	private void initialise(Socket socket, Server callbackServer) {
		this.callbackServer = callbackServer;		
	}
	
	public void run(){
		while(isRunning){
			
		}
	}

}
