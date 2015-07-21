package com.mobileComputingAssignment4;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

/**
 * Created by Jaasiel on 16.07.2015.
 */
public class Message {

    private int sequenceNumber;
    private int hopCount;
    private byte[] messageData = new byte[100];
    private int length;
    private InetAddress destinationNode;

	private List<InetAddress> messageNodeList;



	public Message(int sequenceNumber, int hopCount, byte messageData[], InetAddress destinationNode){
        this.sequenceNumber = sequenceNumber;
        this.hopCount = hopCount;
        this.messageData = messageData;
        this.destinationNode = destinationNode;
        length = 8+messageData.length;
    }

    public Message(byte[] packetData){
        ByteBuffer wrapped = ByteBuffer.wrap(packetData); // big-endian by default
        //wrapped.order(ByteOrder.LITTLE_ENDIAN);

        sequenceNumber = wrapped.getInt();
        hopCount = wrapped.getInt();
        wrapped.get(messageData);
        length = packetData.length;
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
        return 8+messageData.length;
    }
    
  

    public byte[] toByte() {
        ByteBuffer buffer = ByteBuffer.allocate(length);

       // buffer.order(ByteOrder.LITTLE_ENDIAN);

        buffer.putInt(sequenceNumber);
        buffer.putInt(hopCount);
        buffer.put(messageData);

        return buffer.array();
    }
    
    //added for DSR
    public void addNodeToList(InetAddress address){
    	
    	messageNodeList.add(address);
    }
    
    public InetAddress getDestinationNode() {
		return destinationNode;
	}

	public void setDestinationNode(InetAddress destinationNode) {
		this.destinationNode = destinationNode;
	}
	
    public List<InetAddress> getMessageNodeList() {
		return messageNodeList;
	}

	public void setMessageNodeList(List<InetAddress> messageNodeList) {
		this.messageNodeList = messageNodeList;
	}
}
