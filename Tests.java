import static org.junit.Assert.*;
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
}
