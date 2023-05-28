import java.util.*;

public class Elevator {
    public final int elevatorId;
    public final int capacity;
    public final double speed;
    public final int[] ableFloor;
    public double addTime;
    public double maintainTime = -1;
    public boolean readMaintain = false;
    public int maintainMove = 0;

    public int currentFloor;
    public boolean open;
    public double timeStamp;
    public HashMap<Integer, Request> requestList = new HashMap<>();

    public double electricity = 0;

    Elevator(double time, int elevatorId, int capacity, double speed, int floorCode, int initFloor) {
        this.addTime = time;
        this.elevatorId = elevatorId;
        this.capacity = capacity;
        this.speed = speed;
        this.ableFloor = getAbleFloor(floorCode);
        this.currentFloor = initFloor;
    }

    public void move(double time, int floor) {
        if (floor > 11 || floor < 1) {
            OutputFormat.errorPrint(time, 3, "elevator cannot reach floor");
        } else if (time - timeStamp < speed - 0.01) {
            System.out.printf("%d - %f - %f - %f\n", elevatorId, time, timeStamp, speed);
            OutputFormat.errorPrint(time, 4, "elevator overspeed");
        } else if (Math.abs(floor - currentFloor) != 1) {
            OutputFormat.errorPrint(time, 5, "elevator overmoved");
        } if (readMaintain) {
            maintainMove++;
            if (maintainMove > 2) {
                OutputFormat.errorPrint(time, 6, "elevator didnot maintain in time");
            }
        }

        // ------------------------------- a legal move here -------------------------------

        this.currentFloor = floor;
        this.timeStamp = time;
        this.electricity += 0.4;
    }

    public void openDoor(double time, boolean open, int floor, HashMap<Integer, Floor> floorMap) {
        if (open == this.open) {
            OutputFormat.errorPrint(time, 8, "elevator" + (open ? "opened" : "closed") +"repeatedly");
        } else if (floor != this.currentFloor) {
            OutputFormat.errorPrint(time, 9, "elevator moved without print ARRIVE");
        } else if (time - timeStamp < 0.399 && !open) {
            OutputFormat.errorPrint(time, 10, "door closed overspeed");
        } else if (floorMap.get(floor).openElevator >= 4 && open) {
            OutputFormat.errorPrint(time, 15, "floor overcrowd with working elevator");
        } else if (open && ableFloor[currentFloor] == 0 && !readMaintain) {
            OutputFormat.errorPrint(time, 26, "elevator open at forbidden floor");
        }

        // ------------------------------- a simple legal doorMove here -------------------------------

        Record record = new Record(time, elevatorId, (open) ? 1 : 3);
        record.requestIds = new HashSet<>();
        record.requestIds.addAll(requestList.keySet());

        floorMap.get(floor).visitedRecord.add(record);

        if (open) {
            floorMap.get(floor).openElevator++;
        } else {
            floorMap.get(floor).openElevator--;
            floorMap.get(floor).adjustType();
        }

        // ------------------------------- a legal doorMove here -------------------------------

        this.open = open;
        this.timeStamp = time;
        this.electricity += 0.1;
    }

    public void addRequest(double time, HashMap<Integer, Request> requestMap, int requestId, int floor) {
        if (floor != this.currentFloor) {
            OutputFormat.errorPrint(time, 9, "elevator moved without print ARRIVE");
        } else if (!requestMap.containsKey(requestId)) {
            OutputFormat.errorPrint(time, 11, "elevator carried non-exist/unusable request");
        } else if (requestList.size() == capacity) {
            OutputFormat.errorPrint(time, 12, "elevator overweight");
        } else if (requestMap.get(requestId).from != currentFloor) {
            OutputFormat.errorPrint(time, 13, "add request at wrong floor");
        } else if (!open) {
            OutputFormat.errorPrint(time, 21, "request in while closing door");
        }

        // ------------------------------- a legal add here -------------------------------

        requestList.put(requestId, requestMap.get(requestId));
        requestList.get(requestId).actionTime = time;
        requestMap.remove(requestId);
    }

    public void minusRequest(double time, HashMap<Integer, Request> requestMap, int requestId, int floor) {
        if (floor != currentFloor) {
            OutputFormat.errorPrint(time, 9, "elevator moved without print ARRIVE");
        } else if (!requestList.containsKey(requestId)) {
            OutputFormat.errorPrint(time, 14, "elevator unload request without adding it");
        } else if (!open) {
            OutputFormat.errorPrint(time, 21, "request out while closing door");
        }

        // ------------------------------- a legal unload here -------------------------------

        if (currentFloor == requestList.get(requestId).to) {
            requestList.get(requestId).finalTime = time;
        }
        requestList.get(requestId).from = currentFloor;
        requestList.get(requestId).actionTime = time;
        requestMap.put(requestId, requestList.get(requestId));
        requestList.remove(requestId);
    }

    public void readyMaintain(double time) {
        if (time < maintainTime + 0.001) {
            OutputFormat.errorPrint(time, 7, "elevator maintain before inform");
        }
        this.readMaintain = true;
    }

    public void finishMaintain(double time) {
        if (this.open) {
            OutputFormat.errorPrint(time, 1, "maintain without closing"); // maintain without closing
        } else if (!this.requestList.isEmpty()) {
            OutputFormat.errorPrint(time, 2, "maintain without clearing inside Requests"); // maintain without clearing inside Requests
        }
    }

    public int[] getAbleFloor(int floorCode) {
        int[] returnFloor = new int[12];
        int code = floorCode;
        for (int i = 1; i <= 11; i++) {
            returnFloor[i] = code & 1;
            code /= 2;
        }
        return returnFloor;
    }
}
