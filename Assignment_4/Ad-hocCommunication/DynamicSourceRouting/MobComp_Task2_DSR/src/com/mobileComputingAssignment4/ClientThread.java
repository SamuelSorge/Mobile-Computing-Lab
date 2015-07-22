package com.mobileComputingAssignment4;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jaasiel on 14.07.2015.
 */
public class ClientThread implements Runnable {
	DatagramSocket sock;

	final int teamNumber = 5;
	final int port = 5000 + 10 * teamNumber +1;

	// added for DSR
	private static List<String> nodeAddresses = new ArrayList<>();

	public void run() {
		try {
			sock = new DatagramSocket();
			sock.setBroadcast(true);

			byte[] sendData = "DISCOVER_NODE_REQUEST".getBytes();
			Message msg = new Message(1, 0, sendData,
					InetAddress.getByName("192.168.132.255"));

			System.out.println(getClass().getName() + " SequenceNumber: "
					+ msg.getSequenceNumber() + ", HopCount: "
					+ msg.getHopCount()
					+ (", messageData: " + new String(msg.getMessageData())));

			// DatagramPacket sendPacket = new DatagramPacket(sendData,
			// sendData.length, InetAddress.getByName("192.168.132.255"), port);
			DatagramPacket sendPacket = new DatagramPacket(msg.toByte(),
					msg.getLength(), InetAddress.getByName("192.168.132.255"),
					port);
			sock.send(sendPacket);
			System.out.println(getClass().getName()
					+ "\n Packet sent to: 192.168.132.255");

			/*
			 * byte[] recvBuf = new byte[500]; DatagramPacket receivePacket =
			 * new DatagramPacket(recvBuf, recvBuf.length);
			 * 
			 * 
			 * sock.receive(receivePacket);
			 * nodeAddresses.add(receivePacket.getAddress().getHostAddress());
			 * System.out.println(getClass().getName() +
			 * " Broadcast response from: " +
			 * receivePacket.getAddress().getHostAddress());
			 * 
			 * //String message = new String(receivePacket.getData()).trim();
			 * //if(message.equals("DISCOVER_NODE_RESPONSE")) { Message response
			 * = new Message(receivePacket.getData()); String message = new
			 * String(response.getMessageData()).trim(); //
			 * System.out.println(getClass().getName() + " Test2"+message); //
			 * if
			 * (msg.getMessageData().toString().equals("DISCOVER_NODE_RESPONSE"
			 * )) { if (message.equals("DISCOVER_NODE_RESPONSE")) {
			 * System.out.println(getClass().getName() + " Response correct!");
			 * }
			 */

			//sock = new DatagramSocket(port);
			// sendSock = new DatagramSocket();
			// sendSock.setBroadcast(true);
			//sock.setBroadcast(true);
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
				Message recievedMsg = new Message(packet.getData());

				String message = new String(recievedMsg.getMessageData())
						.trim();
				System.out.println(getClass().getName()
						+ " SequenceNumber: "
						+ recievedMsg.getSequenceNumber()
						+ ", HopCount: "
						+ recievedMsg.getHopCount()
						+ (", messageData: " + new String(recievedMsg
								.getMessageData())) + "Source: "
						+ packet.getAddress().getHostAddress());
				if (message.equals("SEND_BACK_ROUTE")) {
					System.out.println(recievedMsg.getMessageNodeList());

				}
				/*
				 * sendSock = new DatagramSocket(); sendSock.setBroadcast(true);
				 * 
				 * byte[] sendData = "SEND_BACK_ROUTE".getBytes();
				 * 
				 * Message msgTest = new Message(1, 1, sendData,
				 * recievedMsg.getDestinationNode());
				 * msgTest.addNodeToList(InetAddress.getLocalHost());
				 * msgTest.setCurrentPositionInList(msgTest
				 * .getMessageNodeList().size());
				 * 
				 * // get node before this node, send msg to that node String
				 * PrevNode = "" + msgTest.getMessageNodeList().get(
				 * msgTest.getCurrentPositionInList() - 1);
				 * 
				 * DatagramPacket sendPacket = new DatagramPacket(
				 * msgTest.toByte(), msgTest.getLength(),
				 * InetAddress.getByName(PrevNode), port);
				 * sendSock.send(sendPacket);
				 * System.out.println(getClass().getName() +
				 * "\n NodeList sent to: " + PrevNode);
				 */

			} // sock.close();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static ClientThread getInstance() {
		return ClientThreadHolder.INSTANCE;
	}

	public static class ClientThreadHolder {
		private static final ClientThread INSTANCE = new ClientThread();
	}

	public void receiveNodeList(List<String> nodeAddresses) {
		System.out.println("receive NodeList Route");
		System.out.println(nodeAddresses);

	}
}
