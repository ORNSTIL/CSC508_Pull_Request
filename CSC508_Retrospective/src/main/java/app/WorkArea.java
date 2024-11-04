package app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

/**
 * WorkArea class that observes changes in the Blackboard and updates the UI.
 *
 * @version 1.2
 */
public class WorkArea extends JPanel implements MouseListener, BlackboardObserver {

    public WorkArea(EventManager eventManager) {
        eventManager.addObserver(this);
        addMouseListener(this);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        ArrayList<Point> clickPositions = Blackboard.getInstance().getClickPositions();
        for (Point clickPosition : clickPositions) {
            g.fillOval(clickPosition.x, clickPosition.y, 10, 10);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (Blackboard.getInstance().isTracking()) {
            Point clickPosition = e.getPoint();
            Blackboard.getInstance().addClick(clickPosition);
            repaint();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void update() {
        repaint(); // Refresh the panel when notified of a change
    }
}
