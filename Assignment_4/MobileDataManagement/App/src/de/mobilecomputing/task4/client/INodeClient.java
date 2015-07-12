package de.mobilecomputing.task4.client;

import de.mobilecomputing.task4.communication.Message;

import java.util.List;

/**
 * Created by SebastianHesse on 06.07.2015.
 */
public interface INodeClient {

    String getName();

    Message writeNewMessage(String text);

    Message writeDependentMessage(String text, Message previousMessage);

    int saveNewMessagesOnServer();

    List<Message> getAllLocalMessages();

    List<Message> getMessagesFromOtherClient(String serverAddress, int serverPort);

    void close();
}
