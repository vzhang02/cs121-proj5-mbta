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
    if (!mbta.stationContainsPassenger(p, s)) {
      throw new IllegalStateException("Passenger " + p.toString() + " isn't at station " + s.toString());
    }
    if (s != mbta.currTrainLoc(t)) {
      throw new IllegalStateException(s.toString() + " is not the current location of train " + t.toString());
    }
    if (mbta.trainContainsPassenger(p, t)) {
      throw new IllegalStateException("Train " + t.toString() + " does not contain passenger " + p.toString());
    }
    if (!mbta.deboardPassenger(p, s, t)) {
      throw new IllegalStateException("Passenger " + p.toString() + " cannot deboard here");
    }
    mbta.logDeboardEvent(p, t, s); 
  }
}
