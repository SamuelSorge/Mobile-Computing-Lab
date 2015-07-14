package com.mobileComputingAssignment4;

import javafx.scene.chart.PieChart;

import java.io.IOException;
import java.net.*;
import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class Main
{
    private static DatagramSocket receiverSocket;
    private static DatagramSocket senderBroadcastSocket;
    private static DatagramSocket senderAcknowledgeSocket;

    private static final int teamNumber = 5;
    private static final int port = 5000 + 10 * teamNumber;

    private static Server serverThread;
    private static Client clientThread;

    private static boolean clientMode =  false;
    private static String userInput = "";

    private static Scanner scanner;

    private static String bcastMsg = "";

    public static void main(String[] args)
    {
        System.out.print("On which Machine is the Flooding executed? \n 1: 129.69.210.77 \n 2: 129.69.210.78 \n 3: 129.69.210.1 \n" +
                " 4: 129.69.210.2 \n" +
                " 5: 129.69.210.3 \n");
        userInput = System.console().readLine();

        scanner = new Scanner(System.in);
        System.out.print("\n Do you want to execute the Flooding including a client (true or false? \n");
        clientMode = scanner.nextBoolean();

        switch (userInput) {
            case "1": bcastMsg ="129.69.210.77";
                break;
            case "2": bcastMsg ="129.69.210.78";
                break;
            case "3": bcastMsg ="129.69.210.1";
                break;
            case "4": bcastMsg ="129.69.210.2";
                break;
            case "5": bcastMsg ="129.69.210.3";
                break;
        }

        try {
            receiverSocket = new DatagramSocket(port);
            senderBroadcastSocket = new DatagramSocket();
            senderAcknowledgeSocket = new DatagramSocket();
        } catch (SocketException e)
        {
            e.printStackTrace();
        }

        try {
            receiverSocket.setBroadcast(true);
            senderBroadcastSocket.setBroadcast(true);
            senderAcknowledgeSocket.setBroadcast(false);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        serverThread = new Server(receiverSocket, senderBroadcastSocket, senderAcknowledgeSocket, bcastMsg);
        clientThread = new Client(senderBroadcastSocket, bcastMsg, port);

        serverThread.start();
        if(clientMode)
        {
            clientThread.start();
        }







    }

}
    /*private static DatagramSocket recvSock ;
    private static DatagramSocket ackSock ;
    private static DatagramSocket sendSock;

    private static DatagramPacket recvPacket;
    private static DatagramPacket ackPacket;
    private static DatagramPacket sendPacket;

    private static final int teamNumber = 5;
    private static final int port = 5000 + 10 * teamNumber;

    private static List<DatagramPacket> packets= new ArrayList<DatagramPacket>();
    private static List<String> nodeAddresses = new ArrayList<String>();

    private static String ackMsg = "";
    private static String bcastMsg = "";

    private static Client client;
    private static Server server;

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

        System.out.print("Which Machine? \n 1: 129.69.210.77 \n 2: 129.69.210.78 \n 3: 129.69.210.1 \n" +
                " 4: 129.69.210.2 \n" +
                " 5: 129.69.210.3");
        String input = System.console().readLine();

        switch (input) {
            case "1": bcastMsg ="129.69.210.77";
                break;
            case "2": bcastMsg ="129.69.210.78";
                break;
            case "3": bcastMsg ="129.69.210.1";
                break;
            case "4": bcastMsg ="129.69.210.2";
                break;
            case "5": bcastMsg ="129.69.210.3";
                break;
        }
        sendBuf = bcastMsg.getBytes();
        System.out.println("Message initialized with: "+ bcastMsg);

        sendPacket = new DatagramPacket(sendBuf, sendBuf.length, InetAddress.getByName("192.168.132.255"), port);

        int i = 0;

        while (i < 20)
        {
            System.out.println("Sending UDP message ");
            sendSock.send(sendPacket);

            try {
                Thread.sleep( 1000 );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("Receiving UDP messages ");
            try
            {
                recvBuf = null;
                // Receive a packet
                recvSock.receive(recvPacket);
                packets.add(recvPacket);



               /* String test = new String(recvPacket.getData());
                Collator collator = Collator.getInstance(Locale.US);
                System.out.println("Convert: " + collator.compare(test, bcastMsg));

                //Send a response
                if (0 == collator.compare(test, bcastMsg))
                {
                    System.out.println("Received own broadcast message");
                    // Print received packet data
                    System.out.println("Packet received from: " + recvPacket.getAddress().getHostAddress());
                    System.out.println("Received packet data: " + new String(recvPacket.getData()));
                }
                else
                {
                   System.out.println("Received another broadcast message");
                    // Print received packet data
                    System.out.println("Packet received from: " + recvPacket.getAddress().getHostAddress());
                    System.out.println("Received packet data: " + new String(recvPacket.getData()));
                    ackMsg = "Received your Hello World";
                    ackBuf = ackMsg.getBytes();
                    ackPacket = new DatagramPacket(ackBuf, ackBuf.length, recvPacket.getAddress(), recvPacket.getPort());
                    ackSock.send(ackPacket);
                    System.out.println("Ack sent to: " + ackPacket.getAddress().getHostAddress());
                }

                //nodes.add(receiverPacket.getAddress());*/
         /*   } catch (IOException e) {
                e.printStackTrace();
            }

            i++;
        }
        for (int j = 0; j <= packets.size()-1; j++) {
            System.out.println("Messages: " + new String(packets.get(j).getData()));
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

    }
}*/
