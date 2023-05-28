import java.util.HashMap;

public class Elevator {
    public int elevatorId;
    public int floor = 1;
    public int capacity;
    public int movingTime;
    public HashMap<Integer, Request> requests = new HashMap<>();
    public boolean openDoor = false;
    public double doortime;
    public double addTime;
    public int maintainCnt;
    public boolean able = false;
    public boolean used = false;

    Elevator(int elevatorId, int initFloor, int capacity, int movingTime) {
        this.elevatorId = elevatorId;
        this.floor = initFloor;
        this.capacity = capacity;
        this.movingTime = movingTime;
        this.maintainCnt = -1;
        this.addTime = 0;
    }
}
