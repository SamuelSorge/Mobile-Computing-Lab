/**
 * ServerThread class which implements forwarding and response messages
 *
 * @author Jaasiel Walter
 * @version 1.0
 */
package com.mobileComputingAssignment4;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.List;

/**
 * Created by Jaasiel on 14.07.2015.
 */
public class ServerThread implements Runnable {
	DatagramSocket sock;
	DatagramSocket sendSock;

	// Calculate port number
	final int teamNumber = 5;
	final int port = 5000 + 10 * teamNumber;

	Boolean messageNotYetForwarded = true;
	private static Boolean clientModeOn = false;
	private static int seqNr = 0;
	private static int seqNrTemp = 0;
	static String serverAddress;

	public void run() {
		try {
			sock = new DatagramSocket(port);
			// sendSock = new DatagramSocket();
			// sendSock.setBroadcast(true);
			sock.setBroadcast(true);
			byte[] bcastMsg = new byte[500];
			DatagramPacket packet = new DatagramPacket(bcastMsg,
					bcastMsg.length);
			// System.out.println(getClass().getName() + " Client mode! " +
			// clientModeOn);
			while (true) {
				System.out.println(getClass().getName()
						+ " Receiving broadcast messages!");

				sock.receive(packet);

				// System.out.println(getClass().getName() +
				// " Message received from: "+
				// packet.getAddress().getHostAddress());
				// System.out.println(getClass().getName() +
				// " Received message data: "+ new String(packet.getData()));

				// String message = new String(packet.getData()).trim();
				
				
				Message msg = new Message(packet.getData());
				seqNr = msg.getSequenceNumber();
				String message = new String(msg.getMessageData()).trim();
				System.out
						.println(getClass().getName()
								+ " SequenceNumber: "
								+ msg.getSequenceNumber()
								+ ", HopCount: "
								+ msg.getHopCount()
								+ (", messageData: " + new String(msg
										.getMessageData())) + "Source: "
								+ packet.getAddress().getHostAddress());
				// if (message.equals("DISCOVER_NODE_REQUEST")) {
				
				//add current Node to NodeList
				System.out.println("msg.getMessageNodelist " + msg.getMessageNodeList());
				List<InetAddress> currentMessageNodeList = msg.getMessageNodeList();
				System.out.println("currentMessageNodeList " + currentMessageNodeList);
				currentMessageNodeList.add(msg.getMessageNodeList().size(), InetAddress.getByName(serverAddress));
				
				
				System.out.println(InetAddress.getByName(serverAddress));
				System.out.println("destination node AFTER sending " +msg.getDestinationNode());
				
				//if (serverAddress.equals("192.168.132.12")){
			
				if (msg.getDestinationNode().equals(InetAddress.getByName(serverAddress))) {
					// TODO send back route to source node

					sendSock = new DatagramSocket();
					sendSock.setBroadcast(true);

					byte[] sendData = "SEND_BACK_ROUTE".getBytes();

					// get node before this node, send msg to that node
					//int nodeNumber = currentMessageNodeList.size();
					int nodeNumber = currentMessageNodeList.indexOf(InetAddress.getByName(serverAddress));
					InetAddress PrevNode = currentMessageNodeList.get(nodeNumber-1);
					
					
					
					Message msgTest = new Message(1, 1, sendData, msg.getDestinationNode(),
							currentMessageNodeList);
					msgTest.addNodeToList(InetAddress.getByName(serverAddress));
					msgTest.setCurrentPositionInList(msgTest
							.getMessageNodeList().size());

					

					DatagramPacket sendPacket = new DatagramPacket(
							msgTest.toByte(), msgTest.getLength(),
							InetAddress.getByName(""+PrevNode), port);
					sendSock.send(sendPacket);
					System.out.println(getClass().getName()
							+ "\n NodeList sent to: " + PrevNode);

				} else {
					// if (message.equals("DISCOVER_NODE_REQUEST") &&
					// !clientModeOn && (seqNr != seqNrTemp)) {
					if (message.equals("DISCOVER_NODE_REQUEST")) {
						// byte[] sendData =
						// "DISCOVER_NODE_RESPONSE".getBytes();
						System.out.println(getClass().getName()
								+ "Message equal");
						if (!clientModeOn && seqNr != seqNrTemp) {


							sendSock = new DatagramSocket();
							sendSock.setBroadcast(true);

							byte[] sendData = "DISCOVER_NODE_REQUEST"
									.getBytes();

							Message msgTest = new Message(1, 1, sendData,
									msg.getDestinationNode(), currentMessageNodeList);
							msgTest.addNodeToList(InetAddress.getByName(serverAddress));

							System.out.println(getClass().getName()
									+ " SequenceNumber: "
									+ msgTest.getSequenceNumber()
									+ ", HopCount: "
									+ msgTest.getHopCount()
									+ (", messageData: " + new String(msgTest
											.getMessageData())));

							// DatagramPacket sendPacket = new
							// DatagramPacket(sendData, sendData.length,
							// InetAddress.getByName("192.168.132.255"), port);
							DatagramPacket sendPacket = new DatagramPacket(
									msgTest.toByte(), msgTest.getLength(),
									InetAddress.getByName("192.168.132.255"),
									port);
							sendSock.send(sendPacket);
							System.out.println(getClass().getName()
									+ "\n Packet sent to: 192.168.132.255");

							seqNrTemp = seqNr;
						} else {
							System.out.println(getClass().getName()
									+ "Message already received");
						}
			
					} else if (message.equals("SEND_BACK_ROUTE")) {

						sendSock = new DatagramSocket();
						sendSock.setBroadcast(true);

						byte[] sendData = "SEND_BACK_ROUTE".getBytes();
						

						Message msgTest = new Message(1, 1, sendData,
								msg.getDestinationNode(), currentMessageNodeList);
						msgTest.addNodeToList(InetAddress.getByName(serverAddress));
						msgTest.setCurrentPositionInList(msgTest
								.getMessageNodeList().size());

						// get node before this node, send msg to that node
						String PrevNode = ""
								+ msgTest.getMessageNodeList().get(
										msgTest.getCurrentPositionInList() - 1);

						DatagramPacket sendPacket = new DatagramPacket(
								msgTest.toByte(), msgTest.getLength(),
								InetAddress.getByName(PrevNode), port);
						sendSock.send(sendPacket);
						System.out.println(getClass().getName()
								+ "\n NodeList sent to: " + PrevNode);

	
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} /*
		 * catch (InterruptedException e) { e.printStackTrace(); }
		 */

	}

	public static ServerThread getInstance(boolean clientMode, String address) {
		serverAddress = address;
		clientModeOn = clientMode;
		return ServerThreadHolder.INSTANCE;
	}

	public static class ServerThreadHolder {
		private static final ServerThread INSTANCE = new ServerThread();
	}

}
