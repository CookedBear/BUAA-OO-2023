import java.util.HashMap;
import java.util.Set;

public class Record {
    public double time;
    public int elevatorId;
    public int type;
    // 1: working
    // 2: close
    // 3: loading
    public Set<Integer> requestIds;

    Record(double time, int elevatorId, int type) {
        this.time = time;
        this.elevatorId = elevatorId;
        this.type = type;
    }

}
