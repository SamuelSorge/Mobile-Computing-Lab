package baseclient;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import common.*;

public class BaseClientThread extends Thread {


    //ip and port of VLR to whom this client connects
    String serverIP = null;
    int port = -1;

    //socket and stream for connection with VLR
    public Socket socket = null;
    ObjectInputStream objectInput;
    ObjectOutputStream objectOutput;

    //set of LAs for whom the connected VLR is responsible
    ArrayList<Rectangle2D> vlrArea = null;

    //unique id of this thread
    int id = -1;

    //parent BaseClient
    BaseClient parent = null;

    public BaseClientThread(int id, String serverIP, int port, ArrayList<Rectangle2D> vlrArea,
                            BaseClient parent) {
        this.serverIP = serverIP;
        this.vlrArea = vlrArea;
        this.parent = parent;
        this.port = port;
        this.id = id;
    }

    @Override
    public boolean equals(Object object) {
        boolean same = false;

        if (object != null && object instanceof BaseClientThread) {
            same = (this.id == ((BaseClientThread) object).id);
        }

        return same;
    }


    @Override
    public void run() {
        // connect to VLR (server)
        try {

            System.out.println("Base Client Thread running");

            // Client connection to VLR
            socket = new Socket(serverIP, port);
            System.out.println("Connection to: "+serverIP+", port: "+port);
            objectOutput = new ObjectOutputStream(socket.getOutputStream());
            objectOutput.flush();
            objectInput = new ObjectInputStream(socket.getInputStream());


            // Initialize VLR with location areas for which the VLR is responsible
            InitializationMsg im = new InitializationMsg();
            im.locationAreas = vlrArea;
            objectOutput.writeObject(im);

            while (!interrupted() && parent.conn != null && !parent.conn.isClosed()) {
                //TODO: handle messages from VLR
                try {
                    BaseMessage message = (BaseMessage) objectInput.readObject();
                    if (message instanceof SearchResponseMessage) {
                        handleSearchResponse((SearchResponseMessage) message);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleSearchResponse(SearchResponseMessage message) {
        System.out.println("search response");
        System.out.println("Vehicle id: "+message.targetId+", position: ["+message.targetLA.getX()+","+message.targetLA.getY()+"]");
    }

    public void setServiceAreas(ArrayList<Rectangle2D> las) {
        this.vlrArea = las;
    }

    public void stopBaseClientThread() {
        //TODO - close connection to VLR ...
        sendMessage(new SimulationCompleteMessage());

        try {
            objectOutput.flush();
            objectOutput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendLocationUpdate(String id, Rectangle2D currentLA, Rectangle2D previousLA) {
        // TODO - update location of vehicle with id, from previousLA to currentLA
        // NOTE: previousLA is null for very first location update
        LocationUpdateMessage message = new LocationUpdateMessage();
        message.id = id;
        message.currentLA = currentLA;
        if (previousLA != null) {
            message.previousLA = previousLA;
        } else {
            message.previousLA = currentLA;
        }
        sendMessage(message);
    }

    public void sendRemoveMessage(String id) {
        // TODO - Inform VLR that vehicle with id has connected to another VLR and thus needs to be removed
        RemoveMessage message = new RemoveMessage();
        message.id = id;
        sendMessage(message);
    }

    public void sendSearch(String sourceId, String targetId) {
        // TODO - initiate a search message
        SearchMessage message = new SearchMessage();
        message.sourceId = sourceId;
        message.targetId = targetId;
        sendMessage(message);
    }

    private void sendMessage(BaseMessage message) {
        try {
            objectOutput.writeObject(message);
            objectOutput.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
