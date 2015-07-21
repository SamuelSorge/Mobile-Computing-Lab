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
                    saveMessagesOnServer();
                    break;
                case 3:
                    showMessagesFromOtherClient();
                    break;
                case 4:
                    printKnownMessages();
                    break;
                case 5:
                    printLocalMessageHistory();
                    break;
                case 6:
                    System.out.println("### Gewaehlt: (6) Programm beenden");
                    isRunning = false;
                    System.out.println("### EXIT ###");
                    break;
                default:
                    System.out.println("### Falsche Eingabe. Bitte erneut versuchen ###");
                    break;
            }
            System.out.println("###");
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
        Message lastMessage = this.client.writeNewMessage(text);
        System.out.println("Message wurde erstellt: " + lastMessage.toString());
    }

    private void saveMessagesOnServer() {
        System.out.println("### Gewaehlt: (2) Neue Messages auf Server speichern");

        int savedMessages = this.client.saveNewMessagesOnServer();
        System.out.println("Messages gespeichert: " + savedMessages);
    }

    private void showMessagesFromOtherClient() {
        System.out.println("### Gewaehlt: (3) Nachrichten anderer Clients anzeigen");

        printAvailableClients();
        System.out.println("Bitte geben Sie den Namen des Clients an: ");
        String clientName = this.readLineFromConsole();
        if (this.network.containsKey(clientName)) {
            System.out.println("Nachrichten von " + clientName);
            Pair<String, Integer> clientsServer = this.network.get(clientName);
            List<Message> messages = this.client.getMessagesFromOtherClient(clientName, clientsServer.getA(), clientsServer.getB());
            if (messages != null) {
                printMessageList(messages);
            } else {
                System.out.println("No messages to display.");
            }
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

    private void printKnownMessages() {
        System.out.println("### Gewaehlt: (4) Alle bekannten Nachrichten ausgeben");

        printMessages(this.client.getLocalMessages());
    }

    private void printMessages(Map<String, List<Message>> downloadedMessages) {
        System.out.println("Downloaded Messages from " + downloadedMessages.size() + " Clients.");
        List<String> outputData = new ArrayList<>();
        try {
            for (List<Message> messages : downloadedMessages.values()) {
                for (int i = 0; i < messages.size(); i++) {
                    Message message = messages.get(i);
                    if (i == 0) {
                        String firstLine = "Sender: "+message.getSender();
                        outputData.add(firstLine);
                    }
                    String value;
                    if (outputData.size()-1 < i-1) {
                        value = outputData.get(i-1);
                    } else {
                        value = "";
                    }
                    value += String.format("Message %15s, %.20s, %50s", "" + message.getTime(), message.getText(), convertHistoryToString(message.getHistory()));
                    outputData.add(value);
                }
            }

            System.out.println("###");
            for (String outputLine : outputData) {
                System.out.println(outputLine);
            }
        } catch (IllegalStateException e) {
            System.out.println("Can't show messages. Reason: "+e.getMessage());
        }
    }

    private String convertHistoryToString(Map<String, Long> history) throws IllegalStateException {
        String result = "{";
        Map<String, Long> versionVector = this.client.getVersionVector();
        for (String key : history.keySet()) {
            final Long dependentMessageTime = history.get(key);
            if (!versionVector.containsKey(key) || versionVector.containsKey(key) && versionVector.get(key).compareTo(dependentMessageTime) < 0) {
                throw new IllegalStateException("Client doesn't know message from client " + key + " and time "+dependentMessageTime+". Please re-sync.");
            }
            result += key + ":" + dependentMessageTime + ",";
        }
        return result + "}";
    }

    private void printLocalMessageHistory() {
        System.out.println("### Gewaehlt: (5) Lokale Message History ausgeben");

        List<Message> localMessages = this.client.getAllLocalMessages();
        printMessageList(localMessages);
    }

    private void printCommands() {
        System.out.println("### Befehle:");
        System.out.println("(1) Neue Message schreiben");
        System.out.println("(2) Neue Messages auf Server speichern");
        System.out.println("(3) Nachrichten anderer Clients anzeigen");
        System.out.println("(4) Alle bekannten Nachrichten ausgeben");
        System.out.println("(5) Lokale Message History ausgeben");
        System.out.println("(6) Programm beenden");
    }

    private void printMessageList(List<Message> localMessages) {
        System.out.println("Messages: " + localMessages.size());
        for (Message message : localMessages) {
            System.out.println(message.toString());
        }
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
