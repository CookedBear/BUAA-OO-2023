package mine.tool;

import java.util.Objects;

public class Arc implements Comparable<Arc> {
    private final int person1;
    private final int person2;
    private int value;

    public Arc(int p1, int p2, int value) {
        this.person1 = Math.min(p1, p2);
        this.person2 = Math.max(p1, p2);
        this.value = value;
    }

    public void updValue(int value) { this.value = value; }

    public int getPerson1() { return this.person1; }

    public int getPerson2() { return this.person2; }

    public int getValue() { return this.value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Arc arc = (Arc) o;
        return person1 == arc.person1 && person2 == arc.person2;
    }

    @Override
    public int hashCode() { return Objects.hash(person1, person2); }

    public int compareTo(Arc a) { return this.value - a.value; }
}
