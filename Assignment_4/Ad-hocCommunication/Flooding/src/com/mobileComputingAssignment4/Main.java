package com.mobileComputingAssignment4;

import java.io.IOException;
import java.net.*;

public class Main {

    private static DatagramSocket sock;
    private static DatagramPacket packet;
    private static final int teamNumber = 5;
    private static final int port = 5000 + 10 * teamNumber;
    private static String msg;
    private static byte[] bcast_msg;

    private static boolean initializeSocket(DatagramSocket sock) {
        try {
            sock.setBroadcast(true);
        } catch (SocketException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }



    public static void main(String[] args) throws UnknownHostException {
	// write your code here

        try {
            sock = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        initializeSocket (sock);
        bcast_msg = msg.getBytes();

        msg = "Hello World message";


        packet = new DatagramPacket(bcast_msg, bcast_msg.length, InetAddress.getByName("192.168.132.255"), port);

        try {
            sock.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
