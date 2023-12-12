import java.util.*;

public class BoardEvent implements Event {
  public final Passenger p; public final Train t; public final Station s;
  public BoardEvent(Passenger p, Train t, Station s) {
    this.p = p; this.t = t; this.s = s;
  }
  public boolean equals(Object o) {
    if (o instanceof BoardEvent e) {
      return p.equals(e.p) && t.equals(e.t) && s.equals(e.s);
    }
    return false;
  }
  public int hashCode() {
    return Objects.hash(p, t, s);
  }
  public String toString() {
    return "Passenger " + p + " boards " + t + " at " + s;
  }
  public List<String> toStringList() {
    return List.of(p.toString(), t.toString(), s.toString());
  }
  public void replayAndCheck(MBTA mbta) {
    // check that train, passenger, and station exist in the simulation
    if (!mbta.hasTrain(t)) {
      throw new IllegalStateException("Simulation does not have Train " + t.toString());
    }

    if (!mbta.hasPassenger(p)) {
      throw new IllegalStateException("Simulation does not have Passenger " + p.toString());
    }

    if (!mbta.hasStation(s)) {
      throw new IllegalStateException("Simulation does not have Station " + s.toString());
    }

    // check that station does contain passenger already
    if (!mbta.stationContainsPassenger(p, s)) {
      throw new IllegalStateException("Passenger " + p.toString() + " isn't at station " + s.toString());
    }
    // check that train is at station s
    if (!mbta.atStation(s, t)) {
      throw new IllegalStateException(s.toString() + " is not the current location of train " + t.toString());
    }
    // check that train does not already contain passenger
    if (mbta.trainContainsPassenger(p, t)){
      throw new IllegalStateException("Train " + t.toString() + " already contains Passenger " + p.toString());
    }
    // check if passenger gets on train here(check if train line has curr station and next station)
    if (!mbta.boardPassenger(p, t, s)) {
      throw new IllegalStateException("Passenger " + p.toString() + " does not board here");
    }
    mbta.logBoardEvent(p, t, s); 
  }
}
