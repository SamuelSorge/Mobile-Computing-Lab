package hlr;

import common.*;
import org.apache.log4j.*;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;


public class VLRHandlerThread extends Thread {

    private static Logger logger = Logger.getLogger(VLRHandlerThread.class);

    private Socket socket;
    HLRServer parent;

    ObjectInputStream objectInput = null;
    ObjectOutputStream objectOutput = null;

    ArrayList<Rectangle2D> serviceArea = null;

    volatile Boolean running = true;

    private int msgCounter = 0;

    public VLRHandlerThread(Socket socket, HLRServer hlr) {
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
            this.socket = socket;
            this.parent = hlr;

            objectInput = new ObjectInputStream(this.socket.getInputStream());
            objectOutput = new ObjectOutputStream(this.socket.getOutputStream());
            objectOutput.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        while (running) {
            //TODO: handle messages from VLR
            try {
                BaseMessage message = (BaseMessage) objectInput.readObject();
                if (message instanceof InitializationMsg) {
                    handleInitMessage((InitializationMsg) message);
                } else if (message instanceof SearchMessage) {
                    handleSearchMessage((SearchMessage) message);
                } else if (message instanceof SearchResponseMessage) {
                    handleSearchResponseMessage((SearchResponseMessage) message);
                } else if (message instanceof LocationUpdateMessage) {
                    handleLocationUpdate((LocationUpdateMessage) message);
                } else if (message instanceof SimulationCompleteMessage) {
                    handleSimulationComplete((SimulationCompleteMessage) message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleInitMessage(InitializationMsg initMessage) {
        System.out.println("init message");
        this.serviceArea = initMessage.locationAreas;
    }

    private void handleSearchMessage(SearchMessage searchMessage) throws IOException {
        System.out.println("search request");
        if (this.parent.vehicleToVLR.containsKey(searchMessage.targetId)) {
            VLRHandlerThread handler = this.parent.vehicleToVLR.get(searchMessage.targetId);
            handler.objectOutput.writeObject(searchMessage);
            handler.objectOutput.flush();
            msgCounter++;
        } else {
            System.out.println("Location of target vehicle is unknown.");
        }
    }

    private void handleSearchResponseMessage(SearchResponseMessage message) throws IOException{
        System.out.println("search response");
        if (this.parent.vehicleToVLR.containsKey(message.sourceId)) {
            VLRHandlerThread handler = this.parent.vehicleToVLR.get(message.sourceId);
            handler.objectOutput.writeObject(message);
            handler.objectOutput.flush();
            msgCounter++;
        } else {
            System.out.println("Location of source vehicle is unknown.");
        }
    }

    private void handleLocationUpdate(LocationUpdateMessage updateMessage) {
        System.out.println("location update");
        this.parent.vehicleToVLR.put(updateMessage.id, this);
    }

    private void handleSimulationComplete(SimulationCompleteMessage simulationCompleteMessage) {
        System.out.println("sim complete");
        this.running = false;
        logger.info("VLR Handler Thread: "+msgCounter);
    }


}
