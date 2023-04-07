import com.oocourse.elevator3.TimableOutput;

public class OutputFormat {

    public static void start() { TimableOutput.initStartTimestamp(); }

    public static void open(int currentFloor, int elevatorId) {
        TimableOutput.println(String.format("OPEN-%d-%d",currentFloor, elevatorId));
    }

    public static void close(int currentFloor, int elevatorId) {
        TimableOutput.println(String.format("CLOSE-%d-%d",currentFloor, elevatorId));
    }

    public static void in(int id, int from, int elevatorId) {
        TimableOutput.println(String.format("IN-%d-%d-%d", id, from, elevatorId));
    }

    public static void out(int id, int to, int elevatorId) {
        TimableOutput.println(String.format("OUT-%d-%d-%d", id, to, elevatorId));
    }

    public static void arrive(int currentFloor, int elevatorId) {
        TimableOutput.println(String.format("ARRIVE-%d-%d", currentFloor, elevatorId));
    }

    public static void able(int elevatorId) {
        TimableOutput.println(String.format("MAINTAIN_ABLE-%d", elevatorId));
    }

    public static void say(String word) {
        TimableOutput.println(word);
    }

}
