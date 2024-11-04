package app;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class for the eye tracking simulation.
 *
 * @version 1.2
 */
public class Main extends JFrame implements ActionListener, BlackboardObserver {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private Publisher server;
    private Thread serverThread;
    private JMenuItem startMenuItem;
    private JMenuItem stopMenuItem;
    private JMenuItem configureMenuItem;
    private JMenuItem startServerMenuItem;
    private final EventManager eventManager;

    public static void main(String[] args) {
        Main main = new Main();
        main.setTitle("Eye Tracking Simulator");
        main.setSize(800, 600);
        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        main.setVisible(true);
    }

    public Main() {
        this.eventManager = new EventManager();
        eventManager.addObserver(this);

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Options");

        startMenuItem = new JMenuItem("Start");
        stopMenuItem = new JMenuItem("Stop");
        configureMenuItem = new JMenuItem("Configure");
        startServerMenuItem = new JMenuItem("Start Server");

        menu.add(startMenuItem);
        menu.add(stopMenuItem);
        menu.add(configureMenuItem);
        menu.add(startServerMenuItem);
        menuBar.add(menu);
        setJMenuBar(menuBar);

        startMenuItem.addActionListener(this);
        stopMenuItem.addActionListener(this);
        configureMenuItem.addActionListener(this);
        startServerMenuItem.addActionListener(this);

        stopMenuItem.setEnabled(false);
        WorkArea workArea = new WorkArea(eventManager);
        add(workArea);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startServerMenuItem) {
            server = new Publisher(eventManager);
            serverThread = new Thread(server);
            serverThread.start();
            startServerMenuItem.setEnabled(false);
            logger.info("Server started.");
        } else if (e.getSource() == startMenuItem) {
            startTracking();
        } else if (e.getSource() == stopMenuItem) {
            stopTracking();
        } else if (e.getSource() == configureMenuItem) {
            configureSettings();
        }
    }

    private void startTracking() {
        startMenuItem.setEnabled(false);
        stopMenuItem.setEnabled(true);
        configureMenuItem.setEnabled(false);
        Blackboard.getInstance().startTracking();
        server.startTransmission();
        eventManager.notifyObservers();
        logger.info("Tracking started.");
    }

    private void stopTracking() {
        startMenuItem.setEnabled(true);
        stopMenuItem.setEnabled(false);
        configureMenuItem.setEnabled(true);
        Blackboard.getInstance().stopTracking();
        server.stopTransmission();
        eventManager.notifyObservers();
        logger.info("Tracking stopped.");
    }

    private void configureSettings() {
        String widthStr = JOptionPane.showInputDialog(this, "Enter width:", "Configure", JOptionPane.QUESTION_MESSAGE);
        String heightStr = JOptionPane.showInputDialog(this, "Enter height:", "Configure", JOptionPane.QUESTION_MESSAGE);
        String speedStr = JOptionPane.showInputDialog(this, "Enter data transmission speed (frames per second):", "Configure", JOptionPane.QUESTION_MESSAGE);

        int width = Integer.parseInt(widthStr);
        int height = Integer.parseInt(heightStr);
        int speed = Integer.parseInt(speedStr);

        setSize(width, height);
        Blackboard.getInstance().setTransmissionSpeed(speed);
        eventManager.notifyObservers();
        logger.info("Configuration updated: width={}, height={}, transmission speed={}", width, height, speed);
    }

    @Override
    public void update() {
        logger.debug("Main received an update from EventManager.");
    }
}
