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
    private int requestCount = 0;
    private int finishedCount = 0;
    private int[] inIng = new int[12];
    private int[] working = new int[12];
    private AlGraph eleGraph = new AlGraph();

    public synchronized void putRequest(RequestData rd) {
        // OutputFormat.say("xxx");

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
        eleGraph.startDfs(rd.getFrom(), rd.getTo());
        ArrayList<Answer> routes = eleGraph.printAns(rd.getFrom(), rd.getTo());
        if (routes.get(0).getOverTimes() == 0) {         // contain single-lines
            // OutputFormat.say("Has single route");
            Long threadId = getThreadIdSingle(rd);
            if (threadId != -2) {                   // try to use single-line
                return threadId;
            }
            while (!routes.isEmpty() && routes.get(0).getOverTimes() == 0) {
                routes.remove(0);
            }
        // single-line failed
        } else {                                    // need to change elevator
            int[] usedFloor = new int[12];
            for (Answer route : routes) {
                if (usedFloor[route.getGetDownStation()] == 0) {
                    usedFloor[route.getGetDownStation()] = 1;
                    rd.setTo(route.getGetDownStation());
                    Long threadId = getThreadIdSingle(rd);
                    if (threadId != -2) {                   // try to use this route
                        return threadId;
                    }
                }
            }
        }
        return (long) -2;                           // all fulled, finally cannot be processed
    }

    private Long getThreadIdSingle(RequestData rd) {
        int to = rd.getTo();
        int from = rd.getFrom();
        long threadId = -2;
        double dis = 191981000;
        double totalTime;
        for (ElevatorTMessage etm : elevatorInformation.values()) {
            int[] list;
            if (etm.getAbleFloor()[from] != 1 || etm.getAbleFloor()[to] != 1) {
                continue; }
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
                        stops++; } }
                if (b) { continue; }
                if (etm.getIsUp()) {
                    if (etm.getFl() < from || (etm.getFl() <= from && !etm.getWorking())) {
                        threadId = etm.getElevator().getId();
                        break;
                    } else { continue; }
                } else {                                // 反向电梯
                    if (etm.getRcDn() < from) {
                        totalTime = (from + etm.getFl() - 2 * etm.getRcDn()) * etm.getMvTm();
                    } else { totalTime = (from - etm.getFl()) * etm.getMvTm(); } }
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
                        break; } else if (list[i] != list[i + 1]) {
                        stops++; } }
                if (b) { continue; }
                if (!etm.getIsUp()) {
                    if (etm.getFl() > from || (etm.getFl() >= from && !etm.getWorking())) {
                        threadId = etm.getElevator().getId();
                        break;
                    } else { continue; }
                } else {                                // 反向电梯
                    if (etm.getRcUp() > from) {  // 延申
                        totalTime = (etm.getRcUp() * 2 - etm.getFl() - from) * etm.getMvTm();
                    } else { totalTime = (from - etm.getFl()) * etm.getMvTm(); } }
                if (totalTime < dis) {
                    threadId = etm.getElevator().getId();
                    dis = totalTime; } } }
        if (threadId == -2) { return threadId; }
        g(rd, to, from, threadId);
        return threadId; }

    public void g(RequestData rd, int to, int from, long threadId) {
        rd.setThreadId(threadId);
        if (rd.isUp()) {    // renew Up-Down-Floor
            if (to > elevatorInformation.get(threadId).getRcUp()) {
                elevatorInformation.get(threadId).setReachingUp(to); }
            if (from < elevatorInformation.get(threadId).getRcDn()) {
                elevatorInformation.get(threadId).setReachingDown(from); }
        } else {
            if (from > elevatorInformation.get(threadId).getRcUp()) {
                elevatorInformation.get(threadId).setReachingUp(from); }
            if (to < elevatorInformation.get(threadId).getRcDn()) {
                elevatorInformation.get(threadId).setReachingDown(to); } }
        if (rd.isUp()) {    // renew weight-List
            int[] list = elevatorInformation.get(threadId).getUpList();
            for (int i = from; i < to; i++) { list[i] = list[i] + 1; }
        } else {
            int[] list = elevatorInformation.get(threadId).getDownList();
            for (int i = from; i > to; i--) { list[i] = list[i] + 1; } }
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
        elevatorInformation.put(elevator.getId(),
                new ElevatorTMessage(
                    elevator, elevatorId,
                    elevator.getStartFloor(),
                    elevator.getMaxPeople(),
                    elevator.getMovingTime(),
                    elevator.getAbleFloor()));
        eleGraph.addNode(elevatorId, elevator.getAbleFloor());
    }

    public synchronized HashMap<Long, ElevatorTMessage> getElevatorInformation() {
        return this.elevatorInformation;
    }

    public synchronized void setFinish(Boolean finish) { this.finish = finish; }

    public synchronized boolean getFinish() {
        return this.finish && saturateList.isEmpty() && requestCount == finishedCount;
    }

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
        eleGraph.delNode(elevatorId);
    }

    public synchronized boolean getMaintain(long threadId) {
        ElevatorTMessage etm = elevatorInformation.get(threadId);
        return etm.getMaintain();
    }

    public synchronized long getMaintainThreadId() { return this.maintainThreadId; }

    public synchronized void setMaintainThreadId(long threadI) { this.maintainThreadId = threadI; }

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

    public synchronized void addIn(int currentFloor, boolean add) {
        if (add) {
            this.inIng[currentFloor]++;
        } else {
            this.inIng[currentFloor]--;
        }
        addWorking(currentFloor, add);
    }

    public synchronized void addWorking(int currentFloor, boolean add) {
        if (add) {
            this.working[currentFloor]++;
        } else {
            this.working[currentFloor]--;
        }
    }

    public synchronized int getIn(int currentFloor) {
        return this.inIng[currentFloor];
    }

    public synchronized int getWorking(int currentFloor) {
        return this.working[currentFloor];
    }

    public synchronized void addRequestCount() { requestCount++; }

    public synchronized void addFinishCount() { finishedCount++; }
}

/*
把图节点安放在新建的类内，并成为manager的一个属性
    节点初始化 - 最初每层不相连的锚定节点
    添加/维护电梯时的节点更新
        重构图方法，只考虑最少换乘

重写getThreadId()方法 - 核心点
    首先检查所有直达电梯，取出进行常规做法 - 非空就直接使用
    然后dfs找到所有可达路径（借助锚定节点）/bfs找到一条较短的可达路径
    按照换乘次数将路径排序，优先处理低换乘路线 - 考虑只处理最少换乘次数 ~ 次数 + 1的换乘，过多的换乘会极大地减弱调度的优势，同时可能等待更长的时间
        计算所有同级换乘路线中，本请求搭乘的第一部电梯中，常规做法最近的
    下客时判断 finalTo == to 如果相同则直接退出，不相同则需要令 from = to、to = finalTo 回填请求

 */