package de.mobilecomputing.task4.client;

import de.mobilecomputing.task4.communication.Message;

import java.util.List;
import java.util.Map;

/**
 * Created by SebastianHesse on 06.07.2015.
 */
public interface INodeClient {

    String getName();

    Message writeNewMessage(String text);

    int saveNewMessagesOnServer();

    List<Message> getAllLocalMessages();

    List<Message> getMessagesFromOtherClient(String clientName, String serverAddress, int serverPort);

    Map<String, List<Message>> getLocalMessages();

    Map<String, Long> getVersionVector();

    void close();
}
