package com.mobileComputingAssignment4;

import java.io.IOException;
import java.net.*;
import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Main
{
    private static DatagramSocket recvSock ;
    private static DatagramSocket ackSock ;
    private static DatagramSocket sendSock;

    private static DatagramPacket recvPacket;
    private static DatagramPacket ackPacket;
    private static DatagramPacket sendPacket;

    private static final int teamNumber = 5;
    private static final int port = 5000 + 10 * teamNumber;

    private static List<InetAddress> nodes = new ArrayList<InetAddress>();
    private static List<String> nodeAddresses = new ArrayList<String>();

    private static String ackMsg = "";
    private static String bcastMsg = "";

    public static void main(String[] args) throws IOException
    {
        byte[] recvBuf = new byte[100]; // used to store received message data
        byte[] ackBuf = new byte[100]; // used to store ack message data
        byte[] sendBuf = new byte[100];

        // Open Socket to listen to all UDP traffic on this port
        try
        {
            recvSock = new DatagramSocket(port);
            recvSock.setBroadcast(true);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        // Open Socket to send acknowledgements
        try
        {
            ackSock = new DatagramSocket();
            ackSock.setBroadcast(false);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        // Open Socket to send broadcast
        try
        {
            sendSock = new DatagramSocket();
            sendSock.setBroadcast(true);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        System.out.println("Sockets initialized");

        // Create new receiver packet to store received message data
        recvPacket = new DatagramPacket(recvBuf, recvBuf.length);

        bcastMsg = args[0];
        sendBuf = bcastMsg.getBytes();
        System.out.println("Message initialized with: "+ bcastMsg);

        sendPacket = new DatagramPacket(sendBuf, sendBuf.length, InetAddress.getByName("192.168.132.255"), port);

        int i = 0;

        while (i < 20)
        {
            System.out.println("Sending UDP message ");
            sendSock.send(sendPacket);

            System.out.println("Receiving UDP messages ");
            try
            {
                recvBuf = null;
                // Receive a packet
                recvSock.receive(recvPacket);

                // Print received packet data
                System.out.println("Packet received from: " + recvPacket.getAddress().getHostAddress());
                System.out.println("Received packet data: " + new String(recvPacket.getData()));

                String test = new String(recvPacket.getData());
                Collator collator = Collator.getInstance(Locale.US);
                System.out.println("Convert: " + collator.compare(test, bcastMsg));

                //Send a response
                if (0 == collator.compare(test, bcastMsg))
                {
                    System.out.println("Received own broadcast message");
                }
                else
                {
                   System.out.println("Received another broadcast message");
                    ackMsg = "Received your Hello World";
                    ackBuf = ackMsg.getBytes();
                    ackPacket = new DatagramPacket(ackBuf, ackBuf.length, recvPacket.getAddress(), recvPacket.getPort());
                    ackSock.send(ackPacket);
                    System.out.println("Ack sent to: " + ackPacket.getAddress().getHostAddress());
                }

                //nodes.add(receiverPacket.getAddress());
            } catch (IOException e) {
                e.printStackTrace();
            }

            i++;
        }
    }

    /*public static void main(String[] args) throws UnknownHostException {
	// write your code here
        byte[] bcast_msg;

        byte[] buf = new byte[100]; // used to store received message data


        try {
            senderSock = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        initializeSocket (senderSock);


        try
        {
            // Open Socket to listen to all UDP traffic on this port
            receiverSock = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        // Enable Broadcast on this socket and port
        initializeSocket(receiverSock);



        msg = "Hello World message";

        bcast_msg = msg.getBytes();

        senderPacket = new DatagramPacket(bcast_msg, bcast_msg.length, InetAddress.getByName("192.168.132.255"), port);
        receiverPacket = new DatagramPacket(buf, buf.length);


        while (i < 10)
        {
            System.out.println("Sending message ");
            try {
                senderSock.send(senderPacket);
                System.out.println("Message sent");
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                System.out.println("Sleep ");
                Thread.sleep( 1000 );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Receiving message " + i);
            try {
                receiverSock.receive(receiverPacket);
                nodes.add(receiverPacket.getAddress());//TODO: when to stop listening
                System.out.println(receiverPacket.getData().toString());
                System.out.println(receiverPacket.getSocketAddress().toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            i++;

        }
        for (int j = 0; j < nodes.size()-1; j++) {
            System.out.println("Address: " + nodes.get(j));
        }

    }*/
}
