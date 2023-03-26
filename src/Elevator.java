import java.util.ArrayList;

public class Elevator extends Thread {
    private final int maxPeople = 6;
    private final int openTime = 200;
    private final int moveTime = 400;
    private final int elevatorId;
    private final Manager publicManager;
    private int currentFloor;
    private ArrayList<RequestData> currentRequest;
    private boolean isUp;
    private int reachingUp;
    private int reachingDown;
    private boolean isClosed;
    private long time = 0;
    private boolean stopped = true;

    Elevator(int id, Manager manager) {
        this.currentFloor = 1;  // initialize to climb from floor-1
        this.currentRequest =   // initialize a new List
                new ArrayList<>();
        this.elevatorId = id;
        this.publicManager =
                manager;
        this.isUp = true;       // initialize to climb up
        this.reachingUp = 1;   // initialize to close the door
        this.reachingDown = 11;
        this.isClosed = true;
    }

    @Override
    public void run() {

        try {

            synchronized (publicManager) {
                //OutputFormat.say(currentThread().getName() + " waiting!");
                publicManager.setStopped(currentThread().getId(), true);
                publicManager.wait();
                publicManager.setStopped(currentThread().getId(), false);
                //OutputFormat.say(currentThread().getName() + " started!");
            }
            // check people out-and-in
            checkRequest();
            // still have requests

            while (true) {
                if (publicManager.getFinish() &&
                    !publicManager.hasRequest(currentThread().getId()) &&
                    currentRequest.isEmpty()) {
                    // after inputFinish, no requests left
                    //OutputFormat.say(currentThread().getName() + " closed!");
                    synchronized (publicManager) {
                        publicManager.setNotifyThreadId(-1);
                        publicManager.notifyAll();
                        //OutputFormat.say("notifyAll called by " + currentThread().getName());
                        return;
                    }
                }
                synchronized (publicManager) {
                    if ((!publicManager.getFinish()) &&
                        !publicManager.hasRequest(currentThread().getId()) &&
                        currentRequest.isEmpty()) {
                        // resting before inputFinish
                        //OutputFormat.say(currentThread().getName() + " Resting!");
                        do {
                            publicManager.setStopped(currentThread().getId(), true);
                            publicManager.wait();
                            publicManager.setStopped(currentThread().getId(), false);
                        } while (publicManager.getNotifyThreadId() != currentThread().getId() &&
                                publicManager.getNotifyThreadId() != -1);
                        //OutputFormat.say(currentThread().getName() + " Restarting!");
                        if (publicManager.getNotifyThreadId() == -1 &&
                                !publicManager.hasRequest(currentThread().getId()) &&
                                currentRequest.isEmpty()) {
                            return;
                        }
                        checkRequest();
                        // do we need turn?
                        turn();
                        // check people after restart
                        checkRequest();
                    }
                }
                // move one floor
                climbOneFloor();
                // do we need turn?
                turn();
                // check people out-and-in
                checkRequest();
            }

        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    private void climbOneFloor() {
        try {
            if ((moveTime + time - System.currentTimeMillis()) < 405) {
                Thread.sleep(400);
            } else {
                Thread.sleep(moveTime + time - System.currentTimeMillis());
            }
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        if (isUp) {
            currentFloor++;
        } else {
            currentFloor--;
        }
        OutputFormat.arrive(currentFloor, elevatorId);
        time = System.currentTimeMillis();
        // TimableOutput.println(String.format("ARRIVE-%d-%s", currentFloor, elevatorId));
    }

    private void turn() {

        if (currentFloor ==
            publicManager.getElevatorInformation().
            get(currentThread().getId()).getReachingUp() &&
            isUp) {                // reach top, turning down
            publicManager.getElevatorInformation().get(currentThread().getId()).setReachingUp(1);
            isUp = false;
            publicManager.renewEtmData(currentThread().getId(), currentFloor, isUp);
            publicManager.flushSaturateList(currentThread().getId());
            return;
        } else if (currentFloor ==
                   publicManager.getElevatorInformation().
                   get(currentThread().getId()).
                   getReachingDown() &&
                    !isUp) {     // reach bottom, turning up
            publicManager.getElevatorInformation().get(currentThread().getId()).setReachingDown(11);
            isUp = true;
            publicManager.renewEtmData(currentThread().getId(), currentFloor, isUp);
            publicManager.flushSaturateList(currentThread().getId());
            return;
        }

        if (currentFloor == 11 && isUp) {   // top floor: force
            isUp = false;
            publicManager.renewEtmData(currentThread().getId(), currentFloor, isUp);
            publicManager.flushSaturateList(currentThread().getId());
            return;
        }
        if (currentFloor == 1 && !isUp) {   // base floor: force

            isUp = true;
            publicManager.renewEtmData(currentThread().getId(), currentFloor, isUp);
            publicManager.flushSaturateList(currentThread().getId());
        }

        publicManager.renewEtmData(currentThread().getId(), currentFloor, isUp);
    }

    private void checkRequest() {

        /*
            电梯上下客的核心程序
            先下后上，要事先根据下电梯人数更新currentRequest.size
            问题在于：到站能确定out和 **一部分** in，关门瞬间才能 **完全** 确定in
         */

        ArrayList<RequestData> outRequestList = getOutRequest();
        boolean hasOutRequest = !outRequestList.isEmpty();

        ArrayList<RequestData> inRequestList =
            publicManager.getAbleRequest(currentFloor, isUp, currentThread().getId());
        boolean hasInRequest = !inRequestList.isEmpty();

        boolean opened = false;                     // flag of opened-or-not
        long t1 = 0;

        if (hasInRequest || hasOutRequest) {        // need to OPEN
            t1 = openClosed(true, outRequestList, (long)0); // OPEN
            opened = true;                          // flag for CLOSE
        }

        if (opened) {                               // use flag to CLOSE
            time = openClosed(false, inRequestList, t1); // CLOSE
        }
    }

    private long openClosed(boolean open, ArrayList<RequestData> actionRequestList, Long t1) {
        long t0 = 0;
        if (open) {             // open -> sleep (-> out)
            this.isClosed = false;
            OutputFormat.open(currentFloor, elevatorId);
            t0 = System.currentTimeMillis();
            // TimableOutput.println(String.format("OPEN-%d-%s",currentFloor, elevatorId));
            for (RequestData rd : actionRequestList) {  // print request out data
                rd.requestOut(elevatorId);
            }
            return t0;
        } else {                // sleep -> (in) ->closed
            this.isClosed = true;
            long t2 = System.currentTimeMillis();
            try {
                Thread.sleep(405 - t2 + t1);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
            //OutputFormat.say("Try to collect 3563!");
            actionRequestList.addAll(
                    publicManager.getAbleRequest(currentFloor, isUp, currentThread().getId()));
            for (RequestData rd : actionRequestList) {  // print request in data
                rd.requestIn(elevatorId);
            }
            currentRequest.addAll(actionRequestList);   // add in the request on-board
            OutputFormat.close(currentFloor, elevatorId);
            // TimableOutput.println(String.format("CLOSE-%d-%s",currentFloor, elevatorId));
            return System.currentTimeMillis();
        }

    }

    private ArrayList<RequestData> getOutRequest() {
        /*
         *  Return requestList of going out at currentFloor in this elevator
         *  while return, change currentRequest
         */
        ArrayList<RequestData> returnRequest = new ArrayList<>();
        for (int i = currentRequest.size() - 1; i >= 0; i--) {
            RequestData rd = currentRequest.get(i);
            //  System.out.println("out-request:" + rd.getId() + ", " + rd.getTo());
            if (rd.getTo() == currentFloor) {
                returnRequest.add(rd);
                currentRequest.remove(rd);
            }
        }

        return returnRequest;
    }

    public boolean stopped() { return this.stopped; }
}

/*
11-FROM-1-TO-15
12-FROM-1-TO-15
13-FROM-1-TO-15
 */