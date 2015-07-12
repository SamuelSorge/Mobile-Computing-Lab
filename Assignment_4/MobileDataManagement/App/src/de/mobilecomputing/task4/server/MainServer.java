package de.mobilecomputing.task4.server;

import de.mobilecomputing.task4.client.INodeClient;
import de.mobilecomputing.task4.client.NodeClient;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by SebastianHesse on 07.07.2015.
 */
public class MainServer {

    public static void main(String[] args) {
        if (args != null && args.length == 1) {
            // args: 1) number of client which is used for property file
            final int clientNumber = Integer.parseInt(args[0]);
            INodeServer server;
            try {
                Properties props = System.getProperties();
                props.load(new BufferedInputStream(new FileInputStream("app.properties")));

                int serverPort = Integer.parseInt(props.getProperty("server.port" + clientNumber));
                server = new NodeServer(serverPort);
                server.run();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Something went wrong while initialization.");
            }
        }
    }
}
