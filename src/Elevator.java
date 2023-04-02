import java.util.ArrayList;

public class Elevator extends Thread {
    private int maxPeople = 6;
    private final int openTime = 200;
    private int moveTime = 400;
    private final int gapTime = 5;
    private final int topFloor = 11;
    private final int bottomFloor = 1;
    private final int elevatorId;
    private final Manager publicManager;
    private int currentFloor;
    private ArrayList<RequestData> currentRequest;
    private boolean isUp;
    private long time = 0;

    Elevator(int id,int currentFloor,int maxPeople, int moveTime, Manager manager) {
        this.currentFloor = currentFloor;  // initialize to climb from floor-1
        this.currentRequest =   // initialize a new List
                new ArrayList<>();
        this.elevatorId = id;
        this.publicManager = manager;
        this.isUp = true;       // initialize to climb up
        this.moveTime = moveTime;
        this.maxPeople = maxPeople;
    }

    @Override
    public void run() {

        try {
            synchronized (publicManager) {
                // OutputFormat.say(currentThread().getName() + " waiting!");
                publicManager.setStopped(currentThread().getId(), true);
                publicManager.wait();
                // publicManager.setStopped(currentThread().getId(), false);
            }
            // OutputFormat.say(currentThread().getName() + " started!");
            if (checkMaintain()) {
                OutputFormat.able(elevatorId);
                // OutputFormat.say("Elevator "+ elevatorId + " maintained1!");
                return; }
            turn();
            // check people out-and-in
            checkRequest();
            // still have requests

            while (true) {
                if (publicManager.getMaintain(currentThread().getId())) {
                    if (checkMaintain()) {
                        OutputFormat.able(elevatorId);
                        // OutputFormat.say("Elevator "+ elevatorId + " maintained2!");
                        return; } }
                if (publicManager.getFinish() &&
                    !publicManager.hasRequest(currentThread().getId()) &&
                    currentRequest.isEmpty()) {
                    // after inputFinish, no requests left
                    // OutputFormat.say(currentThread().getName() + " closed!");
                    synchronized (publicManager) {
                        publicManager.setNotifyThreadId(-1);
                        publicManager.setMaintainThreadId(-1);
                        publicManager.notifyAll();
                        // OutputFormat.say("notifyAll called by " + currentThread().getName());
                        return; } }
                synchronized (publicManager) {
                    if ((!publicManager.getFinish()) &&
                        !publicManager.hasRequest(currentThread().getId()) &&
                        currentRequest.isEmpty()) {
                        // resting before inputFinish
                        // OutputFormat.say(currentThread().getName() + " Resting!");
                        do {
                            publicManager.setStopped(currentThread().getId(), true);
                            // OutputFormat.say(currentThread().getName() + " Waiting!");
                            publicManager.wait();
                            // OutputFormat.say(currentThread().getName() + " Waking!");
                            // publicManager.setStopped(currentThread().getId(), false);
                        } while (publicManager.getNotifyThreadId() != currentThread().getId() &&
                                 publicManager.getNotifyThreadId() != -1 &&
                                 !publicManager.getMaintain(currentThread().getId()));
                        // OutputFormat.say(currentThread().getName() + " Restarting!");
                        if (publicManager.getNotifyThreadId() == -1 &&
                                !publicManager.hasRequest(currentThread().getId()) &&
                                currentRequest.isEmpty() &&
                                !publicManager.getMaintain(currentThread().getId())) {
                            // OutputFormat.say(currentThread().getName() + " closed!");
                            return; } } }
                checkRequest();
                // do we need turn?
                turn();
                // check people after restart
                checkRequest();

                // just jump out the synchronized range
                if (checkMaintain()) {
                    OutputFormat.able(elevatorId);
                    // OutputFormat.say("Elevator "+ elevatorId + " maintained2!");
                    return;
                }
                // move one floor
                climbOneFloor();
                if (checkMaintain()) {
                    OutputFormat.able(elevatorId);
                    // OutputFormat.say("Elevator "+ elevatorId + " maintained2!");
                    return;
                }
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
        publicManager.setStopped(currentThread().getId(), false);
        try {
            if ((moveTime + time - System.currentTimeMillis()) < gapTime + moveTime) {
                Thread.sleep(moveTime);
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
            get(currentThread().getId()).getRcUp() &&
            isUp) {                // reach top, turning down
            publicManager.getElevatorInformation().get(
                    currentThread().getId()).setReachingUp(bottomFloor);
            isUp = false;
            publicManager.renewEtmData(currentThread().getId(), currentFloor, isUp);
            publicManager.flushSaturateList(currentThread().getId());
            return;
        } else if (currentFloor ==
                   publicManager.getElevatorInformation().
                   get(currentThread().getId()).
                           getRcDn() &&
                   !isUp) {     // reach bottom, turning up
            publicManager.getElevatorInformation().get(
                    currentThread().getId()).setReachingDown(topFloor);
            isUp = true;
            publicManager.renewEtmData(currentThread().getId(), currentFloor, isUp);
            publicManager.flushSaturateList(currentThread().getId());
            return;
        }

        if (currentFloor == topFloor && isUp) {   // top floor: force
            isUp = false;
            publicManager.renewEtmData(currentThread().getId(), currentFloor, isUp);
            publicManager.flushSaturateList(currentThread().getId());
            return;
        }
        if (currentFloor == bottomFloor && !isUp) {   // base floor: force

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
            publicManager.setStopped(currentThread().getId(), true);
            opened = true;                          // flag for CLOSE
        }

        if (opened) {                               // use flag to CLOSE
            time = openClosed(false, inRequestList, t1); // CLOSE
        }
    }

    private boolean checkMaintain() {
        // 清除剩余队列中的同电梯标记
        // wait时需要notify并进入check - finished
        ElevatorTMessage etm = publicManager.getElevatorInformation().get(currentThread().getId());
        if (etm.getMaintain()) {
            // left two arrives(actually one left)
            // do we need turn?
            turn();
            // check people out-and-in
            checkRequest();
            // prevent setting Request-in-SL into the closed thread again!!
            publicManager.removeEtm(currentThread().getId());
            publicManager.delCurrentRequest(currentThread().getId());
            if (currentRequest.isEmpty()) {
                // cannot transport more, reEmpty old-request.threadId

            } else {
                // trans for one-and-final floor
                // climbOneFloor();
                pourRequestOut();
                publicManager.reAdd2SaturateList(currentRequest);

            }
            publicManager.setAble();
            return true;
        }
        return false;
    }

    private long openClosed(boolean open, ArrayList<RequestData> actionRequestList, Long t1) {
        long t0 = 0;
        if (open) {             // open -> sleep (-> out)
            OutputFormat.open(currentFloor, elevatorId);
            t0 = System.currentTimeMillis();
            // TimableOutput.println(String.format("OPEN-%d-%s",currentFloor, elevatorId));
            for (RequestData rd : actionRequestList) {  // print request out data
                rd.requestOut(elevatorId);
            }
            return t0;
        } else {                // sleep -> (in) ->closed
            long t2 = System.currentTimeMillis();
            try {
                Thread.sleep(gapTime + 2 * openTime - t2 + t1);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
            // CLOSE 前标记为开始移动 (停止分配)
            publicManager.setStopped(currentThread().getId(), false);
            // OutputFormat.say("set false");
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

    private void pourRequestOut() {
        OutputFormat.open(currentFloor, elevatorId);
        try {
            Thread.sleep(400);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        for (int i = currentRequest.size() - 1; i >= 0; i--) {
            RequestData rd = currentRequest.get(i);
            rd.requestOutTemp(elevatorId, currentFloor);
            if (rd.getTo() == currentFloor) {
                currentRequest.remove(i);
            } else {
                rd.setFrom(currentFloor);
                rd.setThreadId((long) 0);
            }
        }
        OutputFormat.close(currentFloor, elevatorId);
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

    public int getStartFloor() { return this.currentFloor; }

    public int getMaxPeople() { return this.maxPeople; }

    public double getMovingTime() { return this.moveTime; }
}

/*
11-FROM-1-TO-15
12-FROM-1-TO-15
13-FROM-1-TO-15
 */