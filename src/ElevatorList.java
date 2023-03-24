import java.util.ArrayList;
import java.util.HashMap;

public class ElevatorList {
    public ArrayList<Elevator> elevatorArrayList = new ArrayList<>();
    public final int MAXPEOPLE = 6;
    public double ele = 0;
    public double time = 0;

    ElevatorList() {
        for (int i = 0; i < 6; i++) {
            elevatorArrayList.add(new Elevator());
        }
    }

    public boolean requestAction(int action, int elevatorId, int floor, double time, int requestId, HashMap<Integer, Request> requestList) {
        Elevator elevator = elevatorArrayList.get(elevatorId - 1);
        int prevFloor = elevator.floor;
        int people = elevator.requests.size();
        boolean doorOpen = elevator.openDoor;

        if (prevFloor != floor) {
            System.out.println("Move without printing ARRIVE!");
            return true;
        }
        if (!doorOpen) {
            System.out.println("Elevator " + elevatorId + " process request without open door!");
            return true;
        }

        /*
            IN = 1
            OUT = 2
         */

        if (action == 1) {
            if (people >= MAXPEOPLE) {
                System.out.println("Elevator " + elevatorId + " overweight at floor " + floor + "!");
                return true;
            } else {
                if (elevator.requests.containsKey(requestId)) {
                    System.out.println("Request " + requestId + " over-processed!");
                    return true;
                } else if (floor != requestList.get(requestId).from) {
                    System.out.println(requestList.get(requestId).from);
                    System.out.println("Request " + requestId + " collected at wrong place!");
                    return true;
                }
                elevator.requests.put(requestId, new Request(floor, -1, requestId, time));
            }
        } else {
            if (!elevator.requests.containsKey(requestId)) {
                System.out.println("Request " + requestId + " dropped without in!");
                return true;
            } else if (floor != requestList.get(requestId).to) {
                elevator.requests.remove(requestId);
                requestList.get(requestId).from = floor;
                return false;
//                System.out.println("Request " + requestId + " dropped at wrong place!");
//                return true;
            }
            elevator.requests.remove(requestId);
            requestList.remove(requestId);
        }
        this.time = time;
        return false;
    }

    public boolean elevatorAction(int action, int elevatorId, int floor, double time) {
        Elevator elevator = elevatorArrayList.get(elevatorId - 1);

        /*  ARRIVE = 3  *
         *  OPEN = 4    *
         *  CLOSE = 5   */

        int prevFloor = elevator.floor;
        double doorTime = elevator.doortime;
        switch (action) {
            case 3:
                if (Math.abs(prevFloor - floor) > 1) {
                    System.out.println("Elevator " + elevatorId + " overmoved!");
                } else if (time * 10000 - doorTime * 10000 < 4000) {
                    System.out.println("Elevator " + elevatorId + " overspeed!");
                } else {
                    elevator.floor = floor;                 // move successfully
                    elevator.doortime = time;
                    ele += 0.4;
                }
                break;
            case 4:
                if (elevator.openDoor) {
                    System.out.println("Open door two times!");
                    return true;
                } else if (prevFloor != floor) {
                    System.out.println("Move without printing ARRIVE!");
                    return true;
                } else {
                    elevator.openDoor = true;               // open successfully
                    elevator.doortime = time;
                    ele += 0.1;
                }
                break;
            case 5:
                if (!elevator.openDoor) {
                    System.out.println("Close door two times!");
                    return true;
                } else if (prevFloor != floor) {
                    System.out.println("Move without printing ARRIVE!");
                    return true;
                } else if (time - doorTime < 0.4) {
                    System.out.println("Elevator " + elevatorId + " closed door too fast!");
                } else {
                    elevator.openDoor = false;              // close successfully
                    elevator.doortime = time;
                    ele += 0.1;
                }
                break;
            default:
                System.out.println("Wrong input format!");
                return true;
        }
        this.time = time;
        return false;
    }
}
