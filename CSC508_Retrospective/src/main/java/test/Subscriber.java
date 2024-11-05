package test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.awt.*;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

/**
 * Subscriber class that connects to a server and reads a list of click positions from the server.
 *
 * @version 1.2
 */
public class Subscriber {

    private static final Logger logger = LoggerFactory.getLogger(Subscriber.class);

    public static void main(String[] args) {
        Socket socket = null;
        ObjectInputStream objectInputStream = null;
        int maxRetries = 5;
        int retryInterval = 5000;
        int attempts = 0;

        while (attempts < maxRetries) {
            try {
                socket = new Socket("localhost", 12345);
                logger.info("Connected to server.");
                objectInputStream = new ObjectInputStream(socket.getInputStream());
                break;
            } catch (ConnectException e) {
                attempts++;
                logger.warn("Failed to connect to the server. Attempt {}/{}", attempts, maxRetries);
                if (attempts < maxRetries) {
                    logger.info("Retrying in {} seconds...", retryInterval / 1000);
                    try {
                        Thread.sleep(retryInterval);
                    } catch (InterruptedException ie) {
                        logger.error("Retry interrupted.");
                    }
                } else {
                    logger.error("Could not connect to the server after {} attempts. Exiting.", maxRetries);
                    return;
                }
            } catch (IOException e) {
                logger.error("An I/O error occurred: {}", e.getMessage());
                e.printStackTrace();
                return;
            }
        }

        try {
            while (true) {
                try {
                    List<Point> clicks = (List<Point>) objectInputStream.readObject();
                    logger.info("Received clicks from server: {}", clicks);
                } catch (EOFException eof) {
                    logger.info("Server closed connection. Exiting.");
                    break;
                } catch (SocketException se) {
                    logger.error("Connection with the server was reset. Exiting.");
                    break;
                } catch (ClassNotFoundException e) {
                    logger.error("Class not found while reading data from server.");
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            logger.error("Error while communicating with the server: {}", e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (objectInputStream != null) {
                    objectInputStream.close();
                }
                if (socket != null) {
                    socket.close();
                }
                logger.info("Resources closed.");
            } catch (IOException e) {
                logger.error("Error while closing resources: {}", e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
