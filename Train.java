import java.util.HashMap;
import java.util.Map;

public class Train extends Entity {
    private Train(String name) { super(name); }
    private static Map<String, Train> trains = new HashMap<>();

    public static Train make(String name) {
        if (!trains.containsKey(name)) {
          trains.put(name, new Train(name));
        }
        return trains.get(name);
    }
}
