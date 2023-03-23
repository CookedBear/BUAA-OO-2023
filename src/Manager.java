import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    private static final ArrayList<RequestData> REQUESTLIST = new ArrayList<>();
    private final HashMap<Long, ElevatorTMessage> elevatorInformation = new HashMap<>();
    private boolean finish = false;
    private long notifyThreadId;
    private long[] threadIdList = new long[6];
    private int cnt = 0;

    public synchronized void putRequest(RequestData rd) {
        REQUESTLIST.add(rd);
        notifyThreadId = getThreadId(rd);
        /*
            notifyAll here, and re-wait the waiting elevators by notifyThreadId
         */
        OutputFormat.say("notifyAll!");
        notifyAll();
    }

    private synchronized Long getThreadId(RequestData rd) {

        /*
            get the correct threadId(correct elevator) for RequestData rd
            and store the threadId here
            **     main arrange algorithm!     **
         */

        cnt++;
        if (cnt >= 6) {
            cnt = 0;
        }
        int to = rd.getTo();
        int from = rd.getFrom();

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

    public synchronized boolean isEmpty() { return REQUESTLIST.isEmpty(); }

    public synchronized long getNotifyThreadId() { return this.notifyThreadId; }

    public synchronized void setNotifyThreadId(long threadId) { this.notifyThreadId = threadId; }
}
