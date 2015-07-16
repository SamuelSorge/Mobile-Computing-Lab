package com.mobileComputingAssignment4;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by Jaasiel on 16.07.2015.
 */
public class Message {

    private int sequenceNumber;
    private int hopCount;
    private byte[] messageData = new byte[100];
    private int length;

    public Message(int sequenceNumber, int hopCount, byte messageData[]){
        this.sequenceNumber = sequenceNumber;
        this.hopCount = hopCount;
        this.messageData = messageData;
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
}
