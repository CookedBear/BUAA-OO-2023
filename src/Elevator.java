import java.util.HashMap;

public class Elevator {
    public int floor = 1;
    public HashMap<Integer, Request> requests = new HashMap<>();
    public boolean openDoor = false;
    public double doortime;
}
