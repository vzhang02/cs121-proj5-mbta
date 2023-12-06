import java.io.FileReader;
import java.util.*;
import com.google.gson.*;

public class MBTA {
  // Configuration
  private Map<String, List<String>> lines;
  private Map<String, List<String>> trips;

  // State
  private Map<Train, List<Passenger>> currTrainPassengers; // train name, list of passengers
  private Map<Train, String> currTrainLoc;            // train, station name
  private Map<Train, Integer> trainIndex;             // train, index
  private Map<Train, Integer> trainDirection;         // true is forward, false is backward
 
  private Map<Passenger, Integer> currPassLoc;        // where the passenger is in their trip
  private Map<Station, List<Passenger>> currStationPassengers;

  // Configuration class for Gson
  private class Config {
    private Map<String, List<String>> lines;
    private Map<String, List<String>> trips;
  }
  private Config conf = new Config();

  // Creates an initially empty simulation
  public MBTA() {
    lines = new HashMap<>();
    trips = new HashMap<>();

    currTrainPassengers = new HashMap<>();
    currTrainLoc = new HashMap<>();
    trainIndex = new HashMap<>();
    trainDirection = new HashMap<>();

    currPassLoc = new HashMap<>();
  }

  // Adds a new transit line with given name and stations
  public void addLine(String name, List<String> stations) {
    lines.put(name, stations);
  }

  // Adds a new planned journey to the simulation
  public void addJourney(String name, List<String> stations) {
    trips.put(name, stations);
  }

  // Return normally if initial simulation conditions are satisfied, otherwise
  // raises an exception
  public void checkStart() {
    for (String train : lines.keySet()) {
      String start = lines.get(train).get(0);
      if (start != currTrainLoc.get(Train.make(train)).toString() || trainIndex.get(Train.make(train)) != 0) {
        throw new IllegalStateException("Train " + train + " is not at starting station");
      }
      if (!currTrainPassengers.get(Train.make(train)).isEmpty()) { // check if all trains are empty
        throw new IllegalStateException("Train " + train + " is not empty");
      }
    }
    for (String pass : trips.keySet()) {
      //String start = trips.get(pass).get(0);
      // pass. curr loc isn't the 1st station in trip and the station doesn't contain the passenger
      if (currPassLoc.get(Passenger.make(pass)) != 0) {
        throw new IllegalStateException("Passenger " + pass + " is not at starting station");
      }
      if (currStationPassengers.get(Station.make(trips.get(pass).get(0))).contains(Passenger.make(pass))) {
        throw new IllegalStateException("Passenger " + pass + "is not at destination");
      }
    }
  }

  // Return normally if final simulation conditions are satisfied, otherwise
  // raises an exception
  public void checkEnd() {
    for (String pass : trips.keySet()) {
      if (currPassLoc.get(Passenger.make(pass)) != trips.get(pass).size()) {
        throw new IllegalStateException("Passenger " + pass + "is not at destination");
      }
    }
  }

  // reset to an empty simulation
  public void reset() {
    lines.clear();
    trips.clear();
  }

  // adds simulation configuration from a file
  public void loadConfig(String filename) {
    Gson g = new Gson();
    FileReader fr;
    try {
      fr = new FileReader(filename);
      conf = g.fromJson(fr, Config.class); 
      // set up train configuration
      for (String train : conf.lines.keySet()) {
        Train t = Train.make(train);
        List<String> line = new ArrayList<>();
        // for each station in line
        for (String s : conf.lines.get(train)) {
          // make station
          Station stn = Station.make(s);
          // add station to currStationPassengers
          if (!currStationPassengers.containsKey(stn)) {
            currStationPassengers.put(stn, new ArrayList<>());
          }
          // add station name to line
          line.add(s);
        }
        addLine(train, line); // add line to simulation
        trainDirection.put(t, 1); // set starting direction to forward 
        // set starting loc to 1st station in line
        currTrainLoc.put(t, line.get(0)); 
        trainIndex.put(t, 0);
      }

      // set up passenger configuration
      for (String pass : conf.trips.keySet()) {
        Passenger p = Passenger.make(pass); 
        List<String> trip = new ArrayList<>();
        // for each station in trip
        for (String s : conf.trips.get(p)) {
          trip.add(s); // add station to trip
        }
        addJourney(pass, trip); // add trip to simulation
        // set starting location to 1st station in trip
        currPassLoc.put(p, 0);
        // add passenger to starting station
        currStationPassengers.get(Station.make(trip.get(0))).add(p);
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
    
  }
  // get current Station of train
  public Station currTrainLoc(Train t) {
    return Station.make(currTrainLoc.get(t));
  }

  // get next Station in the line of the train
  public Station nexStation(Train t) {
    if (trainDirection.get(t) == 1) { // train moving forward
      if (trainIndex.get(t) == lines.get(t.toString()).size() - 1) { // train is at end of line
        return Station.make(lines.get(t.toString()).get(trainIndex.get(t) - 1));
      }
      return Station.make(lines.get(t.toString()).get(trainIndex.get(t) + 1));
    } else { // train is moving backword
      if (trainIndex.get(t) == 0) { // train is at start of line
        return Station.make(lines.get(t.toString()).get(trainIndex.get(t) + 1));
      }
      return Station.make(lines.get(t.toString()).get(trainIndex.get(t) - 1));
    }
  }

  // set the train index
  public void logMoveEvent(Train t, Station end) {
    Integer dir = trainDirection.get(t);
    int trainIdx = trainIndex.get(t);
    if (dir == 1) {
      if (trainIdx == lines.get(t.toString()).size() - 1) { // if at end of the line
        dir = -1; // flip direction to backward
        trainIdx--; // go backward
      } 
      trainIdx++; // move forward

    } else { // going backward
      if (trainIdx == 0) { // at start of line
        dir = 1; // flip direction to forward
        trainIdx++; // go forward
      }
      trainIdx--; // go backward
    }
    trainDirection.put(t, dir); // put new direction
    trainIndex.put(t, trainIdx); // put new index
    currTrainLoc.put(t, end.toString()); // change curr location
  }

  // check if statio contains passenger
  public boolean stationContainsPassenger(Passenger p, Station s) {
    return currStationPassengers.get(s).contains(p);
  }

  public boolean deboardPassenger(Passenger p, Station s, Train t) {
    int pos = currPassLoc.get(p).intValue();
    String sname = s.toString();
    List<String> trip = trips.get(p.toString());
    String nextS = trip.get(pos+1);  
    if (nextS != sname) {
      for (String train : lines.keySet()) {
        String tname = t.toString();
        if (tname != train) {
          List<String> line = lines.get(tname);
          if (line.contains(nextS)) {
            return true; 
          }
        }
      }
    }
    return (pos == trip.size() - 1);
    
  }

  public boolean trainContainsPassenger(Passenger p, Train t) {
    return currTrainPassengers.get(t).contains(p);
  }

  public void logDeboardEvent(Passenger p, Train t, Station s) {
    int pos = currPassLoc.get(p).intValue();
    currPassLoc.put(p, Integer.valueOf(pos + 1)); // update passenger location
    currTrainPassengers.get(t).remove(p); // remove passenger from the train
    currStationPassengers.get(s).add(p); // add passenger to current station
  }

}

