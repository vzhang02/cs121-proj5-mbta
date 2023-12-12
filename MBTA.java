import java.io.FileReader;
import java.util.*;

import com.google.gson.*;

public class MBTA {
  // Configuration
  private Map<Train, List<String>> lines;
  private Map<Passenger, List<String>> trips;

  // State
  private Map<Train, List<Passenger>> currTrainPassengers; // train name, list of passengers
  private Map<Train, String> currTrainLoc;            // train, station name
  private Map<Train, Integer> trainIndex;             // train, index
  private Map<Train, Integer> trainDirection;         // true is forward, false is backward
 
  private Map<Passenger, Integer> currPassLoc;        // where the passenger is in their trip
  private Map<Station, List<Passenger>> currStationPassengers;
  private Map<Station, Train> stationTrain;



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
    currStationPassengers = new HashMap<>();
    stationTrain = new HashMap<>();
  }

  // Adds a new transit line with given name and stations
  public void addLine(String name, List<String> stations) {
    Train t = Train.make(name); // make train
    List<String> line = new ArrayList<>(); // initialize list to put in
    for (String s : stations) { // for each station in line
      Station stn = Station.make(s);  // make station
      // add station to currStationPassengers
      if (!currStationPassengers.containsKey(stn)) {
        currStationPassengers.put(stn, new ArrayList<>());
      }
      line.add(s); // add station name to line
    }
    Station start = Station.make(line.get(0));
    lines.put(t, line); // add line
    trainDirection.put(t, 1); // set starting direction to forward 
    // set starting loc to 1st station in line
    currTrainLoc.put(t, line.get(0)); 
    trainIndex.put(t, 0);
    currTrainPassengers.put(t, new ArrayList<>());
    stationTrain.put(start, t);
  }

  // Adds a new planned journey to the simulation
  public void addJourney(String name, List<String> stations) {
    Passenger p = Passenger.make(name); 
    List<String> trip = new ArrayList<>();
    // for each station in trip
    for (String s : stations) {
      trip.add(s); // add station to trip
    }
    trips.put(p, trip);
    // set starting location to 1st station in trip5
    currPassLoc.put(p, 0); 
    Station start = Station.make(trip.get(0)); 
    // add passenger to starting station
    if (!currStationPassengers.containsKey(start)) {
        currStationPassengers.put(start, new ArrayList<>());
    }
    currStationPassengers.get(start).add(p); 
    System.out.println("Adding passenger " + p.toString() + " to Station " + start.toString());
  }

  // Return normally if initial simulation conditions are satisfied, otherwise
  // raises an exception
  public void checkStart() {
    for (Train train : lines.keySet()) {
      String start = lines.get(train).get(0);
      if (start != currTrainLoc.get(train).toString() || trainIndex.get(train) != 0) {
        throw new IllegalStateException("Train " + train + " is not at starting station");
      }
      if (!currTrainPassengers.get(train).isEmpty()) { // check if all trains are empty
        throw new IllegalStateException("Train " + train + " is not empty");
      }
    }
    for (Passenger pass : trips.keySet()) {
      String start = trips.get(pass).get(0);
      // pass. curr loc isn't the 1st station in trip and the station doesn't contain the passenger
      if (currPassLoc.get(pass) != 0) {
        throw new IllegalStateException("Passenger " + pass + " is not at starting station");
      }
      if (!currStationPassengers.get(Station.make(start)).contains(pass)) {
        throw new IllegalStateException("Passenger " + pass + " is not at start");
      }
    }
  }

  // Return normally if final simulation conditions are satisfied, otherwise
  // raises an exception
  public void checkEnd() {
    for (Passenger pass : trips.keySet()) {
      int tripLength = trips.get(pass).size(); 
      Station dest = Station.make(trips.get(pass).get(tripLength - 1)); 
      if (currPassLoc.get(pass) != (tripLength - 1)
         && !currStationPassengers.get(dest).contains(pass)) {
        throw new IllegalStateException("Passenger " + pass + " is not at destination");
      }
    }
  }

  // reset to an empty simulation
  public void reset() {
    lines.clear();
    trips.clear();

    currTrainPassengers.clear(); 
    currTrainLoc.clear();          
    trainIndex.clear();             
    trainDirection.clear();
    
    currPassLoc.clear(); 
    currStationPassengers.clear();
    stationTrain.clear();
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
        addLine(train, conf.lines.get(train));
      }
      // set up passenger configuration
      for (String pass : conf.trips.keySet()) {
        addJourney(pass, conf.trips.get(pass));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // returns if passenger exists in the simulation
  public boolean hasPassenger(Passenger p) {
    return trips.containsKey(p);
  }

  // returns if train exists in the simulation
  public boolean hasTrain(Train t) {
    return lines.containsKey(t);
  }

  // returns if train exists in the simulation
  public boolean hasStation(Station s) {
    return currStationPassengers.containsKey(s);
  }

  // checks if train is at station
  public boolean atStation(Station s, Train t) {
    return (s.toString() == currTrainLoc.get(t));
  }
  
  // checks if station is available
  public boolean stationOpen(Station s) {
    for (Train train : currTrainLoc.keySet()) {
      if (currTrainLoc.get(train) == s.toString()) {
        return false;
      }
    }
    return true; 
  }

  // get current Station of train
  public Station currTrainLoc(Train t) {
    return Station.make(currTrainLoc.get(t));
  }

   // check if statio contains passenger
  public boolean stationContainsPassenger(Passenger p, Station s) {
    return currStationPassengers.get(s).contains(p);
  }

  // checks that train contains passenger
  public boolean trainContainsPassenger(Passenger p, Train t) {
    return currTrainPassengers.get(t).contains(p);
  }

  // check if the passenger gets off here
  public boolean deboardPassenger(Passenger p, Station s, Train t) {
    int pos = currPassLoc.get(p).intValue();
    String sname = s.toString(); // station that event wants to deboard to(curr station)
    List<String> trip = trips.get(p); // get passenger's trip
    if (pos >= trip.size() - 1) {
      return false;
    }
    String nextS = trip.get(pos+1); // next station in the trip
    if (sname == nextS) {
      return true;
    } else {
      for (Train train : lines.keySet()) {
        if (t != train) {
          List<String> line = lines.get(t);
          if (line.contains(sname) && line.contains(nextS)) {
            return true; 
          }
        }
      }
    }
    return false;
  }

  //check if the passenger get on train
  public boolean boardPassenger(Passenger p, Train t, Station s) {
    int pos = currPassLoc.get(p).intValue(); // get passengers current position
    String sname = s.toString(); 
    List<String> trip = trips.get(p); // get passengers trip
    if (pos >= trip.size() - 1) {
      return false;
    }
    List<String> line = lines.get(t); // get train line
    String nextS = trip.get(pos + 1); // get next station
    // check that the line has the current station and the next station
    return line.contains(sname) && line.contains(nextS); 
  }

  // get next Station in the line of the train
  public Station nextTrainStation(Train t) {
    if (trainDirection.get(t) == 1) { // train moving forward
      if (trainIndex.get(t) == lines.get(t).size() - 1) { // train is at end of line
        return Station.make(lines.get(t).get(trainIndex.get(t) - 1));
      }
      return Station.make(lines.get(t).get(trainIndex.get(t) + 1));
    } else { // train is moving backword
      if (trainIndex.get(t) == 0) { // train is at start of line
        return Station.make(lines.get(t).get(trainIndex.get(t) + 1));
      }
      return Station.make(lines.get(t).get(trainIndex.get(t) - 1));
    }
  }


  // move train
  public void logMoveEvent(Train t, Station end) {
    Integer dir = trainDirection.get(t);
    int trainIdx = trainIndex.get(t);
    if (dir == 1) {
      if (trainIdx == lines.get(t).size() - 1) { // if at end of the line
        dir = -1; // flip direction to backward
      } 
    } else { // going backward
      if (trainIdx == 0) { // at start of line
        dir = 1; // flip direction to forward
      }
    }
    trainIdx += dir;
    trainDirection.put(t, dir); // put new direction
    trainIndex.put(t, trainIdx); // put new index
    currTrainLoc.put(t, end.toString()); // change curr location

  }

  // deboard passenger
  public void logDeboardEvent(Passenger p, Train t, Station s) {
    int pos = currPassLoc.get(p).intValue();
    currPassLoc.put(p, Integer.valueOf(pos + 1)); // update passenger location
    currTrainPassengers.get(t).remove(p); // remove passenger from the train
    currStationPassengers.get(s).add(p); // add passenger to current station
  }

  // board passenger
  public void logBoardEvent(Passenger p, Train t, Station s) {
    currTrainPassengers.get(t).add(p); // add passenger from the train
    currStationPassengers.get(s).remove(p); // remove passenger from current station
  }

  public boolean atStart() {
    try {
      checkStart();
    } catch (Exception e) {
      return false;
    }
    return true;
  }
  // check if simulation is complete(all passengers have reached destinations)
  public boolean isFinished() {
    try {
      checkEnd();
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  // get destination station of passenger
  public Station getDestination(Passenger p) {
    List<String> trip = trips.get(p);
    return Station.make(trips.get(p).get(trip.size()-1));
  }

  // check if passenger is at destination
  public boolean atDestination(Passenger p) {
    Station dest = getDestination(p);
    
    return (stationContainsPassenger(p, dest)); 
  }

  public boolean isAvailable(Station s) {
    return stationTrain.get(s) == null;
  }

  public void setStationTrain(Station s, Train t) {
    stationTrain.put(s, t);
  }

  public List<Train> getTrains() {
    return new ArrayList<>(lines.keySet());
  }
  
  public List<Passenger> getPassengers() {
    return new ArrayList<>(trips.keySet());
  }

  public Station getPassStation(Passenger p, int index) {
    return Station.make(trips.get(p).get(index));
  }

  public Train getStationTrain(Station s) {
    return stationTrain.get(s);
  }

  public Train toBoard(Passenger p) {
    Station curr = Station.make(trips.get(p).get(currPassLoc.get(p)));
    Station next = Station.make(trips.get(p).get(currPassLoc.get(p) + 1));
    for (Train t : lines.keySet()) {
      List<String> line = lines.get(t);
      if (line.contains(curr.toString()) && line.contains(next.toString())) {
        return t;
      }
    }
    return null;
  }

  public int getPassLoc(Passenger p) {
    return currPassLoc.get(p);
  }

  // public boolean checkMove(Train t, Station s1, Station s2) {
  //   if (!hasTrain(t) || !hasStation(s1) || !hasStation(s2)) {
  //     throw new IllegalStateException("Simulation does not have specified train or stations");
  //   }

  //   List<String> line = lines.get(t);
  //   if (!(line.contains(s1.toString()) && line.contains(s2.toString()))) {
  //     throw new RuntimeException(" ");
  //   }
  //   if (s1 != s1 && s2 != s2) {
  //     throw new IllegalStateException("This move [" + toString() + "] is not a valid event"); 
  //   }
  //   if (!mbta.stationOpen(s2)) {
  //     throw new IllegalStateException("Station " + s2.toString() +" is not available");
  //   }
  // }
 
}

