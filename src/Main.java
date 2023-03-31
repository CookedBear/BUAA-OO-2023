public class Main {
    public static void main(String[] args) {
        OutputFormat.start();
        Manager manager = new Manager();
        Producer producer = new Producer(manager);
        producer.start();

       for (int i = 1; i <= 6; i++) {
           Elevator elevator = new Elevator(i, 1, 6, 400, manager);
           manager.setElevatorInformation(i, elevator);
           elevator.start();
       }
    }
}
