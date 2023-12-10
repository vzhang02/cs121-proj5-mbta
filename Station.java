import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Station extends Entity {
    private Station(String name) { super(name); }
    private static Map<String, Station> stations = new HashMap<>();

    public Lock sLock = new ReentrantLock(); 
    public Condition available = sLock.newCondition();

    public static Station make(String name) {
        if (!stations.containsKey(name)) {
            stations.put(name, new Station(name));
        }
        return stations.get(name);
    }

    public void makeAvailable() {
        sLock.lock();
        available.signalAll();
        sLock.unlock();
    }
}
