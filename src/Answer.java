import java.util.ArrayList;

public class Answer implements Comparable<Answer> {
    private int overTimes;
    private ArrayList<Integer> overList;
    private int getDownStation = -1;

    Answer(int overTimes, ArrayList<Integer> overList) {
        this.overList = overList;
        this.overTimes = overTimes;
    }

    @Override
    public int compareTo(Answer answer) {
        if (overTimes != answer.overTimes) {
            return Integer.compare(overTimes, answer.overTimes);
        } else {
            return getDownStation - answer.getDownStation;
        }
    }

    public int getOverTimes() { return this.overTimes; }

    public void setOverTimes(int overTimes) { this.overTimes = overTimes; }

    public ArrayList<Integer> getOverList() { return this.overList; }

    public void setOverList(ArrayList<Integer> overList) { this.overList = overList; }

    public int getGetDownStation() { return this.getDownStation; }

    public void setGetDownStation(int getDownStation) { this.getDownStation = getDownStation; }
}
