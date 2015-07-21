package de.mobilecomputing.task4.client;

import de.mobilecomputing.task4.communication.Action;
import de.mobilecomputing.task4.communication.ActionId;
import de.mobilecomputing.task4.communication.Message;
import de.mobilecomputing.task4.communication.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by SebastianHesse on 06.07.2015.
 */
public class NodeClient implements INodeClient {

    private Socket client;
    private String name;

    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;

    private int localMessagePointer = 0;

    private Map<String, List<Message>> localMessages = new HashMap<>();

    public NodeClient(String name, String serverAddress, int serverPort) {
        if (serverAddress != null && !serverAddress.isEmpty() && name != null && !name.isEmpty()) {
            this.name = name;
            this.localMessages.put(name, new ArrayList<Message>());

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
        Message newMessage = new Message(this.getName(), getVersionVector(), text);
        List<Message> messages = getLocalMessagesAsList();
        messages.add(newMessage);
        return newMessage;
    }

    @Override
    public int saveNewMessagesOnServer() {
        try {
            final List<Message> localMessageList = getLocalMessagesAsList();
            final int totalMessages = localMessageList.size();
            if (this.localMessagePointer < totalMessages) {
                System.out.println("Send Action to server");
                Action action = new Action(ActionId.SAVE, new ArrayList<>(localMessageList.subList(this.localMessagePointer, totalMessages)));
                this.outputStream.writeObject(action);
                this.outputStream.flush();
                this.localMessagePointer = totalMessages;
                System.out.println("Waiting for response...");
                Response<Integer> savedMessages = (Response<Integer>) this.inputStream.readObject();
                return savedMessages.getResponseObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public List<Message> getAllLocalMessages() {
        return getLocalMessagesAsList();
    }

    @Override
    public List<Message> getMessagesFromOtherClient(String clientName, String serverAddress, int serverPort) {
        if (serverAddress == null || serverAddress.isEmpty()) {
            throw new RuntimeException("Can't connect to empty server address.");
        }
        List<Message> result = null;
        try {
            Socket socket = new Socket(serverAddress, serverPort);
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush();
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

            result = getMessages(clientName, outputStream, inputStream);

            inputStream.close();
            outputStream.close();
            socket.close();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("### ERROR: Connection not possible or interrupted. Please check if client "+clientName+" is available.");
        }
        return result;
    }

    private List<Message> getMessages(String clientName, ObjectOutputStream outputStream, ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        List<Message> result;
        Response<List<Message>> response;
        List<Message> alreadyDownloadedMessages;
        Action actionToSend;

        if (this.localMessages.containsKey(clientName) && (alreadyDownloadedMessages = this.localMessages.get(clientName)).size() > 0) {
            List<Message> lastMessageList = new ArrayList<>();
            lastMessageList.add(alreadyDownloadedMessages.get(alreadyDownloadedMessages.size() - 1));
            actionToSend = new Action(ActionId.GET, lastMessageList);
        } else {
            alreadyDownloadedMessages = new ArrayList<>();
            actionToSend = new Action(ActionId.GET_ALL, null);
        }

        outputStream.writeObject(actionToSend);
        outputStream.flush();
        response = (Response<List<Message>>) inputStream.readObject();
        result = response.getResponseObject();
        alreadyDownloadedMessages.addAll(result);
        this.localMessages.put(clientName, alreadyDownloadedMessages);

        return result;
    }

    @Override
    public Map<String, List<Message>> getLocalMessages() {
        return this.localMessages;
    }

    @Override
    public Map<String, Long> getVersionVector() {
        Map<String, Long> result = new HashMap<>(this.localMessages.size());
        for (String key : this.localMessages.keySet()) {
            final List<Message> messages = this.localMessages.get(key);
            if (messages.size() > 0) {
                result.put(key, messages.get(messages.size()-1).getTime());
            } else {
                result.put(key, 0l);
            }
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

    private List<Message> getLocalMessagesAsList() {
        return this.localMessages.get(this.name);
    }
}
