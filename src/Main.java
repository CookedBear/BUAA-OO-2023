import com.oocourse.elevator1.*;

public class Main {
    public static void main(String[] args) {
        OutputFormat.start();
        Manager manager = new Manager();
        Producer producer = new Producer(manager);
        producer.start();

        Elevator elevator1 = new Elevator(1, manager);
        Elevator elevator2 = new Elevator(2, manager);
        Elevator elevator3 = new Elevator(3, manager);
         Elevator elevator4 = new Elevator(4, manager);
         Elevator elevator5 = new Elevator(5, manager);
         Elevator elevator6 = new Elevator(6, manager);

        manager.setElevatorInformation(elevator1);
        manager.setElevatorInformation(elevator2);
        manager.setElevatorInformation(elevator3);
         manager.setElevatorInformation(elevator4);
         manager.setElevatorInformation(elevator5);
         manager.setElevatorInformation(elevator6);

        elevator1.start();
        elevator2.start();
        elevator3.start();
         elevator4.start();
         elevator5.start();
         elevator6.start();

        //TimableOutput.println(String.format("ARRIVE-%d-%s", 1, "A"));
    }
}