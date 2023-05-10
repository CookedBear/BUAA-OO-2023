package mine.tool;

import java.util.Objects;

public class Edge implements Comparable<Edge> {
    private final int id;
    private final int distance;
    private boolean isFirst;

    public Edge(int id, int distance) {
        this.id = id;
        this.distance = distance;
        this.isFirst = false;
    }

    public Edge(int id, int distance, boolean isFirst) {
        this.id = id;
        this.distance = distance;
        this.isFirst = isFirst;
    }

    public int getId() { return this.id; }

    public int getDistance() { return this.distance; }

    public boolean getFirst() { return this.isFirst; }

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
