public class ElevatorTMessage {
    private final Elevator elevator;
    private final int elevatorId;
    private boolean working;
    private boolean isUp;
    private int floor;
    private int people;
    private final int maxPeople;
    private int reachingUp;       // farthest floor of the elevator can reach this turn
    private int reachingDown;
    private double movingTime;
    private boolean maintain = false;

    private int[] upList = new int[13];
    private int[] downList = new int[13]; // [in,out)

    ElevatorTMessage(Elevator elevator, int elevatorId, int floor, int maxPeople, double movingTime) {
        this.elevator = elevator;
        this.elevatorId = elevatorId;
        this.working = false;
        this.isUp = true;
        this.floor = floor;
        this.people = 0;
        this.maxPeople = maxPeople;
        this.reachingUp = 0;
        this.reachingDown = 12;
        this.movingTime = movingTime;
    }

    public Elevator getElevator() { return this.elevator; }

    public int getMaxPeople() { return this.maxPeople; }

    public synchronized int[] getUpList() { return this.upList; }

    public synchronized int[] getDownList() { return this.downList; }

    public boolean getWorking() { return this.working; }

    public synchronized boolean getIsUp() { return this.isUp; }

    public synchronized int getFloor() { return this.floor; }

    public double getMovingTime() { return this.movingTime; }

    // public int getReaching() { return this.reaching; }
    public synchronized int getReachingUp() { return this.reachingUp; }

    public synchronized int getReachingDown() { return this.reachingDown; }

    public void setWorking(boolean working) { this.working = working; }

    public void setIsUp(boolean isUp) { this.isUp = isUp; }

    public synchronized void setFloor(int floor) { this.floor = floor; }

    public int getElevatorId() { return this.elevatorId; }

    public synchronized void setMaintain(boolean maintain) { this.maintain = maintain; }

    public synchronized boolean getMaintain() { return this.maintain; }

    // public void setReaching(int reaching) { this.reaching = reaching; }
    public synchronized void setReachingUp(int reaching) { this.reachingUp = reaching; }

    public synchronized void setReachingDown(int reaching) { this.reachingDown = reaching; }
}
