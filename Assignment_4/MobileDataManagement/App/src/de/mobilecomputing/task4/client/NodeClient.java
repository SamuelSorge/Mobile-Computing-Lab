package de.mobilecomputing.task4.client;

import de.mobilecomputing.task4.communication.Pair;
import de.mobilecomputing.task4.communication.Action;
import de.mobilecomputing.task4.communication.ActionId;
import de.mobilecomputing.task4.communication.Message;
import de.mobilecomputing.task4.communication.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SebastianHesse on 06.07.2015.
 */
public class NodeClient implements INodeClient {

    private Socket client;
    private String name;

    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;

    private List<Message> localMessages;
    private int localMessagePointer = 0;

    public NodeClient(String name, String serverAddress, int serverPort) {
        if (serverAddress != null && !serverAddress.isEmpty() && name != null && !name.isEmpty()) {
            this.name = name;
            this.localMessages = new ArrayList<>();

            setupServerConnection(serverAddress, serverPort);
        } else {
            throw new IllegalArgumentException("Server and name may not be null or empty.");
        }
    }

    private void setupServerConnection(String serverAddress, int serverPort) {
        try {
            System.out.println("Trying to setup connection to server...");
            this.client = new Socket(serverAddress, serverPort);
            this.outputStream = new ObjectOutputStream(this.client.getOutputStream());
            this.outputStream.flush();
            this.inputStream = new ObjectInputStream(this.client.getInputStream());
        } catch (IOException e) {
            throw new IllegalStateException("No connection to server possible. Please retry.");
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Message writeNewMessage(String text) {
        // TODO: differentiate between first message and "normal" one
        Message newMessage = new Message(this.getName(), text);
        this.localMessages.add(newMessage);
        return newMessage;
    }

    @Override
    public Message writeDependentMessage(String text, Message previousMessage) {
        Message messageToRespond = null;
        if (!previousMessage.getSender().equals(this.getName())) {
            messageToRespond = previousMessage;
        } else {
            messageToRespond = this.localMessages.get(this.localMessages.size() - 1);
        }

        Message newMessage = new Message(this.getName(), new Pair<>(previousMessage.getSender(), messageToRespond), text);
        this.localMessages.add(newMessage);
        return newMessage;
    }

    @Override
    public int saveNewMessagesOnServer() {
        try {
            final int totalMessages = this.localMessages.size();
            if (this.localMessagePointer < totalMessages) {
                System.out.println("Send Action to server");
                Action action = new Action(ActionId.SAVE, new ArrayList<>(this.localMessages.subList(this.localMessagePointer, totalMessages)));
                this.outputStream.writeObject(action);
                this.outputStream.flush();
                this.localMessagePointer = totalMessages;
                // TODO: check response if every message has been saved or send/receive ACK for each
                System.out.println("Waiting for response...");
                Response<Integer> savedMessages = (Response<Integer>) this.inputStream.readObject();
                return savedMessages.getResponseObject();
            }
        } catch (IOException|ClassNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public List<Message> getAllLocalMessages() {
        return this.localMessages;
    }

    @Override
    public List<Message> getMessagesFromOtherClient(String serverAddress, int serverPort) {
        if (serverAddress == null || serverAddress.isEmpty()) {
            throw new RuntimeException("Can't connect to empty server address.");
        }
        List<Message> result = null;
        try {
            Socket socket = new Socket(serverAddress, serverPort);
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush();
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

            outputStream.writeObject(new Action(ActionId.GET_ALL, null));
            outputStream.flush();
            Response<List<Message>> response = (Response<List<Message>>) inputStream.readObject();
            result = response.getResponseObject();

            inputStream.close();
            outputStream.close();
            socket.close();
        } catch (IOException|ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void close() {
        try {
            this.inputStream.close();
            this.outputStream.close();
            this.client.close();
        } catch (IOException e) {
            System.out.println("Error occurred while closing streams and socket.");
        }
    }

    @Override
    public String toString() {
        return "NodeClient{" +
                "name='" + name + '\'' +
                '}';
    }
}
