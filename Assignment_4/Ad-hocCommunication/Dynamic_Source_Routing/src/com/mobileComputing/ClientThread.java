package com.mobileComputing;

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

    private static List<InetAddress> nodeAddresses = new ArrayList<InetAddress>();

    private static String src = "";
    private static String trgt = "";

    public void run(){
        try {
            sock = new DatagramSocket();
            sock.setBroadcast(true);

            nodeAddresses.add(InetAddress.getByName(src));
            nodeAddresses.add(InetAddress.getByName(trgt));

            byte[] sendData = "DISCOVER_NODE_REQUEST".getBytes();

            Message msg = new Message(1,0,sendData, nodeAddresses);
           // System.out.println(getClass().getName() + "\n Message data:" + new String(msg.getMessageData()) + "\n Source: " + msg.getNodes().get(0) + "\n Target: " + msg.getNodes().get(1));

            DatagramPacket sendPacket = new DatagramPacket(msg.toByte(), msg.getLength(), InetAddress.getByName("192.168.132.255"), port);
            sock.send(sendPacket);
            System.out.println(getClass().getName() + "\n Client: Packet sent to: 192.168.132.255");


                       byte[] recvBuf = new byte[500];
            DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);


                sock.receive(receivePacket);
                    //nodeAddresses.add(receivePacket.getAddress().getHostAddress());
                    System.out.println(getClass().getName() + " Broadcast response from: " + receivePacket.getAddress().getHostAddress());

                    //String message = new String(receivePacket.getData()).trim();
                    //if(message.equals("DISCOVER_NODE_RESPONSE")) {
                    Message response = new Message(receivePacket.getData());
                    String message = new String(response.getMessageData()).trim();
                    // System.out.println(getClass().getName() + " Test2"+message);
                    //  if(msg.getMessageData().toString().equals("DISCOVER_NODE_RESPONSE")) {
                    if (message.equals("DISCOVER_NODE_RESPONS")) {
                        System.out.println(getClass().getName() + "Reply reached me, I am source\n");
                        System.out.println(getClass().getName() + " Route: ");
                        if(msg.getHopCount() == 0)
                        {
                            System.out.println(getClass().getName() + msg.getNodes().get(1) + "->" + msg.getNodes().get(0));
                        }
                        else
                        {
                            int hop = msg.getHopCount();
                            while(hop > 0)
                            {
                                System.out.println(getClass().getName()  + msg.getNodes().get(hop+1) + "->");
                                hop--;

                            }
                            System.out.println(getClass().getName() + msg.getNodes().get(1) + "->" + msg.getNodes().get(0));

                        }
                    }

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ClientThread getInstance(String source, String target) {
        src = source;
        trgt = target;

        return ClientThreadHolder.INSTANCE;
    }

    public static class ClientThreadHolder {
        private static final ClientThread INSTANCE = new ClientThread();
    }
}
