import com.oocourse.elevator1.ElevatorInput;
import com.oocourse.elevator1.PersonRequest;

public class Producer extends Thread {
    private final Manager manager;

    Producer(Manager manager) {
        this.manager = manager;
    }

    @Override
    public void run() {
        PersonRequest pr;
        ElevatorInput input = new ElevatorInput(System.in);
        while ((pr = input.nextPersonRequest()) != null) {
            // still has requests!
            RequestData rd = new RequestData(pr.getPersonId(), pr.getFromFloor(), pr.getToFloor());
            manager.putRequest(rd);
        }
        manager.setFinish(true);
        // procedure finished here
        //OutputFormat.say(currentThread().getName() + " finished!");
    }
}
