public class ElevatorTMessage {
    private final Elevator elevator;
    private final int elevatorId;
    private boolean working;
    private boolean sleeping;
    private boolean isUp;
    private int floor;
    private int people;
    private final int maxPeople;
    private int reachingUp;       // farthest floor of the elevator can reach this turn
    private int reachingDown;
    private double movingTime;
    private boolean maintain = false;
    private boolean notify = false;

    private int[] upList = new int[13];
    private int[] downList = new int[13]; // [in,out)
    private Integer[] ableFloor;

    ElevatorTMessage(Elevator elevator, int elevatorId,
                     int floor, int maxPeople, double movingTime, Integer[] floorCode) {
        this.elevator = elevator;
        this.elevatorId = elevatorId;
        this.working = false;
        this.sleeping = true;
        this.isUp = true;
        this.floor = floor;
        this.people = 0;
        this.maxPeople = maxPeople;
        this.reachingUp = 0;
        this.reachingDown = 12;
        this.movingTime = movingTime;
        this.ableFloor = floorCode;
    }

    public Elevator getElevator() { return this.elevator; }

    public int getMaxPeople() { return this.maxPeople; }

    public synchronized int[] getUpList() { return this.upList; }

    public synchronized int[] getDownList() { return this.downList; }

    public boolean getWorking() { return this.working; }

    public synchronized boolean getIsUp() { return this.isUp; }

    public synchronized int getFl() { return this.floor; }

    public double getMvTm() { return this.movingTime; }

    // public int getReaching() { return this.reaching; }
    public synchronized int getRcUp() { return this.reachingUp; }

    public synchronized int getRcDn() { return this.reachingDown; }

    public void setWorking(boolean working) { this.working = working; }

    public void setIsUp(boolean isUp) { this.isUp = isUp; }

    public synchronized void setFloor(int floor) { this.floor = floor; }

    public int getElevatorId() { return this.elevatorId; }

    public synchronized void setMaintain(boolean maintain) { this.maintain = maintain; }

    public synchronized boolean getMaintain() { return this.maintain; }

    public synchronized void setNotify(boolean notify) { this.notify = notify; }

    public synchronized boolean getNotify() { return this.notify; }

    // public void setReaching(int reaching) { this.reaching = reaching; }
    public synchronized void setReachingUp(int reaching) { this.reachingUp = reaching; }

    public synchronized void setReachingDown(int reaching) { this.reachingDown = reaching; }

    public Integer[] getAbleFloor() { return this.ableFloor; }

    public boolean isSleeping() {
        return sleeping;
    }

    public void setSleeping(boolean sleeping) {
        this.sleeping = sleeping;
    }
}
