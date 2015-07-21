package de.mobilecomputing.task4.server;

import de.mobilecomputing.task4.communication.Message;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SebastianHesse on 06.07.2015.
 */
public class NodeServer implements INodeServer {

    private int port;
    private List<Message> messages;

    public NodeServer(int port) {
        this.port = port;
        this.messages = new ArrayList<>();
    }

    @Override
    public void run() {
        try (ServerSocket server = new ServerSocket(this.port)) {
            System.out.println("### Server started running on port " + this.port);
            while (!server.isClosed()) {
                System.out.println("Waiting for Client to connect...");
                Socket client = server.accept();

                System.out.println("Accepted new client. Delegate to handler thread...");

                new ClientHandlerThread(this, client).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getServerAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "127.0.0.1";
        }
    }

    @Override
    public int getServerPort() {
        return this.port;
    }

    @Override
    public int saveMessages(List<Message> newMessages) {
        if (newMessages != null) {
            this.messages.addAll(newMessages);
            System.out.println("Saved " + newMessages.size() + " Messages.");
            return newMessages.size();
        }
        return 0;
    }

    @Override
    public List<Message> getNewMessages(Message lastMessage) {
        int i = this.messages.size() - 1;

        while (i > 0) {
            if (this.messages.get(i).getTime() > lastMessage.getTime()) {
                i--;
            } else {
                break;
            }
        }

        return new ArrayList<>(this.messages.subList(i+1, this.messages.size()));
    }

    @Override
    public List<Message> getAllMessages() {
        return this.messages;
    }

    @Override
    public String toString() {
        return "NodeServer{" +
                "port=" + port +
                '}';
    }
}
