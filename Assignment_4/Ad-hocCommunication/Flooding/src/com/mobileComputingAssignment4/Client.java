package com.mobileComputingAssignment4;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Created by SamuelSorge on 14.07.2015.
 */
public class Client extends Thread {

    private static DatagramSocket senderBroadcastSock;
    private static DatagramPacket sendPacket;
    private static String msg ="";
    private static int port;

    public Client(DatagramSocket senderBroadcastSocket, String bcastMsg, int portNr)
    {
        senderBroadcastSock = senderBroadcastSocket;
        msg = bcastMsg;
        port = portNr;

    }
    public void run() {
        byte[] sendBuf = new byte[100];
        sendBuf = msg.getBytes();

        try {
            sendPacket = new DatagramPacket(sendBuf, sendBuf.length, InetAddress.getByName("192.168.132.255"), port);
        } catch
                (IOException e) {
            e.printStackTrace();
        }


            try {
                senderBroadcastSock.send(sendPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }

    }
}
