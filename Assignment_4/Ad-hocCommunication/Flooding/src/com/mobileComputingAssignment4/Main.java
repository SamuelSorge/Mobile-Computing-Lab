package com.mobileComputingAssignment4;

import java.io.IOException;
import java.net.*;

public class Main {

    private static DatagramSocket senderSock;
    private static DatagramSocket receiverSock;
    private static DatagramPacket senderPacket;
    private static DatagramPacket receiverPacket;
    private static final int teamNumber = 5;
    private static final int port = 5000 + 10 * teamNumber;
    private static String msg;

    private static int i = 1;

    private static boolean initializeSocket(DatagramSocket sock) {
        try {
            sock.setBroadcast(true);
        } catch (SocketException e) {
            e.printStackTrace();
            return false;
        }
        System.out.println("Sender socket initialized");
        return true;
    }



    public static void main(String[] args) throws UnknownHostException {
	// write your code here
        byte[] bcast_msg;
        byte[] buf = new byte[100];

        try {
            senderSock = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        initializeSocket (senderSock);
        try {
            receiverSock = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }



        msg = "Hello World message";

        bcast_msg = msg.getBytes();

        senderPacket = new DatagramPacket(bcast_msg, bcast_msg.length, InetAddress.getByName("192.168.132.255"), port);
        receiverPacket = new DatagramPacket(buf, buf.length);
        try {
            senderSock.send(senderPacket);
            System.out.println("Message sent");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep( 1000 );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while (true)
        {
            System.out.println("Receiving message " + i);
            try {
                receiverSock.receive(receiverPacket);
                System.out.print(receiverPacket.getData());

            } catch (IOException e) {
                e.printStackTrace();
            }
            i++;

        }

    }
}
