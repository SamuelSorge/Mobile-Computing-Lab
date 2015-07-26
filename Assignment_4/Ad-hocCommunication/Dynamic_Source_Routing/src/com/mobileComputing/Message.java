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

    public static final String DISCOVER_NODE_REQUEST = "DISCOVER_NODE_REQUEST";
    public static final String DISCOVER_NODE_RESPONS = "DISCOVER_NODE_RESPONS";

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
        byte[] bSequenceNumber = new byte[400];
        byte[] bHopCount = new byte[400];
        byte[] bLengthsOfIpAddresses = new byte[400];
        byte[] bIpAddresses = new byte[400];

        wrapped.get(bSequenceNumber, 0, 1);
        this.sequenceNumber = Integer.parseInt(new String(bSequenceNumber).trim());

        wrapped.get(bHopCount, 1, 1);
        String hopCounter = new String(bHopCount);
        this.hopCount = Integer.parseInt(hopCounter.trim());

        wrapped.get(messageData, 2, 21);
        wrapped.get(bLengthsOfIpAddresses, 24, (2 + this.hopCount) * 3);
        String lengthsAsString = new String(bLengthsOfIpAddresses).trim();
        String[] lengths = lengthsAsString.split(",");
        int totalLength = 0;

        for (String length : lengths) {
            if (!"".equals(length)) {
                totalLength += Integer.valueOf(length.trim());
            }
        }

        wrapped.get(bIpAddresses, 24, 29 + totalLength);
        String s = new String(bIpAddresses);
        String[] neu = s.split("/");

        nodeAddresses.clear();
        nodeAddresses.add(InetAddress.getByName(neu[1]));
        nodeAddresses.add(InetAddress.getByName(neu[2]));
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
