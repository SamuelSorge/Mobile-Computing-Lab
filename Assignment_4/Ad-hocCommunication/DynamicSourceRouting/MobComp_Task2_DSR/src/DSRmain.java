import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;


public class DSRmain {

	/**
	 * @param args
	 */
	private static DatagramSocket socket;
    private static DatagramPacket sendPacket;
	
	public static void main(String[] args) {
		//Socket socket;
		Server server;
		try {
			server = new Server (socket);
			Client client = new Client(socket, server);

			server.start();
			client.start();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	

	}

}
