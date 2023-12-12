public class PassengerThread extends Thread {
    private MBTA mbta;
    private Passenger p;
    private Log log;

    private Station currStation;
    private Train currTrain;
    private int currLoc;
    

    public PassengerThread(MBTA m, Passenger p, Log l) {
        this.mbta = m;
        this.p = p;
        this.log = l;
        this.currLoc = mbta.getPassLoc(p);
        this.currStation = mbta.getPassStation(p, 0);
        this.currTrain = null;
    }

    public void run() {
        while(!mbta.atDestination(p)) {
            if (currTrain == null) { // passenger is at a station
                System.out.println("Passenger " + p.toString() + " is at Station " + currStation);
                Station s = currStation;
                Train toBoard = mbta.toBoard(p);
                toBoard.pLock.lock();
                try {
                    while(mbta.getStationTrain(currStation) != toBoard) {
                        System.out.println("Passenger " + p.toString() + " is waiting for " + toBoard.toString() + "'s lock");

                        toBoard.arrived.await();
                    }
                    System.out.println("Passenger " + p.toString() + " has obtained " + toBoard.toString() + "'s lock");
                    this.currTrain = toBoard;
                    this.currStation = null;
                    mbta.logBoardEvent(p, toBoard, s);
                    log.passenger_boards(p, toBoard, s);
                    toBoard.hasArrived();

                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("Passenger " + p.toString() + " could not board train");
                } finally {
                    toBoard.pLock.unlock();
                }

            } else { // passenger is on a train
                System.out.println("Passenger " + p.toString() + " is on Train " + currTrain.toString());

                Station toDeboard = mbta.getPassStation(p, currLoc + 1);
                Train t = currTrain;
                currTrain.pLock.lock();
                try {
                    while(mbta.getStationTrain(toDeboard) != currTrain) {
                        System.out.println("Passenger " + p.toString() + " is waiting for " + currTrain.toString() + "'s lock");
                        currTrain.arrived.await();
                    }
                    System.out.println("Passenger " + p.toString() + " has obtained " + currTrain.toString() + "'s lock");

                    this.currStation = toDeboard;
                    this.currTrain = null;
                    currLoc++;
                    mbta.logDeboardEvent(p, t, toDeboard);
                    log.passenger_deboards(p, t, toDeboard);
                    t.hasArrived();

                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("Passenger " + p.toString() + " could not deboard");
                } finally {
                    t.pLock.unlock();
                }
            }
        }
        System.out.println("Passenger " + p.toString() + " has finished their trip");
    }
}
