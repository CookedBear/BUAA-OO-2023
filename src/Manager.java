import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Manager {
    private static final ArrayList<RequestData> REQUESTLIST = new ArrayList<>();
    private static ArrayList<RequestData> saturateList = new ArrayList<>();
    private final HashMap<Long, ElevatorTMessage>
            elevatorInformation = new HashMap<>();
    private boolean finish = false;
    private boolean eof = false;
    private long notifyThreadId;
    private long maintainThreadId = -1;
    private int acceptedCount = 0;
    private int ableCount = 0;

    public synchronized void putRequest(RequestData rd) {

        notifyThreadId = getThreadId(rd);
        maintainThreadId = -1;
        /*
            notifyAll here, and re-wait the waiting elevators by notifyThreadId
         */
        if (notifyThreadId == -2) {     // cannot process this request now
            saturateList.add(rd);
        } else {                        // can find the suitable elevator now
            REQUESTLIST.add(rd);
            // OutputFormat.say("notifyAll by " + rd.getId() + " " + notifyThreadId + " !");
            notifyAll();
        }

    }

    public synchronized void flushSaturateList(Long threadId) {
        if (threadId != -1) {
            if (elevatorInformation.get(threadId).getIsUp()) {
                Arrays.fill(elevatorInformation.get(threadId).getDownList(), 0);
            } else {
                Arrays.fill(elevatorInformation.get(threadId).getUpList(), 0);
            }
        }
        for (int i = saturateList.size() - 1; i >= 0; i--) {
            RequestData rd = saturateList.get(i);
            // this.putRequest(rd);
            notifyThreadId = getThreadId(rd);
            maintainThreadId = -1;
            if (notifyThreadId != -2) {                        // can find the suitable elevator now
                REQUESTLIST.add(rd);
                saturateList.remove(rd);
                // OutputFormat.say("Request " + rd.getId() + " in" + notifyThreadId + " !");
                notifyAll();
            }
        }

    }

    public synchronized void renewEtmData(Long threadId, Integer floor, Boolean isUp) {
        ElevatorTMessage etm = elevatorInformation.get(threadId);
        etm.setFloor(floor);
        etm.setIsUp(isUp);
    }

    public synchronized void delCurrentRequest(Long threadId) {
        for (int i = REQUESTLIST.size() - 1; i >= 0; i--) {
            RequestData rd = REQUESTLIST.get(i);
            if (rd.getThreadId() == threadId) {
                rd.setThreadId((long) 0);
                REQUESTLIST.remove(rd);
                saturateList.add(rd);
                // OutputFormat.say("Set request "+rd.getId()+" unknown!");
            }
        }
        flushSaturateList((long) -1);
        //notifyAll();
    }

    public synchronized void reAdd2SaturateList(ArrayList<RequestData> currentRequest) {
        saturateList.addAll(currentRequest);
        flushSaturateList((long) -1);
        //notifyAll();
    }

    private synchronized Long getThreadId(RequestData rd) {
        int to = rd.getTo();
        int from = rd.getFrom();
        long threadId = -2;
        double dis = 191981000;
        for (ElevatorTMessage etm : elevatorInformation.values()) {
            int[] list;
            if (rd.isUp()) {
                list = etm.getUpList();
                boolean b = false;
                int stops = 0;
                for (int i = from; i < to; i++) {
                    int max = etm.getMaxPeople();
                    if (list[i] >= max) {
                        b = true;
                        break;
                    } else if (list[i] != list[i - 1]) {
                        stops++;
                    }
                }
                if (b) { continue; }
                double totalTime = 191981000;
                if (etm.getIsUp()) {
                    if (etm.getFloor() < from || (etm.getFloor() <= from && !etm.getWorking())) {
                        threadId = etm.getElevator().getId();
                        break;
                        //totalTime = from - etm.getFloor();
                    } else {
                        continue;
                    }
                } else {                                // 反向电梯

                    if (etm.getReachingDown() >= from) {
                        totalTime = (from - etm.getFloor()) * etm.getMovingTime();
                    } else {
                        totalTime = (from + etm.getFloor() - 2 * etm.getReachingDown()) * etm.getMovingTime(); }
                }
                if (totalTime < dis) {
                    threadId = etm.getElevator().getId();
                    dis = totalTime; }
            } else {
                list = etm.getDownList();
                boolean b = false;
                int stops = 0;
                for (int i = from; i > to; i--) {
                    int max = etm.getMaxPeople();
                    if (list[i] >= max) {
                        b = true;
                        break; } else if (list[i] != list[i + 1]) { stops++; } }
                if (b) { continue; }
                double totalTime = 191981000;
                if (!etm.getIsUp()) {
                    if (etm.getFloor() > from || (etm.getFloor() >= from && !etm.getWorking())) {
                        threadId = etm.getElevator().getId();
                        break;
                        //totalTime = - from + etm.getFloor();
                    } else {
                        continue;
                    }
                } else {                                // 反向电梯
                    if (etm.getReachingUp() <= from) {  // 延申
                        totalTime = (from - etm.getFloor()) * etm.getMovingTime();
                    } else { totalTime = (etm.getReachingUp() * 2 - etm.getFloor() - from) * etm.getMovingTime(); }
                }
                if (totalTime < dis) {
                    threadId = etm.getElevator().getId();
                    dis = totalTime; } } //System.out.println(" ");
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

    public synchronized void setElevatorInformation(int elevatorId, Elevator elevator) {
        elevatorInformation.put(elevator.getId(), new ElevatorTMessage(elevator, elevatorId, elevator.getStartFloor(), elevator.getMaxPeople(), elevator.getMovingTime()));
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

    public synchronized void pushMaintain(int elevatorId) {
        for (ElevatorTMessage etm : elevatorInformation.values()) {
            if (etm.getElevatorId() == elevatorId) {
                etm.setMaintain(true);
                // OutputFormat.say("Set elevator "+elevatorId+" maintained!");
                maintainThreadId = etm.getElevator().getId();
                notifyThreadId = -2;
                notifyAll();
            }
        }
    }

    public synchronized boolean getMaintain(long threadId) {
        ElevatorTMessage etm = elevatorInformation.get(threadId);
        return etm.getMaintain();
    }

    public synchronized long getMaintainThreadId() { return this.maintainThreadId; }

    public synchronized void setMaintainThreadId(long threadId) { this.maintainThreadId = threadId; }

    public synchronized void removeEtm(Long threadId) {
        elevatorInformation.remove(threadId);
    }

    public synchronized void setEof(boolean eof) {
        this.eof = eof;
    }

    public synchronized void setAble() {
        this.ableCount++;
        if (eof && ableCount == acceptedCount) {
            setFinish(true);
            notifyThreadId = -1;
            maintainThreadId = -1;
            notifyAll();
        }
    }

    public synchronized int getAble() { return this.ableCount; }

    public synchronized void setAccepted() {
        this.acceptedCount++;
    }

    public synchronized int getAccepted() { return this.acceptedCount; }

}
