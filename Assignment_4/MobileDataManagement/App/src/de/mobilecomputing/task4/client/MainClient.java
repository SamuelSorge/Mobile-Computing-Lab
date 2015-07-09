package de.mobilecomputing.task4.client;

import de.mobilecomputing.task4.communication.Pair;
import de.mobilecomputing.task4.communication.Message;

import java.io.*;
import java.util.*;

/**
 * Created by SebastianHesse on 07.07.2015.
 */
public class MainClient {

    private static String clientName;

    private BufferedReader reader;
    private INodeClient client;
    private Map<String, Pair<String, Integer>> network;
    private Message lastMessage = null;

    public MainClient(INodeClient client) {
        this.client = client;
    }

    public void setNetwork(Map<String, Pair<String, Integer>> network) {
        this.network = network;
    }

    public static void main(String[] args) {
        if (args != null && args.length == 1) {
            // args: 1) number of client which is used for property file
            final int clientNumber = Integer.parseInt(args[0]);
            INodeClient client;
            try {
                Properties props = System.getProperties();
                props.load(new BufferedInputStream(new FileInputStream("app.properties")));
                final Map<String, Pair<String, Integer>> network = readNetworkFromProperties(props, clientNumber);

                client = new NodeClient(clientName, network.get(clientName).getA(), network.get(clientName).getB());
                MainClient mainClient = new MainClient(client);
                mainClient.setNetwork(network);
                mainClient.run();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Something went wrong while initialization.");
            }
        }
    }

    public static Map<String, Pair<String, Integer>> readNetworkFromProperties(Properties props, int clientNumber) {
        Map<String, Pair<String, Integer>> network = new HashMap<>();
        int numberOfClients = Integer.parseInt(props.getProperty("client.count"));

        for (int i = 1; i <= numberOfClients; i++) {
            String name = props.getProperty("client.name" + i);
            String serverAddress = props.getProperty("server.address" + i);
            int serverPort = Integer.parseInt(props.getProperty("server.port" + i));
            network.put(name, new Pair<>(serverAddress, serverPort));

            if (i == clientNumber) {
                clientName = name;
            }
        }

        return network;
    }

    public void run() {
        boolean isRunning = true;
        this.reader = new BufferedReader(new InputStreamReader(System.in));

        printHelloMessage();

        while (isRunning) {
            printCommands();
            String consoleInput = this.readLineFromConsole();
            int input = 0;
            try {
                input = Integer.parseInt(consoleInput);
            } catch (NumberFormatException e) {
                continue;
            }

            switch (input) {
                case 1:
                    writeNewMessage();
                    break;
                case 2:
                    showMessagesFromOtherClient();
                    break;
                case 3:
                    saveMessagesOnServer();
                    break;
                case 4:
                    printMessageHistory();
                    break;
                case 5:
                    System.out.println("### Gewaehlt: (5) Programm beenden");
                    isRunning = false;
                    System.out.println("### EXIT ###");
                    break;
                default:
                    System.out.println("### Falsche Eingabe. Bitte erneut versuchen ###");
                    break;
            }
        }

        this.client.close();
    }

    private void printHelloMessage() {
        System.out.println("### CHIRP CLIENT - " + this.client.getName() + " ###");
        System.out.println("### Simulation von Client-Server Data Replication ###");
    }

    private void writeNewMessage() {
        System.out.println("### Gewaehlt: (1) Neue Message schreiben");

        System.out.println("Bitte Message-Text angeben:");
        String text = this.readLineFromConsole();
        if (lastMessage == null) {
            lastMessage = this.client.writeNewMessage(text);
        } else {
            lastMessage = this.client.writeDependentMessage(text, lastMessage);
        }
        System.out.println("Message wurde erstellt: " + lastMessage.toString());
    }

    private void showMessagesFromOtherClient() {
        System.out.println("### Gewaehlt: (2) Nachrichten anderer Clients anzeigen");
        printAvailableClients();
        System.out.println("Bitte geben Sie den Namen des Clients an: ");
        String clientName = this.readLineFromConsole();
        if (this.network.containsKey(clientName)) {
            System.out.println("Nachrichten von " + clientName);
            Pair<String, Integer> clientsServer = this.network.get(clientName);
            List<Message> messages = this.client.getMessagesFromOtherClient(clientsServer.getA(), clientsServer.getB());
            printMessageList(messages);
            lastMessage = messages.get(messages.size() - 1);
        } else {
            System.out.println("Client unbekannt.");
        }
    }

    private void printAvailableClients() {
        System.out.println("Verfuegbare Clients:");
        for (String name : this.network.keySet()) {
            if (!name.equals(clientName)) {
                System.out.print(name + ", ");
            }
        }
        System.out.println();
    }

    private void saveMessagesOnServer() {
        System.out.println("### Gewaehlt: (3) Messages auf Server speichern");
        int savedMessages = this.client.saveNewMessagesOnServer();
        System.out.println("Messages gespeichert: " + savedMessages);
    }

    private void printMessageHistory() {
        System.out.println("### Gewaehlt: (4) Message History ausgeben");
        List<Message> localMessages = this.client.getAllLocalMessages();
        printMessageList(localMessages);
        System.out.println();
    }

    private void printMessageList(List<Message> localMessages) {
        System.out.println("Messages: " + localMessages.size());
        for (Message message : localMessages) {
            System.out.println(message.toString());
        }
    }

    private void printCommands() {
        System.out.println("### Befehle:");
        System.out.println("(1) Neue Message schreiben");
        System.out.println("(2) Nachrichten anderer Clients anzeigen");
        System.out.println("(3) Messages auf Server speichern");
        System.out.println("(4) Message History ausgeben");
        System.out.println("(5) Programm beenden");
    }

    private String readLineFromConsole() {
        try {
            return this.reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
