package vlr;

import common.*;
import org.apache.log4j.*;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class VLRServer extends Thread {

    private static Logger logger = Logger.getLogger(VLRServer.class);

    private ServerSocket s;
    private volatile Boolean running = true;
    private ObjectInputStream objectInput;
    private ObjectOutputStream objectOutput;
    private VLR parent;

    VLRServer(VLR parent) {
        this.parent = parent;
    }

    @Override
    public void run() {
        try {
            SimpleLayout layout = new SimpleLayout();
            ConsoleAppender consoleAppender = new ConsoleAppender( layout );
            logger.addAppender( consoleAppender );
            FileAppender fileAppender = new FileAppender( layout, "logs/task3.log", true );
            logger.addAppender( fileAppender );
            // ALL | DEBUG | INFO | WARN | ERROR | FATAL | OFF:
            logger.setLevel( Level.INFO );
        } catch( Exception ex ) {
            System.out.println( ex );
        }

        try {

            // Wait on connection from base client
            s = new ServerSocket(parent.vlrport);
            Socket clientConnection = s.accept();

            objectOutput = new ObjectOutputStream(clientConnection.getOutputStream());
            objectOutput.flush();
            objectInput = new ObjectInputStream(clientConnection.getInputStream());

            while (running) {
                //TODO: Handle Messages from base client thread
                try {
                    BaseMessage message = (BaseMessage) objectInput.readObject();
                    if (message instanceof InitializationMsg) {
                        handleInitMessage((InitializationMsg) message);
                    } else if (message instanceof SearchMessage) {
                        handleSearchMessage((SearchMessage) message);
                    } else if (message instanceof SimulationCompleteMessage) {
                        handleSimulationComplete((SimulationCompleteMessage) message);
                    } else if (message instanceof LocationUpdateMessage) {
                        handleLocationUpdate((LocationUpdateMessage) message);
                    } else if (message instanceof RemoveMessage) {
                        handleRemoveMessage((RemoveMessage) message);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void handleInitMessage(InitializationMsg message) throws IOException {
        System.out.println("init msg");
        this.parent.managedLA = message.locationAreas;
        this.parent.objectOutput.writeObject(message);
        this.parent.objectOutput.flush();
        this.parent.msgCounter.addAndGet(1);
    }

    private void handleSearchMessage(SearchMessage searchMessage) {
        System.out.println("search msg");
        if (this.parent.vehicleToLA.containsKey(searchMessage.targetId)) {
            Rectangle2D locationArea = this.parent.vehicleToLA.get(searchMessage.targetId);

            SearchResponseMessage searchResponseMessage = new SearchResponseMessage();
            searchResponseMessage.targetLA = locationArea;
            searchResponseMessage.sourceId = searchMessage.sourceId;
            searchResponseMessage.targetId = searchMessage.targetId;
            searchResponseMessage.time = searchMessage.time;

            sendMessage(searchResponseMessage);
        }
    }

    private void handleSimulationComplete(SimulationCompleteMessage message) throws IOException {
        System.out.println("sim complete");
        running = false;

        // propagate to HLR
        this.parent.objectOutput.writeObject(message);
        this.parent.objectOutput.flush();
        this.parent.msgCounter.addAndGet(1);

        logger.info("VLR Server: "+this.parent.msgCounter.get());
    }

    private void handleLocationUpdate(LocationUpdateMessage message) throws IOException {
        System.out.println("location update");
        this.parent.vehicleToLA.put(message.id, message.currentLA);

        // only propagate to HLR if vehicle changed location area (LA) or it is the initial message
        if (!this.parent.managedLA.contains(message.previousLA) || message.previousLA == message.currentLA) {
            this.parent.objectOutput.writeObject(message);
            this.parent.objectOutput.flush();
            this.parent.msgCounter.addAndGet(1);
        }
    }

    private void handleRemoveMessage(RemoveMessage message) {
        System.out.println("remove msg");
        this.parent.vehicleToLA.remove(message.id);
    }

    public void sendMessageToBaseClient(BaseMessage message) {
        sendMessage(message);
    }


    private void sendMessage(BaseMessage message) {
        try {
            objectOutput.writeObject(message);
            objectOutput.flush();
            this.parent.msgCounter.addAndGet(1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
