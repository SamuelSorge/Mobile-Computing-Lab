package com.mobileComputingAssignment4;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jaasiel on 14.07.2015.
 */
public class ClientThread implements Runnable {
    DatagramSocket sock;

    final int teamNumber = 5;
    final int port = 5000 + 10 * teamNumber;

    private static List<String> nodeAddresses = new ArrayList<>();

    public void run(){
        try {
            sock = new DatagramSocket();
            sock.setBroadcast(true);

            byte[] sendData = "DISCOVER_NODE_REQUEST".getBytes();
            Message msg = new Message(1,0,sendData);
            System.out.println(getClass().getName() + " SequenceNumber: " + msg.getSequenceNumber() + ", HopCount: " + msg.getHopCount() + (", messageData: " + new String(msg.getMessageData())));

            //DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("192.168.132.255"), port);
            DatagramPacket sendPacket = new DatagramPacket(msg.toByte(), msg.getLength(), InetAddress.getByName("192.168.132.255"), port);
            sock.send(sendPacket);
            System.out.println(getClass().getName() + "\n Packet sent to: 192.168.132.255");


            byte[] recvBuf = new byte[500];
            DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);


                sock.receive(receivePacket);
                    nodeAddresses.add(receivePacket.getAddress().getHostAddress());
                    System.out.println(getClass().getName() + " Broadcast response from: " + receivePacket.getAddress().getHostAddress());

                    //String message = new String(receivePacket.getData()).trim();
                    //if(message.equals("DISCOVER_NODE_RESPONSE")) {
                    Message response = new Message(receivePacket.getData());
                    String message = new String(response.getMessageData()).trim();
                    // System.out.println(getClass().getName() + " Test2"+message);
                    //  if(msg.getMessageData().toString().equals("DISCOVER_NODE_RESPONSE")) {
                    if (message.equals("DISCOVER_NODE_RESPONSE")) {
                        System.out.println(getClass().getName() + " Response correct!");
                    }



           // sock.close();
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
