import com.oocourse.elevator1.TimableOutput;

public class OutputFormat {

    public static void start() { TimableOutput.initStartTimestamp(); }
    public static void open(int currentFloor, int elevatorId) {
        TimableOutput.println(String.format("OPEN-%d层-电梯%d",currentFloor, elevatorId));
    }

    public static void close(int currentFloor, int elevatorId) {
        TimableOutput.println(String.format("CLOSE-%d层-电梯%d",currentFloor, elevatorId));
    }

    public static void in(int id, int from, int elevatorId) {
        TimableOutput.println(String.format("IN-乘客%d-%d层-电梯%d", id, from, elevatorId));
    }

    public static void out(int id, int to, int elevatorId) {
        TimableOutput.println(String.format("OUT-乘客%d-%d层-电梯%d", id, to, elevatorId));
    }

    public static void arrive(int currentFloor, int elevatorId) {
        TimableOutput.println(String.format("ARRIVE-%d层-电梯%d", currentFloor, elevatorId));
    }

    public static void say(String said) {
        TimableOutput.println(said);
    }
}
