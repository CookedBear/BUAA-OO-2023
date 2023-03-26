public class ElevatorTMessage {
    private final Elevator elevator;
    private final long id;
    private boolean working;
    private boolean isUp;
    private int floor;
    private int people;
    private int reachingUp;       // farthest floor of the elevator can reach this turn
    private int reachingDown;

    private int[] upList = new int[12];
    private int[] downList = new int[12]; // [in,out)

    ElevatorTMessage(Elevator elevator) {
        this.elevator = elevator;
        this.id = elevator.getId();
        this.working = false;
        this.isUp = true;
        this.floor = 1;
        this.people = 0;
        this.reachingUp = 1;
        this.reachingDown = 11;
    }

    public Elevator getElevator() { return this.elevator; }

    public long getId() { return this.id; }

    public synchronized int[] getUpList() { return this.upList; }

    public synchronized int[] getDownList() { return this.downList; }

    public boolean getWorking() { return this.working; }

    public synchronized boolean getIsUp() { return this.isUp; }

    public synchronized int getFloor() { return this.floor; }

    public int getPeople() { return this.people; }

    // public int getReaching() { return this.reaching; }
    public synchronized int getReachingUp() { return this.reachingUp; }

    public synchronized int getReachingDown() { return this.reachingDown; }

    public void setWorking(boolean working) { this.working = working; }

    public void setIsUp(boolean isUp) { this.isUp = isUp; }

    public synchronized void setFloor(int floor) { this.floor = floor; }

    public void setPeople(int people) { this.people = people; }

    // public void setReaching(int reaching) { this.reaching = reaching; }
    public synchronized void setReachingUp(int reaching) { this.reachingUp = reaching; }

    public synchronized void setReachingDown(int reaching) { this.reachingDown = reaching; }
}
