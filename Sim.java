import java.io.*;
import java.util.*;

public class Sim {

  public static void run_sim(MBTA mbta, Log log) {
    List<Thread> threads = new ArrayList<>();
    List<Train> trains = mbta.getTrains();
    for (Train t : trains) {
      threads.add(new TrainThread(mbta, t, log));
    }

    for (Thread t : threads) {
      t.start();
    }

    try {
      for (Thread t : threads) {
        t.join();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  public static void main(String[] args) throws Exception {
    if (args.length != 1) {
      System.out.println("usage: ./sim <config file>");
      System.exit(1);
    }

    MBTA mbta = new MBTA();
    mbta.loadConfig(args[0]);

    Log log = new Log();

    run_sim(mbta, log);

    String s = new LogJson(log).toJson();
    PrintWriter out = new PrintWriter("log.json");
    out.print(s);
    out.close();

    mbta.reset();
    mbta.loadConfig(args[0]);
    Verify.verify(mbta, log);
  }
}
