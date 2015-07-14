/**
 * ServerThread class which implements forwarding and response messages
 *
 * @author Jaasiel Walter
 * @version 1.0
  */
package com.mobileComputingAssignment4;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Created by Jaasiel on 14.07.2015.
 */
public class ServerThread implements Runnable {
    DatagramSocket sock;

    // Calculate port number
    final int teamNumber = 5;
    final int port = 5000 + 10 * teamNumber;


    Boolean messageNotYetForwarded = true;

    public void run() {
        try {
            sock = new DatagramSocket(port);
            sock.setBroadcast(true);

            while(true) {
                System.out.println(getClass().getName() + " Receiving broadcast messages!");

                byte[] bcastMsg = new byte[500];
                DatagramPacket packet = new DatagramPacket(bcastMsg, bcastMsg.length);
                sock.receive(packet);

                System.out.println(getClass().getName() + " Message received from: "+ packet.getAddress().getHostAddress());
                System.out.println(getClass().getName() + " Received message data: "+ new String(packet.getData()));

                String message = new String(packet.getData()).trim();
                if (message.equals("DISCOVER_NODE_REQUEST")) {
                    byte[] sendData = "DISCOVER_NODE_RESPONSE".getBytes();

                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
                    sock.send(sendPacket);

                    System.out.println(getClass().getName() + " Packet sent to: " + sendPacket.getAddress().getHostAddress());
                }
                else if (message.equals("DISCOVER_NODE_REQUEST_FORWARDED")){
                    byte[] sendData = "DISCOVER_NODE_RESPONSE_FORWARDED".getBytes();

                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
                    sock.send(sendPacket);

                    System.out.println(getClass().getName() + " Packet sent to: " + sendPacket.getAddress().getHostAddress());
                }

                if (messageNotYetForwarded) {
                    byte[] sendData = "DISCOVER_NODE_REQUEST_FORWARDED".getBytes();

                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("192.168.132.255"), port);
                    sock.send(sendPacket);

                    System.out.println(getClass().getName() + " Packet forwarded to: 192.168.132.255");
                    messageNotYetForwarded = false;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static ServerThread getInstance() {
        return ServerThreadHolder.INSTANCE;
    }

    public static class ServerThreadHolder {
        private static final ServerThread INSTANCE = new ServerThread();
    }
}
