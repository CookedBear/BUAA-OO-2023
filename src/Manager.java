import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    private static final ArrayList<RequestData> REQUESTLIST = new ArrayList<>();
    private static ArrayList<RequestData> saturateList = new ArrayList<>();         // request cannot process immediately
    private final HashMap<Long, ElevatorTMessage> elevatorInformation = new HashMap<>();
    private boolean finish = false;
    private long notifyThreadId;
    private final long[] threadIdList = new long[6];
    private int cnt = 0;

    public synchronized void putRequest(RequestData rd) {

        notifyThreadId = getThreadId(rd);
        /*
            notifyAll here, and re-wait the waiting elevators by notifyThreadId
         */
        if (notifyThreadId == -1) {     // cannot process this request now
            saturateList.add(rd);
        } else {                        // can find the suitable elevator now
            REQUESTLIST.add(rd);
            OutputFormat.say("notifyAll by Request " + rd.getId() + " !");
            notifyAll();
        }

    }

    public synchronized void flushSaturateList(Long threadId) {      // flush after every successful turn
        elevatorInformation.get(threadId).setPeople(0);
        for (int i = saturateList.size() - 1; i >= 0; i--) {
            RequestData rd = saturateList.get(i);
            // this.putRequest(rd);
            notifyThreadId = getThreadId(rd);

            if (notifyThreadId != -1) {                        // can find the suitable elevator now
                OutputFormat.say("successfully re-put Request " + rd.getId() + " !");
                REQUESTLIST.add(rd);
                saturateList.remove(rd);
                OutputFormat.say("normally notifyAll by Request " + rd.getId() + " !");
                notifyAll();
            }
        }

    }

    public synchronized void renewEtmData(Long threadId, Integer floor, Boolean isUp) {           // renew after every turn(), a-short-ap
        ElevatorTMessage etm = elevatorInformation.get(threadId);
        etm.setFloor(floor);
        etm.setIsUp(isUp);
    }

    private synchronized Long getThreadId(RequestData rd) {

        /*
            get the correct threadId(correct elevator) for RequestData rd
            and store the threadId here
            **     main arrange algorithm!     **
         */

        cnt++;
        if (cnt >= 3) {
            cnt = 0;
            // OutputFormat.say("cannot process Request: " + rd.getId());
            // return (long)-1;
        }
        int to = rd.getTo();
        int from = rd.getFrom();

        if (elevatorInformation.get(threadIdList[cnt]).getPeople() == 6) {
            OutputFormat.say("cannot process Request: " + rd.getId() + "!!");
            return (long)-1;
        }
        /*
            A到了，叫了flush函数处理数据，数据该给B，B还没到，处理失败，等着
         */

        rd.setThreadId(threadIdList[cnt]);
        if (rd.isUp()) {
            if (to > elevatorInformation.get(threadIdList[cnt]).getReachingUp()) {
                elevatorInformation.get(threadIdList[cnt]).setReachingUp(to);
            }
            if (from < elevatorInformation.get(threadIdList[cnt]).getReachingDown()) {
                elevatorInformation.get(threadIdList[cnt]).setReachingDown(from);
            }
        } else {
            if (from > elevatorInformation.get(threadIdList[cnt]).getReachingUp()) {
                elevatorInformation.get(threadIdList[cnt]).setReachingUp(from);
            }
            if (to < elevatorInformation.get(threadIdList[cnt]).getReachingDown()) {
                elevatorInformation.get(threadIdList[cnt]).setReachingDown(to);
            }
        }
        // System.out.println(elevatorInformation.get(threadIdList[cnt]).getReachingUp());
        // System.out.println(elevatorInformation.get(threadIdList[cnt]).getReachingDown());
        elevatorInformation.get(threadIdList[cnt]).setPeople(elevatorInformation.get(threadIdList[cnt]).getPeople() + 1);
        return threadIdList[cnt];
    }

    public synchronized ArrayList<RequestData> getAbleRequest(Integer currentFloor, Boolean isUp, Long threadId) {
        ArrayList<RequestData> returnRequest = new ArrayList<>();

        for (int i = REQUESTLIST.size() - 1; i >= 0; i--) {
            RequestData rd = REQUESTLIST.get(i);
            if (rd.getThreadId() == threadId &&     //correct elevator
                rd.getFrom() == currentFloor &&     //correct floor
                rd.isUp() == isUp) {                //correct direction
                returnRequest.add(rd);              // move into elevatorList
                REQUESTLIST.remove(rd);
            }
        }
        return returnRequest;
    }

    public synchronized boolean hasRequest(Long threadId) {
        for (RequestData rd : REQUESTLIST) {
            if (rd.getThreadId() == threadId) {
                return true;
            }
        }
        return false;
    }

    public synchronized void setElevatorInformation(Elevator elevator) {
        elevatorInformation.put(elevator.getId(), new ElevatorTMessage(elevator));
        threadIdList[cnt++] = elevator.getId();
    }

    public synchronized HashMap<Long, ElevatorTMessage> getElevatorInformation() { return this.elevatorInformation; }

    public synchronized void setFinish(Boolean finish) { this.finish = finish; }

    public synchronized boolean getFinish() { return this.finish; }

    public synchronized long getNotifyThreadId() { return this.notifyThreadId; }

    public synchronized void setNotifyThreadId(long threadId) { this.notifyThreadId = threadId; }
}
