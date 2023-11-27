import java.util.HashMap;
import java.util.Map;

public class Passenger extends Entity {
    private Passenger(String name) { super(name); }

    private static Map<String, Passenger> passengers = new HashMap<>();

    public static Passenger make(String name) {
        if (!passengers.containsKey(name)) {
          passengers.put(name, new Passenger(name));  
        }
        return passengers.get(name);
    }
}
