import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Train extends Entity {
    private Train(String name) { super(name); }
    private static Map<String, Train> trains = new HashMap<>();

     public Lock pLock = new ReentrantLock(); 
     public Condition arrived = pLock.newCondition();


    public static Train make(String name) {
        if (!trains.containsKey(name)) {
          trains.put(name, new Train(name));
        }
        return trains.get(name);
    }

    public void hasArrived() {
      pLock.lock();
      arrived.signalAll();
      pLock.unlock();
    }
}
