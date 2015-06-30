package vlr;

/**
 * This class implements the main Visitor Location Register (VLR) Server
 */

import java.awt.geom.Rectangle2D;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

import common.BaseMessage;
import common.Constants;
import common.SearchMessage;
import common.SearchResponseMessage;


public class VLR extends Thread {

    // constant for properties file
    private static final String propfile = "vlr.properties";

    /**
     * Connection to HLR
     */
    public Socket socket = null;
    ObjectInputStream objectInput;
    ObjectOutputStream objectOutput;

    /**
     * Connection to Clients
     */
    public VLRServer vlrServer;

    /**
     * Ip address of HLR
     */
    public String serverip;
    /**
     * Port of HLR
     */
    public int hlrport;
    /**
     * Port to Base Client
     */
    public int vlrport;

    //Location Areas for whom this VLR is responsible
    ArrayList<Rectangle2D> managedLA = null;

    volatile Boolean running = true;

    public Map<String, Rectangle2D> vehicleToLA = new HashMap<>();

    /*
     * Constructor of VLR
     */
    public VLR() {

        try {

            Properties props = System.getProperties();
            props.load(new BufferedInputStream(new FileInputStream(propfile)));

            this.serverip = props.getProperty(Constants.HLRIP);
            this.hlrport = Integer.parseInt(props.getProperty(Constants.PORTHLR));
            this.vlrport = Integer.parseInt(props.getProperty(Constants.PORTVLR));

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        try {

            System.out.println("Running VLR Server");

            //Start Server to accept connection from BaseClientThread
            vlrServer = new VLRServer(this);
            vlrServer.start();

            // connect to HLR (server)
            socket = new Socket(serverip, hlrport);
            objectOutput = new ObjectOutputStream(socket.getOutputStream());
            objectOutput.flush();
            objectInput = new ObjectInputStream(socket.getInputStream());

            // wait for incoming messages from HLR
            while (!Thread.interrupted() && running) {
                //TODO: handle messages from HLR
                try {
                    BaseMessage message = (BaseMessage) objectInput.readObject();
                    if (message instanceof SearchMessage) {
                        handleSearchMessage((SearchMessage) message);
                    } else if (message instanceof SearchResponseMessage) {
                        handleSearchResponseMessage((SearchResponseMessage) message);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            // close socket to HLR
            socket.close();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleSearchMessage(SearchMessage message) throws IOException {
        System.out.println("search message");
        if (vehicleToLA.containsKey(message.targetId)) {
            SearchResponseMessage searchResponseMessage = new SearchResponseMessage();
            searchResponseMessage.targetLA = vehicleToLA.get(message.targetId);
            searchResponseMessage.targetId = message.targetId;
            searchResponseMessage.sourceId = message.sourceId;
            objectOutput.writeObject(searchResponseMessage);
            objectOutput.flush();
        } else {
            System.out.println("Target vehicle not found.");
        }
    }

    private void handleSearchResponseMessage(SearchResponseMessage message) {
        System.out.println("search response");
        this.vlrServer.sendMessageToBaseClient(message);
    }
}
