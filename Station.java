import java.util.HashMap;
import java.util.Map;

public class Station extends Entity {
    private Station(String name) { super(name); }
    private static Map<String, Station> stations = new HashMap<>();

    public static Station make(String name) {
        if (!stations.containsKey(name)) {
            stations.put(name, new Station(name));
        }
        return stations.get(name);
    }
}
