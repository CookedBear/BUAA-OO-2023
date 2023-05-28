import java.util.ArrayList;
import java.util.Set;

public class Floor {
    public int floor;
    public int openElevator;
    public ArrayList<Record> visitedRecord = new ArrayList<>();

    Floor(int floor) {
        this.floor = floor;
    }

    public void adjustType() {
        int pos = visitedRecord.size() - 1;
        int elevatorId = visitedRecord.get(pos).elevatorId;
        Set<Integer> fin = visitedRecord.get(pos).requestIds;
        Set<Integer> srt = null;
        for (; pos >= 0; pos--) {
            if (visitedRecord.get(pos).elevatorId == elevatorId && visitedRecord.get(pos).type == 1) {
                srt = visitedRecord.get(pos).requestIds;
                break;
            }
        }
        if (srt == null) {
            OutputFormat.errorPrint(-1, -1, "Other error");
        } else {
            if (fin.containsAll(srt)) {
                visitedRecord.get(pos).type = 2;
                visitedRecord.get(visitedRecord.size() - 1).type = -2;
            }
        }
    }

}
