package de.mobilecomputing.task4.server;

import de.mobilecomputing.task4.communication.Action;
import de.mobilecomputing.task4.communication.Message;
import de.mobilecomputing.task4.communication.Response;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.List;

/**
 * Created by SebastianHesse on 06.07.2015.
 */
public class ClientHandlerThread extends Thread {

    private INodeServer server;
    private Socket client;
    private int port;

    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;

    public ClientHandlerThread(INodeServer server, Socket client) {
        this.server = server;
        this.client = client;
    }

    @Override
    public void run() {
//        try (ServerSocket server = new ServerSocket(this.port)) {
//            System.out.println("### Server started running on port " + this.port);
//            while (!server.isClosed()) {
//                System.out.println("Waiting for Client to connect...");
//                Socket client = server.accept();
//
//                System.out.println("Accepted new client. Delegate to handler thread...");

                handle(client);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void handle(Socket client) {
        try {
            this.outputStream = new ObjectOutputStream(client.getOutputStream());
            this.outputStream.flush();
            this.inputStream = new ObjectInputStream(client.getInputStream());

            while (client.isConnected() && !client.isClosed()) {
                handleClientRequests();
            }

            System.out.println("Client disconnected from handler thread...");
        } catch (Exception e) {
            System.out.println("Error occurred, destroying connection.");
            try {
                this.outputStream.close();
                this.inputStream.close();
                client.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void handleClientRequests() throws IOException, ClassNotFoundException {
        System.out.println("### Waiting for next action...");
        Action action = (Action) this.inputStream.readObject();
        List<Message> messages = action.getMessages();

        switch (action.getActionId()) {
            case SAVE:
                System.out.println("Client wants to save messages...");
                int savedMessages = this.server.saveMessages(messages);
                sendResponse(new Response<Integer>(savedMessages));
                System.out.println("Response was sent back to client...");
                break;
            case GET_ALL:
                System.out.println("Client wants to have all messages...");
                sendResponse(new Response<List<Message>>(this.server.getAllMessages()));
                break;
        }
    }

    private void sendResponse(Response response) {
        try {
            this.outputStream.writeObject(response);
            this.outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
