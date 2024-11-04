package app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

/**
 * Publisher class that listens for incoming connections and handles data transmission to connected clients.
 *
 * @version 1.2
 */
public class Publisher implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(Publisher.class);
    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private boolean isSendingData;
    private final EventManager eventManager;

    public Publisher(EventManager eventManager) {
        this.eventManager = eventManager;
        try {
            ServerSocket serverSocket = new ServerSocket(12345);
            logger.info("Publisher is waiting for client connection...");
            socket = serverSocket.accept();
            logger.info("Client connected.");
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            logger.error("Error initializing Publisher: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        logger.info("Publisher is running...");
        while (true) {
            if (isSendingData) {
                sendClickData();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.error("Thread interrupted: {}", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void startTransmission() {
        logger.info("Starting data transmission...");
        isSendingData = true;
    }

    public void stopTransmission() {
        logger.info("Stopping data transmission...");
        isSendingData = false;
    }

    private void sendClickData() {
        List<Point> clicks = Blackboard.getInstance().getClickPositions();
        if (!clicks.isEmpty()) {
            try {
                objectOutputStream.writeObject(clicks);
                objectOutputStream.flush();
                Blackboard.getInstance().clearClicks();
                logger.debug("Sent clicks to client.");
            } catch (IOException e) {
                logger.error("Error during data transmission: {}", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void stopServer() {
        stopTransmission();
        try {
            if (socket != null) {
                objectOutputStream.close();
                socket.close();
            }
            logger.info("Server stopped.");
        } catch (IOException e) {
            logger.error("Error while stopping server: {}", e.getMessage());
            e.printStackTrace();
        }
    }
}
