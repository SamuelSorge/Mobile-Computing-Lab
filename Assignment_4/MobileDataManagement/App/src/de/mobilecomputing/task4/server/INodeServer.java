package de.mobilecomputing.task4.server;

import de.mobilecomputing.task4.communication.Message;

import java.util.List;

/**
 * Created by SebastianHesse on 06.07.2015.
 */
public interface INodeServer {

    String getServerAddress();

    int getServerPort();

    void run();

    int saveMessages(List<Message> message);

    List<Message> getNewMessages(Message lastMessage);

    List<Message> getAllMessages();
}
