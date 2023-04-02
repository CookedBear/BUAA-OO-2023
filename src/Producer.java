import com.oocourse.elevator2.ElevatorInput;
import com.oocourse.elevator2.ElevatorRequest;
import com.oocourse.elevator2.MaintainRequest;
import com.oocourse.elevator2.PersonRequest;
import com.oocourse.elevator2.Request;

public class Producer extends Thread {
    private final Manager manager;

    Producer(Manager manager) {
        this.manager = manager;
    }

    @Override
    public void run() {
        Request request;
        ElevatorInput input = new ElevatorInput(System.in);
        while ((request = input.nextRequest()) != null) {
            // still has requests!
            if (request instanceof PersonRequest) {
                RequestData rd = new RequestData(
                        ((PersonRequest) request).getPersonId(),
                        ((PersonRequest) request).getFromFloor(),
                        ((PersonRequest) request).getToFloor()
                );
                // OutputFormat.say("RD init!");
                manager.putRequest(rd);
            } else if (request instanceof ElevatorRequest) {

                int elevatorId = ((ElevatorRequest) request).getElevatorId();
                int floor = ((ElevatorRequest) request).getFloor();
                int maxPeople = ((ElevatorRequest) request).getCapacity();
                int movingTime = (int) (((ElevatorRequest) request).getSpeed() * 1000);
                // OutputFormat.say("Add a new elevator: "+elevatorId);
                Elevator elevator = new Elevator(elevatorId, floor, maxPeople, movingTime, manager);
                manager.setElevatorInformation(elevatorId, elevator);
                elevator.start();
            } else if (request instanceof MaintainRequest) {

                int elevatorId = ((MaintainRequest) request).getElevatorId();
                // OutputFormat.say("Remove old elevator: " + elevatorId);
                manager.setAccepted();
                manager.pushMaintain(elevatorId);
            }
            // OutputFormat.say("One sentence!");
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        manager.setEof(true);
        if (manager.getAccepted() == 0 ||
            manager.getAccepted() == manager.getAble()) {       // no maintain elevator + EOF = fin
            manager.setFinish(true);
            synchronized (manager) {
                manager.setNotifyThreadId(-1);
                manager.setMaintainThreadId(-1);
                manager.notifyAll();
            }
        }
        // procedure finished here
        //OutputFormat.say(currentThread().getName() + " finished!");
    }
}




/*
1-FROM-1-TO-11
2-FROM-1-TO-11
3-FROM-1-TO-11
4-FROM-1-TO-11
5-FROM-1-TO-11
6-FROM-1-TO-11
7-FROM-1-TO-11
8-FROM-1-TO-11
9-FROM-1-TO-11
10-FROM-1-TO-11
 */