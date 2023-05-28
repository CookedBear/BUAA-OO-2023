import java.util.ArrayList;
import java.util.HashMap;

public class ElevatorList {
    public ArrayList<Elevator> elevatorArrayList = new ArrayList<>();
    public double ele = 0;
    public double time = 0;
    public int moveTimes = 0;
    public double moveUsedTime = 0;
    public int doorTimes = 0;
    public double doorUsedTime = 0;
    public int acceptedtimes = 0;
    public int abletimes = 0;

    ElevatorList() {
        for (int i = 1; i <= 6; i++) {
            Elevator e = new Elevator(i, 1, 6, 400);
            e.able = true;
            elevatorArrayList.add(e);
        }
    }

    public boolean requestAction(int action, int elevatorId, int floor, double time, int requestId, HashMap<Integer, Request> requestList) {
        Elevator elevator = null;
        for (Elevator e : elevatorArrayList) {
            if (e.elevatorId == elevatorId) {
                elevator = e;
            }
        }

        if (!elevator.able) {
            if (elevator.addTime < time) {
                elevator.able = true;
                elevator.used = true;
            } else {
                System.out.println("Elevator " + elevatorId + " is used before ADD!");
            }
        }
        int prevFloor = elevator.floor;
        int people = elevator.requests.size();
        boolean doorOpen = elevator.openDoor;
        if (!requestList.containsKey(requestId)) {
            System.out.println("Moving non-exist Request: " + requestId);
            return true;
        }

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
            if (people >= elevator.capacity) {
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
        Elevator elevator = null;
        for (Elevator e : elevatorArrayList) {
            if (e.elevatorId == elevatorId) {
                elevator = e;
            }
        }

        if (!elevator.able) {
            if (elevator.addTime < time) {
                elevator.able = true;
                elevator.used = true;
            } else {
                System.out.println("Elevator " + elevatorId + " is used before ADD!");
            }
        }

        /*  ARRIVE = 3  *
         *  OPEN = 4    *
         *  CLOSE = 5   */

        int prevFloor = elevator.floor;
        double doorTime = elevator.doortime;
        if (floor < 1) {
            System.out.println("Elevator " + elevatorId + " is made in hell!");
        } else if (floor > 11) {
            System.out.println("Elevator " + elevatorId + " is made in heaven!");
        }
        switch (action) {
            case 3:
                if (elevator.maintainCnt != -1) {
                    elevator.maintainCnt--;
                }
                if (Math.abs(prevFloor - floor) > 1) {
                    System.out.println("Elevator " + elevatorId + " overmoved!");
                    return true;
                } else if (time * 10000 - doorTime * 10000 < elevator.movingTime * 10 - 1) {
                    System.out.println("Elevator " + elevatorId + " overspeed!");
                    return true;
                } else if (elevator.maintainCnt == 0) {
                    System.out.println("Elevator " + elevatorId + " did not maintain in time!");
                    return true;
                }else {
                    elevator.floor = floor;                 // move successfully
                    moveUsedTime += (time - doorTime);
                    elevator.doortime = time;
                    ele += 0.4;
                    moveTimes++;
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
                } else if (time * 10000 - doorTime * 10000 < 3999) {
                    System.out.println("Elevator " + elevatorId + " closed door too fast!");
                } else {
                    elevator.openDoor = false;              // close successfully
                    doorUsedTime += (time - doorTime);
                    elevator.doortime = time;
                    ele += 0.1;
                    doorTimes++;

                }
                break;
            default:
                System.out.println("Wrong input format!");
                return true;
        }
        this.time = time;
        return false;
    }

    public void checkMaintain(Integer elevatorId) {
        Elevator elevator = null;
        for (Elevator e : elevatorArrayList) {
            if (e.elevatorId == elevatorId) {
                elevator = e;
            }
        }
        if (!elevator.able && elevator.used) {
            System.out.println("Maintain Elevator " + elevatorId + " for too much times!");
        } else {
            elevator.able = false;
            if (!elevator.requests.isEmpty()) {
                System.out.println("Elevator " + elevatorId + " is not EMPTY before maintain!");
            } else if (elevator.openDoor) {
                System.out.println("Elevator " + elevatorId + " didnot close door before maintain!");
            }
        }
    }
}
