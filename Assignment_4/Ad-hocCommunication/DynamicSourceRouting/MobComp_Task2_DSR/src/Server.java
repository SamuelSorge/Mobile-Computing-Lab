^
import java.net.Socket;
import java.net.SocketException;

public class Server extends Thread {
	
	boolean isRunning = false;
	
	public Server(Socket socket) throws SocketException {
		initialise(socket);
	}

	private void initialise(Socket socket)
			throws SocketException {
		isRunning = true;
		
	}

}
