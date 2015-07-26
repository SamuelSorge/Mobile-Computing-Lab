package com.mobileComputing;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jaasiel on 16.07.2015.
 */
public class Message {

    private int sequenceNumber;
    private int hopCount;
    private byte[] messageData = new byte[100];
    private int length;
    private List<InetAddress> nodeAddresses = new ArrayList<InetAddress>();

    public Message(int sequenceNumber, int hopCount, byte messageData[], List<InetAddress> nodes) {
        this.sequenceNumber = sequenceNumber;
        this.hopCount = hopCount;
        this.messageData = messageData;
        for (int i = 0; i < nodes.size(); i++) {
            try {
                nodeAddresses.add(i, InetAddress.getByName(nodes.get(i).getHostAddress()));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        length = 8 + messageData.length + (nodeAddresses.size() * 4);
    }

    public Message(byte[] packetData) throws UnknownHostException {
        ByteBuffer wrapped = ByteBuffer.wrap(packetData); // big-endian by default
        sequenceNumber = 1;
        hopCount = 0;
        byte[] test = new byte[400];
        byte[] test2 = new byte[400];
        byte[] test3 = new byte[400];
        byte[] test4 = new byte[400];

        wrapped.get(test, 0, 1);
        sequenceNumber = Integer.parseInt(new String(test).trim());
        //System.out.println(getClass().getName() + "\n seqnr: " + sequenceNumber);

        wrapped.get(test2, 1, 1);
        String hopcnter = new String(test2);
        hopCount = Integer.parseInt(hopcnter.trim());
        //System.out.println(getClass().getName() + "\n hopcount: " + hopCount);

        wrapped.get(messageData, 2, 21);
       // System.out.println(getClass().getName() + "\n Message byte: " + new String(messageData));

        wrapped.get(test3, 24, (2 + hopCount) * 3);
        final String lengthsAsString = new String(test3).trim();
      //  System.out.println(getClass().getName() + "\n length as string: " + lengthsAsString);

        String[] lengths = lengthsAsString.split(",");
        int totalLength = 0;
        for (String length : lengths) {
            if (!"".equals(length)) {
                totalLength += Integer.valueOf(length.trim());
            }
        }

        //System.out.println(getClass().getName() + "\n Total Length: " + totalLength);

        wrapped.get(test4, 24, 29 + totalLength);
       // System.out.println(getClass().getName() + "\n Message byte: " + new String(test4));

        String s = new String(test4);
        String[] neu = s.split("/");

        //for (String ip : neu) {
         //   System.out.println("Neu: " + ip);
       // }

        //System.out.println("###");

        nodeAddresses.clear();
        nodeAddresses.add(InetAddress.getByName(neu[1]));
        nodeAddresses.add(InetAddress.getByName(neu[2]));

       // System.out.println(getClass().getName() + "\n Source: " + nodeAddresses.get(0).getHostAddress());
       // System.out.println(getClass().getName() + "\n Source: " + nodeAddresses.get(1).getHostAddress());

    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public int getHopCount() {
        return hopCount;
    }

    public byte[] getMessageData() {
        return messageData;
    }

    public int getLength() {
        return length;
    }

    public byte[] toByte() {
        byte[] seq = String.valueOf(sequenceNumber).getBytes();
        byte[] hop = String.valueOf(hopCount).getBytes();


        ByteBuffer buffer = ByteBuffer.allocate(400);

        buffer.put(seq);
        buffer.put(hop);
        buffer.put(messageData);

        byte[][] bigTemp = new byte[nodeAddresses.size()][];
        for (int i = 0; i < nodeAddresses.size(); i++) {
            byte[] temp = String.valueOf(nodeAddresses.get(i)).getBytes();
            buffer.put(String.valueOf(temp.length).getBytes());
            buffer.put(",".getBytes());
            bigTemp[i] = temp;
        }
        for (byte[] temp : bigTemp) {
            buffer.put(temp);
        }

        length = buffer.array().length;

        return buffer.array();
    }

    public InetAddress getTarget() {
        return nodeAddresses.get(1);
    }

    public InetAddress getSource() {
        return nodeAddresses.get(0);
    }

    public List<InetAddress> getNodes() {
        return nodeAddresses;
    }

}
