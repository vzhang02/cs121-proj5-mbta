public class TrainThread extends Thread {
    private MBTA mbta;
    private Train t;
    private Log log;

    public TrainThread(MBTA m, Train t, Log l) {
        this.mbta = m;
        this.t = t;
        this.log = l;
    }


    public void run() {
        Station curr = mbta.currTrainLoc(t);
        start(curr);
        // System.out.println("Finished start procedure for Train " + t.toString());
        while (!mbta.isFinished()) {
            curr = mbta.currTrainLoc(t);
            //System.out.println("Train " + t.toString() + " currently at Station [" + curr.toString() + "]");
            t.pLock.lock();
            //System.out.println("Locked " + t.toString() + " passenger lock");
            Station next = mbta.nextTrainStation(t);
            //System.out.println("Train " + t.toString() + " next station is [" + next.toString() + "]");
            next.sLock.lock();
            try {
                while(!mbta.isAvailable(next)) {
                    next.available.await(); 
                }
                //System.out.println("Moving Train " + t.toString());
                moveTrain(curr, next);
                curr.makeAvailable();
                t.hasArrived();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Train " + t.toString() + " could not move");
            } finally {
                next.sLock.unlock();
                t.pLock.unlock();
            }
            waitAtStation(next);
        }
    }

    private void start(Station curr) {
        // System.out.println("Starting start procedure for Train " + t.toString());
        if (mbta.atStart()) {
            curr.sLock.lock();
            t.pLock.lock();
            mbta.setStationTrain(curr, t);
            t.hasArrived();
            try {
                sleep(10);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Thread " + t.toString() + " couldn't sleep");
            } finally {
                t.pLock.unlock();
            }
        }
    }

    private void waitAtStation(Station s) {
        System.out.println("Train " + t.toString() + " is pausing");
        s.sLock.lock();
        try {
            sleep(100);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Thread " + t.toString() + " couldn't sleep");
        } finally {
            s.sLock.unlock();
        }
    }

    private void moveTrain(Station curr, Station next) {
        mbta.setStationTrain(curr, null);
        mbta.logMoveEvent(t, next);
        mbta.setStationTrain(next, t);
        log.train_moves(t, curr, next);
    }
}