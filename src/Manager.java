import com.oocourse.elevator1.ElevatorInput;

import java.util.ArrayList;
import java.util.Arrays;
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
        if (notifyThreadId == -2) {     // cannot process this request now
            saturateList.add(rd);
        } else {                        // can find the suitable elevator now
            REQUESTLIST.add(rd);
            //OutputFormat.say("notifyAll by Request " + rd.getId() + " !");
            notifyAll();
        }

    }

    public synchronized void flushSaturateList(Long threadId) {      // flush after every successful turn
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
                //OutputFormat.say("successfully re-put Request " + rd.getId() + " !");
                REQUESTLIST.add(rd);
                saturateList.remove(rd);
                //OutputFormat.say("normally notifyAll by Request " + rd.getId() + " !");
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

//        cnt++;
//        if (cnt >= 6) {
//            cnt = 0;
//            // OutputFormat.say("cannot process Request: " + rd.getId());
//            // return (long)-1;
//        }
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
                        break;              // if overweight then find next elevator
                    }
                }
                if (b) {
                    continue;
                }
                if (etm.getIsUp()) {
                    if (etm.getFloor() <= from) {       // 出现顺向截梯，终止寻找
                        threadId = etm.getElevator().getId();
                        break;
                    } else {                            // 不保存顺向错位
                        continue;
                    }
                } else {                                // 反向电梯
                    int distance;
                    if (etm.getReachingDown() >= from) {
                        distance = from - etm.getFloor();
                    } else {
                        distance = from + etm.getFloor() - 2 * etm.getReachingDown();
                    }
                    if (distance < dis) {
                        threadId = etm.getElevator().getId();
                        dis = distance;
                    }
                }
            } else {
                list = etm.getDownList();
                boolean b = false;
                for (int i = from; i > to; i--) {
                    if (list[i] >= 6) {
                        b = true;
                        break;              // overweight
                    }
                }
                if (b) {
                    continue;
                }
                if (!etm.getIsUp()) {
                    if (etm.getFloor() >= from) {       // 出现顺向截梯，终止寻找
                        threadId = etm.getElevator().getId();
                        break;
                    } else {                            // 不保存顺向错位
                        continue;
                    }
                } else {                                // 反向电梯
                    int distance;
                    if (etm.getReachingUp() <= from) {  // 延申
                        distance = from - etm.getFloor();
                    } else {                            // 折返
                        distance = etm.getReachingUp() * 2 - etm.getFloor() - from;
                    }
                    //System.out.println(distance);
                    if (distance < dis) {
                        threadId = etm.getElevator().getId();
                        dis = distance;
                    }
                }
            }

        }
        if (threadId == -2) {                               // all full
            //OutputFormat.say("cannot process Request: " + rd.getId() + "!!");
            return threadId;
        }


//        if (elevatorInformation.get(threadIdList[cnt]).getPeople() == 6) {
//            OutputFormat.say("cannot process Request: " + rd.getId() + "!!");
//            return (long)-1;
//        }
        /*
            A到了，叫了flush函数处理数据，数据该给B，B还没到，处理失败，等着
         */

        rd.setThreadId(threadId);
        if (rd.isUp()) {    // renew Up-Down-Floor
            if (to > elevatorInformation.get(threadId).getReachingUp()) {
                elevatorInformation.get(threadId).setReachingUp(to);
            }
            if (from < elevatorInformation.get(threadId).getReachingDown()) {
                elevatorInformation.get(threadId).setReachingDown(from);
            }
        } else {
            if (from > elevatorInformation.get(threadId).getReachingUp()) {
                elevatorInformation.get(threadId).setReachingUp(from);
            }
            if (to < elevatorInformation.get(threadId).getReachingDown()) {
                elevatorInformation.get(threadId).setReachingDown(to);
            }
        }

        if (rd.isUp()) {    // renew weight-List
            int[] list = elevatorInformation.get(threadId).getUpList();
            for (int i = from; i < to; i++) {
                list[i]++;
            }
        } else {
            int[] list = elevatorInformation.get(threadId).getDownList();
            for (int i = from; i > to; i--) {
                list[i]++;
            }
        }

        // System.out.println(elevatorInformation.get(threadIdList[cnt]).getReachingUp());
        // System.out.println(elevatorInformation.get(threadIdList[cnt]).getReachingDown());
        // elevatorInformation.get(threadId).setPeople(elevatorInformation.get(threadId).getPeople() + 1);
        return threadId;
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

    public synchronized boolean getFinish() { return this.finish && saturateList.isEmpty(); }

    public synchronized long getNotifyThreadId() { return this.notifyThreadId; }

    public synchronized void setNotifyThreadId(long threadId) { this.notifyThreadId = threadId; }
}
