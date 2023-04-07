import java.util.ArrayList;

public class Answer implements Comparable<Answer> {
    int overTimes;
    ArrayList<Integer> overList;
    int getDownStation = -1;

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
}
