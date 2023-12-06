import java.util.*;

public class DeboardEvent implements Event {
  public final Passenger p; public final Train t; public final Station s;
  public DeboardEvent(Passenger p, Train t, Station s) {
    this.p = p; this.t = t; this.s = s;
  }
  public boolean equals(Object o) {
    if (o instanceof DeboardEvent e) {
      return p.equals(e.p) && t.equals(e.t) && s.equals(e.s);
    }
    return false;
  }
  public int hashCode() {
    return Objects.hash(p, t, s);
  }
  public String toString() {
    return "Passenger " + p + " deboards " + t + " at " + s;
  }
  public List<String> toStringList() {
    return List.of(p.toString(), t.toString(), s.toString());
  }

  public void replayAndCheck(MBTA mbta) {
    // check that train, passenger, and station exist in the simulation
    if (!mbta.hasTrain(t) || !mbta.hasPassenger(p) || !mbta.hasStation(s)) {
      throw new IllegalStateException("Simulation does not have specified train, station, or passenger");
    }
    // check that station does not contain passenger already
    if (mbta.stationContainsPassenger(p, s)) {
      throw new IllegalStateException("Passenger " + p.toString() + " isn't at station " + s.toString());
    }
    // check that train is at current station
    if (!mbta.atStation(s, t)) {
      throw new IllegalStateException(s.toString() + " is not the current location of train " + t.toString());
    }
    // check that train contains passenger
    if (!mbta.trainContainsPassenger(p, t)) {
      throw new IllegalStateException("Train " + t.toString() + " does not contain passenger " + p.toString());
    }
    // check if passenger should deboard here
    if (!mbta.deboardPassenger(p, s, t)) {
      throw new IllegalStateException("Passenger " + p.toString() + " cannot deboard here: " + s.toString());
    }
    mbta.logDeboardEvent(p, t, s); 
  }
}
