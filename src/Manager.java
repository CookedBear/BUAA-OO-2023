import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Manager {
    private static final ArrayList<RequestData> REQUESTLIST = new ArrayList<>();
    private static ArrayList<RequestData> saturateList = new ArrayList<>();
    private final HashMap<Long, ElevatorTMessage>
            elevatorInformation = new HashMap<>();
    private boolean finish = false;
    private long notifyThreadId;
    private final long[] threadIdList = new long[6];
    private int cnt = 0;

    public synchronized void putRequest(RequestData rd) {

        notifyThreadId = getThreadId(rd);
        /*
            notifyAll here, and re-wait the waiting elevators by notifyThreadId
         */
        if (notifyThreadId == -2) {     // cannot process this request now
            saturateList.add(rd);
        } else {                        // can find the suitable elevator now
            REQUESTLIST.add(rd);
            //OutputFormat.say("notifyAll by Request " + rd.getId() + " !");
            notifyAll();
        }

    }

    public synchronized void flushSaturateList(Long threadId) {
        if (elevatorInformation.get(threadId).getIsUp()) {
            Arrays.fill(elevatorInformation.get(threadId).getDownList(), 0);
        } else {
            Arrays.fill(elevatorInformation.get(threadId).getUpList(), 0);
        }
        for (int i = saturateList.size() - 1; i >= 0; i--) {
            RequestData rd = saturateList.get(i);
            // this.putRequest(rd);
            notifyThreadId = getThreadId(rd);
            if (notifyThreadId != -2) {                        // can find the suitable elevator now
                REQUESTLIST.add(rd);
                saturateList.remove(rd);
                //OutputFormat.say("normally notifyAll by Request " + rd.getId() + " !");
                notifyAll();
            }
        }

    }

    public synchronized void renewEtmData(Long threadId, Integer floor, Boolean isUp) {
        ElevatorTMessage etm = elevatorInformation.get(threadId);
        etm.setFloor(floor);
        etm.setIsUp(isUp);
    }

    private synchronized Long getThreadId(RequestData rd) {
        int to = rd.getTo();
        int from = rd.getFrom();
        long threadId = -2;
        int dis = 31;
        for (ElevatorTMessage etm : elevatorInformation.values()) {
            int[] list;
            if (rd.isUp()) {
                list = etm.getUpList();
                boolean b = false;
                for (int i = from; i < to; i++) {
                    if (list[i] >= 6) {
                        b = true;
                        break; } }
                if (b) { continue; }
                if (etm.getIsUp()) {
                    if (etm.getFloor() < from || (etm.getFloor() <= from && !etm.getWorking())) {
                        threadId = etm.getElevator().getId();
                        break; }
                } else {                                // 反向电梯
                    int distance;
                    if (etm.getReachingDown() >= from) {
                        distance = from - etm.getFloor();
                    } else {
                        distance = from + etm.getFloor() - 2 * etm.getReachingDown(); }
                    if (distance < dis) {
                        threadId = etm.getElevator().getId();
                        dis = distance; } }
            } else {
                list = etm.getDownList();
                boolean b = false;
                for (int i = from; i > to; i--) {
                    //System.out.print(list[i]+" ");
                    if (list[i] >= 6) {
                        b = true;
                        //System.out.println("f");
                        break; } }
                if (b) { continue; }
                //System.out.println(",");
                if (!etm.getIsUp()) {
                    if (etm.getFloor() > from || (etm.getFloor() >= from && !etm.getWorking())) {
                        threadId = etm.getElevator().getId();
                        break; }
                } else {                                // 反向电梯
                    int distance;
                    if (etm.getReachingUp() <= from) {  // 延申
                        distance = from - etm.getFloor();
                    } else { distance = etm.getReachingUp() * 2 - etm.getFloor() - from; }
                    if (distance < dis) {
                        threadId = etm.getElevator().getId();
                        dis = distance; } } } //System.out.println(" ");
        }
        if (threadId == -2) { return threadId; }
        rd.setThreadId(threadId);
        g(rd, to, from, threadId);
        if (rd.isUp()) {    // renew weight-List
            int[] list = elevatorInformation.get(threadId).getUpList();
            for (int i = from; i < to; i++) { list[i] = list[i] + 1; }
        } else {
            int[] list = elevatorInformation.get(threadId).getDownList();
            for (int i = from; i > to; i--) { list[i] = list[i] + 1; } }
        //System.out.println("choose elevator" + threadId);
        return threadId;
    }

    public void g(RequestData rd, int to, int from, long threadId) {
        if (rd.isUp()) {    // renew Up-Down-Floor
            if (to > elevatorInformation.get(threadId).getReachingUp()) {
                elevatorInformation.get(threadId).setReachingUp(to); }
            if (from < elevatorInformation.get(threadId).getReachingDown()) {
                elevatorInformation.get(threadId).setReachingDown(from); }
        } else {
            if (from > elevatorInformation.get(threadId).getReachingUp()) {
                elevatorInformation.get(threadId).setReachingUp(from); }
            if (to < elevatorInformation.get(threadId).getReachingDown()) {
                elevatorInformation.get(threadId).setReachingDown(to); } }
    }

    public synchronized ArrayList<RequestData> getAbleRequest(
            Integer currentFloor, Boolean isUp, Long threadId) {
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

    public synchronized HashMap<Long, ElevatorTMessage> getElevatorInformation() {
        return this.elevatorInformation;
    }

    public synchronized void setFinish(Boolean finish) { this.finish = finish; }

    public synchronized boolean getFinish() { return this.finish && saturateList.isEmpty(); }

    public synchronized long getNotifyThreadId() { return this.notifyThreadId; }

    public synchronized void setNotifyThreadId(long threadId) { this.notifyThreadId = threadId; }

    public synchronized void setStopped(long threadId, boolean stopped) {
        ElevatorTMessage etm = elevatorInformation.get(threadId);
        etm.setWorking(!stopped);
    }
}
