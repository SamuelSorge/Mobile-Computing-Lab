/**
 * ServerThread class which implements forwarding and response messages
 *
 * @author Jaasiel Walter
 * @version 1.0
 */
package com.mobileComputing;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
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
    private static List<InetAddress> nodeAddresses = new ArrayList<InetAddress>();
    private static String address;

    public void run() {
        try {
            sock = new DatagramSocket(port);
            sock.setBroadcast(true);

            byte[] bcastMsg = new byte[500];
            DatagramPacket packet = new DatagramPacket(bcastMsg, bcastMsg.length);

            while (true) {
                System.out.println(getClass().getName() + " Receiving broadcast messages!");
                sock.receive(packet);

                Message msg = new Message(packet.getData());
                System.out.println(getClass().getName() + " \n Message!" + new String(packet.getData()));
                seqNr = msg.getSequenceNumber();

                String message = new String(msg.getMessageData()).trim();
//                System.out.println(getClass().getName() + " SequenceNumber: " + msg.getSequenceNumber() +
//                        ", HopCount: " + msg.getHopCount() + (", messageData: " + new String(msg.getMessageData())) +
//                        "Source: " + packet.getAddress().getHostAddress());

                if (message.equals("DISCOVER_NODE_REQUEST")) {
                    System.out.println(getClass().getName() + "Message equal");
                    if (!clientModeOn && seqNr != seqNrTemp) {
                        System.out.println(getClass().getName() + msg.getTarget());
                        if (msg.getTarget().equals(InetAddress.getByName(address))) {

                            byte[] reply = "DISCOVER_NODE_RESPONS".getBytes();
                            Message msgTest = new Message(1, msg.getHopCount(), reply, msg.getNodes());
                            sendSock = new DatagramSocket();
                            sendSock.setBroadcast(true);

                            DatagramPacket sendPacket = new DatagramPacket(msgTest.toByte(), msgTest.getLength(), InetAddress.getByName("192.168.132.255"), port);
                            sendSock.send(sendPacket);
                            System.out.println(getClass().getName() + "I am the destination");

                        } else {

                            sendSock = new DatagramSocket();
                            sendSock.setBroadcast(true);
                            nodeAddresses.add(msg.getSource());
                            nodeAddresses.add(msg.getTarget());
                            // add myself
                            nodeAddresses.add(InetAddress.getByName(address));

                            byte[] sendData = "DISCOVER_NODE_REQUEST".getBytes();
                            Message msgTest = new Message(1, msg.getHopCount()+1, sendData, nodeAddresses);
                            System.out.println(getClass().getName() + " SequenceNumber: " + msgTest.getSequenceNumber() +
                                    ", HopCount: " + msgTest.getHopCount() + (", messageData: " + new String(msgTest.getMessageData())));

                            //DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("192.168.132.255"), port);
                            DatagramPacket sendPacket = new DatagramPacket(msgTest.toByte(), msgTest.getLength(), InetAddress.getByName("192.168.132.255"), port);
                            sendSock.send(sendPacket);
                            System.out.println(getClass().getName() + "\n Packet sent to: 192.168.132.255");
                        }

                        seqNrTemp = seqNr;
                    } else {
                        System.out.println(getClass().getName() + "Message already received");
                    }
                }
                else if(message.equals("DISCOVER_NODE_RESPONS"))
                {
                    byte[] reply = "DISCOVER_NODE_RESPONS".getBytes();
                    Message msgTest = new Message(1, msg.getHopCount()-1, reply, msg.getNodes());
                    sendSock = new DatagramSocket();
                    sendSock.setBroadcast(true);

                    // TODO: Auf Reihenfolge achten: nodes[0] = source, nodes[1] = target, node[i]=hops

//                    DatagramPacket sendPacket = new DatagramPacket(msgTest.toByte(), msg.getLength(), msgTest.getNodes().get(msgTest.getHopCount()));
//                    sendSock.send(sendPacket);
//                    System.out.println(getClass().getName() + "I am the destination");
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } /*catch (InterruptedException e) {
            e.printStackTrace();
        }*/

    }

    public static ServerThread getInstance(boolean clientMode, String adr) {

        clientModeOn = clientMode;
        address = adr;
        return ServerThreadHolder.INSTANCE;
    }

    public static class ServerThreadHolder {
        private static final ServerThread INSTANCE = new ServerThread();
    }
}
