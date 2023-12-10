public class PassengerThread extends Thread {
    private MBTA mbta;
    private Passenger p;
    private Log log;

    public PassengerThread(MBTA m, Passenger p, Log l) {
        this.mbta = m;
        this.p = p;
        this.log = l;
    }

    public void run() {
        while(!mbta.atDestination(p)) {
            if (mbta.atStart()) {
                return;
            }
        }
    }
}
