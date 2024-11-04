package app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Blackboard class that focuses solely on data management for click positions and tracking status.
 *
 * @version 1.2
 */
public class Blackboard {
    private static final Logger logger = LoggerFactory.getLogger(Blackboard.class);
    private static Blackboard instance;
    private final List<Point> clickPositions;
    private int transmissionSpeed;
    private boolean tracking;

    private Blackboard() {
        this.clickPositions = new ArrayList<>();
        this.transmissionSpeed = 60;
        this.tracking = false;
    }

    public static synchronized Blackboard getInstance() {
        if (instance == null) {
            instance = new Blackboard();
        }
        return instance;
    }

    public synchronized void addClick(Point click) {
        if (tracking && clickPositions.size() < transmissionSpeed) {
            clickPositions.add(click);
            logger.debug("Click added at position: ({}, {})", click.x, click.y);
        }
    }

    public synchronized ArrayList<Point> getClickPositions() {
        return new ArrayList<>(clickPositions);
    }

    public synchronized void clearClicks() {
        clickPositions.clear();
        logger.debug("All clicks cleared.");
    }

    public synchronized void setTransmissionSpeed(int speed) {
        this.transmissionSpeed = speed;
        logger.debug("Transmission speed set to {}", speed);
    }

    public synchronized boolean isTracking() {
        return tracking;
    }

    public synchronized void startTracking() {
        tracking = true;
        logger.info("Tracking started.");
    }

    public synchronized void stopTracking() {
        tracking = false;
        logger.info("Tracking stopped.");
    }
}
