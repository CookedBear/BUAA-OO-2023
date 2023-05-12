package mine.tool;

import java.util.Objects;

public class Edge implements Comparable<Edge> {
    private final int id;
    private final int distance;
    private boolean isFirst;
    private final int from;

    public Edge(int id, int distance, int from) {
        this.id = id;
        this.distance = distance;
        this.isFirst = false;
        this.from = from;
    }

    public Edge(int id, int distance, boolean isFirst, int from) {
        this.id = id;
        this.distance = distance;
        this.isFirst = isFirst;
        this.from = from;
    }

    public int getId() { return this.id; }

    public int getDistance() { return this.distance; }

    public boolean getFirst() { return this.isFirst; }

    public int getFrom() { return this.from; }

    public int compareTo(Edge o) { return this.distance - o.distance; }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Edge edge = (Edge) o;
        return id == edge.id && distance == edge.distance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, distance);
    }
}
