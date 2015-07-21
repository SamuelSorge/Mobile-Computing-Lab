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
				// TODO getLocast host durch richtige ip ersetzten
				if (msg.getDestinationNode().equals(InetAddress.getLocalHost())) {
					// TODO send back route to source node
					
					sendSock = new DatagramSocket();
					sendSock.setBroadcast(true);

					byte[] sendData = "DISCOVER_NODE_REQUEST"
							.getBytes();

					Message msgTest = new Message(1, 1, sendData,
							msg.getDestinationNode());
					msgTest.addNodeToList(InetAddress.getLocalHost());
					
					//get node before this node, send msg to that node
					msgTest.getMessageNodeList().
					
					
				} else {
					// if (message.equals("DISCOVER_NODE_REQUEST") &&
					// !clientModeOn && (seqNr != seqNrTemp)) {
					if (message.equals("DISCOVER_NODE_REQUEST")) {
						// byte[] sendData =
						// "DISCOVER_NODE_RESPONSE".getBytes();
						System.out.println(getClass().getName()
								+ "Message equal");
						if (!clientModeOn && seqNr != seqNrTemp) {
							// byte[] sendData = "Test".getBytes();
							// Message fwdMsg = new
							// Message(msg.getSequenceNumber(),
							// msg.getHopCount() + 1, msg.getMessageData());
							// System.out.println(getClass().getName() +
							// " New message data: "+ new
							// String(ackMsg.getMessageData()));
							// DatagramPacket sendPacket = new
							// DatagramPacket(sendData, sendData.length,
							// packet.getAddress(), packet.getPort());

							// DatagramPacket sendPacket = new
							// DatagramPacket(fwdMsg.toByte(),
							// fwdMsg.getLength(),
							// InetAddress.getByName("192.168.132.255"),
							// packet.getPort());
							// Thread.sleep(1000);
							// sendSock.send(sendPacket);
							// System.out.println(getClass().getName() +
							// "Message forwarded");
							// System.out.println(getClass().getName() +
							// " SequenceNumber: " + fwdMsg.getSequenceNumber()
							// + ", HopCount: " + fwdMsg.getHopCount() +
							// (", messageData: " + new
							// String(fwdMsg.getMessageData())));

							sendSock = new DatagramSocket();
							sendSock.setBroadcast(true);

							byte[] sendData = "DISCOVER_NODE_REQUEST"
									.getBytes();

							Message msgTest = new Message(1, 1, sendData,
									msg.getDestinationNode());
							msgTest.addNodeToList(InetAddress.getLocalHost());

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
						// System.out.println(getClass().getName() +
						// " Hop Count: " + msg.getHopCount());
						// System.out.println(getClass().getName() +
						// " Response sent to: " +
						// sendPacket.getAddress().getHostAddress());
					}// else if
						// (message.equals("DISCOVER_NODE_REQUEST_FORWARDED")){
					/*
					 * else if
					 * (message.equals("DISCOVER_NODE_REQUEST_FORWARDED") &&
					 * !clientModeOn){ byte[] sendData =
					 * "DISCOVER_NODE_RESPONSE_FORWARDED".getBytes();
					 * 
					 * Message fwdAckMsg = new Message(msg.getSequenceNumber(),
					 * msg.getHopCount(), sendData);
					 * System.out.println(getClass().getName() +
					 * " New message data: "+ new
					 * String(fwdAckMsg.getMessageData())); //DatagramPacket
					 * sendPacket = new DatagramPacket(sendData,
					 * sendData.length, packet.getAddress(), packet.getPort());
					 * 
					 * DatagramPacket sendPacket = new
					 * DatagramPacket(fwdAckMsg.toByte(), fwdAckMsg.getLength(),
					 * packet.getAddress(), packet.getPort());
					 * sock.send(sendPacket);
					 * 
					 * System.out.println(getClass().getName() + " Hop Count: "
					 * + msg.getHopCount());
					 * System.out.println(getClass().getName() +
					 * " Forward Response sent to: " +
					 * sendPacket.getAddress().getHostAddress()); }
					 * 
					 * if (seqNr != seqNrTemp) { messageNotYetForwarded = true;
					 * }
					 * 
					 * if (messageNotYetForwarded && !clientModeOn) { byte[]
					 * sendData = "DISCOVER_NODE_REQUEST_FORWARDED".getBytes();
					 * 
					 * Message fwdMsg = new Message(msg.getSequenceNumber(),
					 * msg.getHopCount()+1, sendData); //DatagramPacket
					 * sendPacket = new DatagramPacket(sendData,
					 * sendData.length,
					 * InetAddress.getByName("192.168.132.255"), port);
					 * Thread.sleep(1000); DatagramPacket sendPacket = new
					 * DatagramPacket(fwdMsg.toByte(), fwdMsg.getLength(),
					 * packet.getAddress(), packet.getPort());
					 * sock.send(sendPacket);
					 * 
					 * System.out.println(getClass().getName() +
					 * " Packet forwarded to: 192.168.132.255");
					 * messageNotYetForwarded = false; }
					 * 
					 * seqNrTemp = seqNr;
					 */
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

	public static ServerThread getInstance(boolean clientMode) {

		clientModeOn = clientMode;
		return ServerThreadHolder.INSTANCE;
	}

	public static class ServerThreadHolder {
		private static final ServerThread INSTANCE = new ServerThread();
	}
}
