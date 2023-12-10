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
        //Station curr = mbta.currTrainLoc(t);
        //start(curr);
        while (!mbta.isFinished()) {
            Station curr = mbta.currTrainLoc(t);
            System.out.println("Train " + t.toString() + " currently at Station [" + curr.toString() + "]");
            t.pLock.lock();
            //System.out.println("Locked " + t.toString() + " passenger lock");
            Station next = mbta.nextTrainStation(t);
            System.out.println("Train " + t.toString() + " next station is [" + next.toString() + "]");
            next.sLock.lock();
            try {
                while(!mbta.isAvailable(next)) {
                    next.available.await(); 
                }
               // System.out.println("Train " + t.toString() + " got Station " + next.toString() + "'s lock");
                mbta.setStationTrain(curr, null);

                //curr.sLock.unlock();

                //System.out.println("Moving Train " + t.toString());
                moveTrain(curr, next);
                curr.makeAvailable();
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException("Train " + t.toString() + " could not move");
            } finally {
                //curr.sLock.unlock();
                next.sLock.unlock();
                t.pLock.unlock();
            }
            waitAtStation(next);
        }
    }

    // private void start(Station curr) {
    //     System.out.println("Starting start procedure for Train " + t.toString());
    //     if (mbta.atStart()) {
    //         curr.sLock.lock();
    //         t.pLock.lock();
    //         mbta.setStationTrain(curr, true);
    //         t.hasArrived();
    //         try {
    //             sleep(100);
    //         } catch (InterruptedException e) {
    //             e.printStackTrace();
    //             throw new RuntimeException("Thread " + t.toString() + " couldn't sleep");
    //         } finally {
    //             t.pLock.unlock();
    //         }
    //     }
    // }

    private void waitAtStation(Station s) {
        System.out.println("Train " + t.toString() + " is pausing");
        // t.pLock.unlock();
        s.sLock.lock();
        try {
            sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException("Thread " + t.toString() + " couldn't sleep");
        } finally {
            s.sLock.unlock();
        }
    }

    private void moveTrain(Station curr, Station next) {
        mbta.logMoveEvent(t, next);
        mbta.setStationTrain(next, t);
        log.train_moves(t, curr, next);
    }
}