import static org.junit.Assert.*;

import java.util.List;

import org.junit.*;

public class Tests {
  @Test public void testPass() {
    assertTrue("true should be true", true);
  }

  @Test public void makeTrain() {
    Train t1 = Train.make("t1");
    Train copy = Train.make("t1");
    Train t2 = Train.make("t2");
    assertTrue("assert t1 and copy are the same", t1 == copy);
    assertTrue("assert t1 and t2 aren't the same", t1 != t2);
  }

  @Test public void makeStation() {
    Station t1 = Station.make("t1");
    Station copy = Station.make("t1");
    Station t2 = Station.make("t2");
    assertTrue("assert t1 and copy are the same", t1 == copy);
    assertTrue("assert t1 and t2 aren't the same", t1 != t2);
  }

  @Test public void makePassenger() {
    Passenger t1 = Passenger.make("t1");
    Passenger copy = Passenger.make("t1");
    Passenger t2 = Passenger.make("t2");
    assertTrue("assert t1 and copy are the same", t1 == copy);
    assertTrue("assert t1 and t2 aren't the same", t1 != t2);
  }

  @Test public void addLine() {
    MBTA t = new MBTA();
    Log log = new Log();
    
    Passenger alice = Passenger.make("Alice");
    Train train = Train.make("Orange");
    Station forestHills = Station.make("Forest Hills");
    Station greenStreet = Station.make("Green Street");
    Station jacksonSquare = Station.make("Jackson Square"); 

    t.addLine("Orange", List.of("Forest Hills", "Green Street", "Jackson Square"));
    t.addJourney("Alice", List.of("Forest Hills", "Jackson Square"));

    assert(t.stationContainsPassenger(alice, forestHills));

  }
 
  @Test public void verify_test1() {
    MBTA t = new MBTA();
    Log log = new Log();
    
    Passenger alice = Passenger.make("Alice");
    Train train = Train.make("Orange");
    Station forestHills = Station.make("Forest Hills");
    Station greenStreet = Station.make("Green Street");
    Station jacksonSquare = Station.make("Jackson Square");
    
    t.addLine("Orange", List.of("Forest Hills", "Green Street", "Jackson Square"));
    t.addJourney("Alice", List.of("Forest Hills", "Jackson Square"));

    log.passenger_boards(alice, train, forestHills);
    log.train_moves(train, forestHills, greenStreet);
    log.train_moves(train, greenStreet, jacksonSquare);
    log.passenger_deboards(alice, train, jacksonSquare);
    
    Verify.verify(t, log);
  }
  
  @Test public void verify_test2() {
    MBTA mbta = new MBTA();
    
    Passenger p1 = Passenger.make("Tim");
    Passenger p2 = Passenger.make("Tom");
    
    Station st1 = Station.make("Boston");
    Station st2 = Station.make("Toronto");
    Station st3 = Station.make("Miami");

    Train tr1 = Train.make("Train1");
    
    mbta.addLine("Train1", List.of("Boston", "Toronto", "Miami"));
    
    mbta.addJourney("Tim", List.of("Boston", "Toronto"));
    mbta.addJourney("Tom", List.of("Boston", "Miami"));
    
    Log log = new Log(List.of(
        new BoardEvent(p1, tr1, st1),
        new BoardEvent(p2, tr1, st1),
        new MoveEvent(tr1, st1, st2),
        new DeboardEvent(p1, tr1, st2),
        new MoveEvent(tr1, st2, st3),
        new DeboardEvent(p2, tr1, st3)
        ));
    
    Verify.verify(mbta, log);
  }

  @Test public void verifyTest() {
    MBTA newEngland = new MBTA();
    newEngland.addLine("North", List.of("Maine", "New Hampshire", "Vermont"));
    newEngland.addLine("Mid", List.of("Rhode Island", "Massachusetts", "Vermont"));
    newEngland.addLine("South", List.of("Massachusetts", "Connecticut", "Rhode Island"));
    Train north = Train.make("North");
    Train mid = Train.make("Mid"); 
    Train south = Train.make("South"); 
    Station me = Station.make("Maine");
    Station nh = Station.make("New Hampshire");
    Station vt = Station.make("Vermont");
    Station ma = Station.make("Massachusetts");
    Station ct = Station.make("Connecticut");
    Station ri = Station.make("Rhode Island"); 
    Passenger p = Passenger.make("West Coaster");
    newEngland.addJourney("West Coaster", List.of("Maine", "Vermont", "Rhode Island", "Connecticut"));
    Log log = new Log(List.of(new BoardEvent(p, north, me),
                              
                              new MoveEvent(north, me, nh),
                              new MoveEvent(south, ma, ct),
                              new MoveEvent(mid, ri, ma),
                             
                              new MoveEvent(north, nh, vt), 
                              new DeboardEvent(p, north, vt),
                              new MoveEvent(north, vt, nh),
                              new MoveEvent(mid, ma, vt),
                              new BoardEvent(p, mid, vt),
                              new MoveEvent(south, ct, ri),                            
                          
                              
                              new MoveEvent(north, nh, me),
                              new MoveEvent(mid, vt, ma),
                              new MoveEvent(south, ri, ct),
                              
                              new MoveEvent(north, me, nh),
                              new MoveEvent(mid, ma, ri),
                              new DeboardEvent(p, mid, ri),
                              new MoveEvent(mid, ri, ma),
                              new MoveEvent(south, ct, ri),
                              new BoardEvent(p, south, ri),
                              
                              new MoveEvent(north, nh, vt),
                              new MoveEvent(south, ri, ct),
                              new DeboardEvent(p, south, ct)));
    Verify.verify(newEngland, log);
  }
}
