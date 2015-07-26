package com.mobileComputing;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * ServerThread class which implements forwarding and response messages
 *
 * @author Jaasiel Walter
 * @version 1.0
 */
public class ServerThread implements Runnable {

    private static Boolean clientModeOn = false;

    // Calculate port number
    final int teamNumber = 5;
    final int port = 5000 + 10 * teamNumber;

    private static int seqNr = 0;
    private static int seqNrTemp = 0;
    private static List<InetAddress> nodeAddresses = new ArrayList<InetAddress>();
    private static String address;

    private DatagramSocket sock;
    private DatagramSocket sendSock;

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
                seqNr = msg.getSequenceNumber();
                String message = new String(msg.getMessageData()).trim();

                if (message.equals(Message.DISCOVER_NODE_REQUEST)) {
                    if (!clientModeOn && seqNr != seqNrTemp) {
                        System.out.println(getClass().getName() + " I am not the client\n");
                        handleMessageIfNotClientAndNotReceivedBefore(packet, msg);
                        seqNrTemp = seqNr;
                    } else {
                        System.out.println(getClass().getName() + " Message already received");
                    }
                } else if (message.equals(Message.DISCOVER_NODE_RESPONS))// E missing because of hard coded length 21
                {
                    handleResponse(msg);
                    //TODO in message equal requets noch direkt zurück an source
                    // else
                    //{
                    //   System.out.println(getClass().getName() + "Direct from destination reply");
                    //  Message msgTest = new Message(1, msg.getHopCount(), reply, msg.getNodes());
                    // }


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
        }
    }

    private void handleMessageIfNotClientAndNotReceivedBefore(DatagramPacket packet, Message msg) throws IOException {
        sendSock = new DatagramSocket();
        sendSock.setBroadcast(true);
        if (msg.getTarget().equals(InetAddress.getByName(address))) {
            System.out.println(getClass().getName() + " I am the destination");

            byte[] reply = Message.DISCOVER_NODE_RESPONS.getBytes();
            nodeAddresses.clear();
            nodeAddresses.add(InetAddress.getByName(msg.getNodes().get(0).getHostAddress()));
            nodeAddresses.add(InetAddress.getByName(msg.getNodes().get(1).getHostAddress()));

            Message msgTest = new Message(1, msg.getHopCount(), reply, nodeAddresses);
            DatagramPacket sendPacket;
            if (msg.getHopCount() != 0) {
                System.out.println(getClass().getName() + " There was a hop between");
                sendPacket = new DatagramPacket(msgTest.toByte(), msgTest.getLength(), InetAddress.getByName("192.168.132.255"), port);
            } else {
                System.out.println(getClass().getName() + " This Discovery came directly to me");
                sendPacket = new DatagramPacket(msgTest.toByte(), msgTest.getLength(), packet.getAddress(), packet.getPort());
            }
            sendSock.send(sendPacket);

        } else {
            System.out.println(getClass().getName() + " I am not the destination");

            nodeAddresses.add(msg.getSource());
            nodeAddresses.add(msg.getTarget());
            // add myself
            nodeAddresses.add(InetAddress.getByName(address));

            byte[] sendData = Message.DISCOVER_NODE_REQUEST.getBytes();
            Message msgTest = new Message(1, msg.getHopCount() + 1, sendData, nodeAddresses);
            DatagramPacket sendPacket = new DatagramPacket(msgTest.toByte(), msgTest.getLength(), InetAddress.getByName("192.168.132.255"), port);
            sendSock.send(sendPacket);
            System.out.println(getClass().getName() + " Forwarded Packet sent to: 192.168.132.255");
        }
    }

    private void handleResponse(Message msg) throws IOException {
        byte[] reply = Message.DISCOVER_NODE_RESPONS.getBytes();
        sendSock = new DatagramSocket();
        sendSock.setBroadcast(true);
        //Hint: this path is useless -> see client
        if (msg.getSource().equals(address)) {
            System.out.println(getClass().getName() + " Reply reached me, I am source");
            System.out.println(getClass().getName() + " Route: ");

            if (msg.getHopCount() == 0) {
                System.out.println(getClass().getName() + " " + msg.getNodes().get(1) + "->" + msg.getNodes().get(0));
            } else {
                int hop = msg.getHopCount();
                while (hop > 0) {
                    System.out.println(getClass().getName() + " " + msg.getNodes().get(hop + 1) + "->");
                    hop--;

                }
                System.out.println(getClass().getName() + " " + msg.getNodes().get(1) + "->" + msg.getNodes().get(0));

            }
        } else if (msg.getHopCount() != 0) {
            System.out.println(getClass().getName() + " Reply Forwarded");
            Message msgTest = new Message(1, msg.getHopCount() - 1, reply, msg.getNodes());
            int hop = msg.getHopCount();
            hop++;
            String dest = new String(msg.getNodes().get(hop).getHostAddress());

            DatagramPacket sendPacket = new DatagramPacket(msgTest.toByte(), msgTest.getLength(), InetAddress.getByName(dest), port);
            sendSock.send(sendPacket);
        }
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
