package com.mobileComputingAssignment4;

import java.io.IOException;
import java.net.*;

/**
 * Created by Jaasiel on 14.07.2015.
 */
public class ClientThread implements Runnable {
    DatagramSocket sock;

    final int teamNumber = 5;
    final int port = 5000 + 10 * teamNumber;

    public void run(){
        try {
            sock = new DatagramSocket();
            sock.setBroadcast(true);

            byte[] sendData = "DISCOVER_NODE_REQUEST".getBytes();

            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("192.168.132.255"), port);
            sock.send(sendPacket);
            System.out.println(getClass().getName() + " Packet sent to: 192.168.132.255");


            byte[] recvBuf = new byte[500];
            DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
            sock.receive(receivePacket);

            System.out.println(getClass().getName() + " Broadcast response from: " + receivePacket.getAddress().getHostAddress());

            String message = new String(receivePacket.getData()).trim();
            if(message.equals("DISCOVER_NODE_RESPONSE")) {
                System.out.println(getClass().getName() + " Response correct!");
            }

            sock.close();
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
}
