package app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Manages observer registration and notifications for updates.
 */
public class EventManager {
    private static final Logger logger = LoggerFactory.getLogger(EventManager.class);
    private final List<BlackboardObserver> observers;

    public EventManager() {
        this.observers = new CopyOnWriteArrayList<>();
    }

    public void addObserver(BlackboardObserver observer) {
        observers.add(observer);
        logger.debug("Observer added: {}", observer.getClass().getSimpleName());
    }

    public void removeObserver(BlackboardObserver observer) {
        observers.remove(observer);
        logger.debug("Observer removed: {}", observer.getClass().getSimpleName());
    }

    public void notifyObservers() {
        for (BlackboardObserver observer : observers) {
            observer.update();
        }
        logger.debug("Observers notified.");
    }
}
