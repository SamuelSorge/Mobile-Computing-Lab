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
    private static List<InetAddress> nodeAddresses = new ArrayList<InetAddress>();

    public Message(int sequenceNumber, int hopCount, byte messageData[],List<InetAddress> nodes){
        this.sequenceNumber = sequenceNumber;
        this.hopCount = hopCount;
        this.messageData = messageData;
        for(int i=0; i < nodes.size(); i++) {
            this.nodeAddresses.add(i, nodes.get(i));
        }
       // System.out.println(getClass().getName() + "\n Message: " + new String(messageData));
       // System.out.println(getClass().getName() + "\n Message: " + nodeAddresses.get(0));
       // System.out.println(getClass().getName() + "\n Message: " + nodeAddresses.get(1));

        length = 8+messageData.length+(nodeAddresses.size()*4);
    }

    public Message(byte[] packetData) throws UnknownHostException {
  /*      ByteBuffer wrapped = ByteBuffer.wrap(packetData); // big-endian by default
        byte[] temp = new byte[1];
        wrapped.get(temp, 0, 1);
        System.out.println(getClass().getName() + "\n Message byte: " + new String(temp));

        byte[] temp2 = new byte[2];
        wrapped.get(temp2, 0, 2);

       wrapped.get(messageData, 0, messageData.length+2 );


        hopCount = wrapped.getInt();
        System.out.println(getClass().getName() + "\n Message byte: " + new String(temp2));
        wrapped.get(messageData, 16, 29);
        System.out.println(getClass().getName() + "\n Message byte: " + new String(messageData));

        length = packetData.length;*/
        ByteBuffer wrapped = ByteBuffer.wrap(packetData); // big-endian by default
       // messageData = "DISCOVER_NODE_REQUEST".getBytes();
        sequenceNumber = 1;
        hopCount = 0;
        byte[] test = new byte[400];
        byte[] test2 = new byte[400];
        byte[] test3 = new byte[400];
        byte[] test4 = new byte[400];
        byte[] test5 = new byte[400];
        byte[] test6 = new byte[400];

        System.out.println(getClass().getName() + "\n pos: " + wrapped.position());
        wrapped.get(test, 0,1);
        System.out.println(getClass().getName() + "\n Message byte: " + sequenceNumber);
        System.out.println(getClass().getName() + "\n pos: " + wrapped.position());

        wrapped.get(test2,1,1);
        System.out.println(getClass().getName() + "\n Message byte: " + new String(test2));
        System.out.println(getClass().getName() + "\n pos: " + wrapped.position());

        wrapped.get(messageData,2,21);
        System.out.println(getClass().getName() + "\n Message byte: " + new String(test3));
        System.out.println(getClass().getName() + "\n pos: " + wrapped.position());

        wrapped.get(test4,24,14);
        System.out.println(getClass().getName() + "\n Message byte: " + new String(test4));
        System.out.println(getClass().getName() + "\n pos: " + wrapped.position());

        wrapped.get(test5,29,15);
        System.out.println(getClass().getName() + "\n Message byte: " + new String(test5));
        System.out.println(getClass().getName() + "\n pos: " + wrapped.position());

        String s = new String(test4);
        String t = new String(test5);
        String[] neu = s.split("/");
        String[] neu2 = t.split("/");

        nodeAddresses.clear();
        nodeAddresses.add(0, InetAddress.getByName(neu[1]));
        nodeAddresses.add(1,InetAddress.getByName(neu2[1]));

        System.out.println(getClass().getName() + "\n Source: " + nodeAddresses.get(0));
        System.out.println(getClass().getName() + "\n Source: " + nodeAddresses.get(1));

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

    public int getLength(){
       // return 8+messageData.length+(nodeAddresses.size()*4);
        return length;
    }

    public byte[] toByte() {
        byte[] seq = String.valueOf(sequenceNumber).getBytes();
        byte[] hop = String.valueOf(hopCount).getBytes();


        ByteBuffer buffer = ByteBuffer.allocate(400);

        //buffer.putInt(sequenceNumber);
        buffer.put(seq);
        //System.out.println(getClass().getName() + "\n toByte Message: " + new String(buffer.array()));
        //buffer.putInt(hopCount);
        buffer.put(hop);
        //System.out.println(getClass().getName() + "\n toByte Message: " + new String(buffer.array()));
        buffer.put(messageData);
        //System.out.println(getClass().getName() + "\n toByte Message: " + new String(buffer.array()));

        for(int i=0; i < nodeAddresses.size(); i++) {
            byte[] temp = String.valueOf(nodeAddresses.get(i)).getBytes();
            buffer.put(temp);
            //System.out.println(getClass().getName() + "\n toByte Message: " + new String(buffer.array()));
        }

        length = buffer.array().length;

        return buffer.array();
    }

    public InetAddress getTarget(){return nodeAddresses.get(1);}
    public InetAddress getSource(){return nodeAddresses.get(0);}
    public List<InetAddress> getNodes(){return nodeAddresses;}

}
