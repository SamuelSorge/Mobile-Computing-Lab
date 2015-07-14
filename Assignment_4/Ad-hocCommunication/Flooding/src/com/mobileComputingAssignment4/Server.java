package com.mobileComputingAssignment4;

import sun.rmi.runtime.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by SamuelSorge on 14.07.2015.
 */
public class Server extends Thread
{
    private static DatagramSocket receiverSock;
    private static DatagramSocket senderBroadcastSock;
    private static DatagramSocket senderAcknowledgeSock;

    private static DatagramPacket receivedPacket;
    private static DatagramPacket ackPacket;
    private static DatagramPacket sendPacket;

    private static List<DatagramPacket> receivedPackets= new ArrayList<DatagramPacket>();

    private static byte[] receiveBuf = new byte[100]; // used to store received message data

    private static final Logger log = Logger.getLogger(Server.class.getName());
    private static Handler handler;

    private static final int teamNumber = 5;
    private static final int port = 5000 + 10 * teamNumber;

    int counter = 0;
    String inputBreak="";

    public Server(DatagramSocket receiverSocket, DatagramSocket senderBroadcastSocket, DatagramSocket senderAcknowledgeSocket, String input)
    {
        receiverSock = receiverSocket;
        senderBroadcastSock = senderBroadcastSocket;
        senderAcknowledgeSock = senderAcknowledgeSocket;
        inputBreak = input;
    }

    public void run()
    {
        // Create new receiver packet to store received message data
        receivedPacket = new DatagramPacket(receiveBuf, receiveBuf.length);

        try {

            handler = new FileHandler("logMessageData.txt");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        handler.setLevel(Level.INFO);
        log.addHandler( handler );
        log.info("Beginning");
        Collator collator = Collator.getInstance(Locale.US);
        while(true)
        {
            try {
                log.info("Receiving");
                receiverSock.receive(receivedPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (counter == 0) {
                    log.info("Test"+ receivedPacket.getAddress().getHostAddress());
                    senderBroadcastSock.send(new DatagramPacket(receivedPacket.getData(), receivedPacket.getData().length, InetAddress.getByName("192.168.132.255"), port));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            if(0 == collator.compare(new String(receivedPacket.getData()), "129.69.210.3"))
            {
                counter = 1;
            }
            receivedPackets.add(receivedPacket);
            log.info(new String(receivedPacket.getData()));
            System.out.println("Message received: " + new String(receivedPacket.getData()));
        }

    }

}
