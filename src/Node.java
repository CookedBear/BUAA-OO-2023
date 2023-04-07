import java.util.Arrays;
import java.util.Objects;

class Node {
    private final int elevatorId;
    private final Integer[] floor;

    Node(int elevatorId, Integer[] floor) {
        this.elevatorId = elevatorId;
        this.floor = floor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node vNode = (Node) o;
        return elevatorId == vNode.elevatorId && floor == vNode.floor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(elevatorId, Arrays.hashCode(floor));
    }

    @Override
    public String toString() {
        return String.format("E %d - F %s", elevatorId, Arrays.toString(floor));
    }

    public Integer[] getFloor() { return this.floor; }

    public int getElevatorId() { return this.elevatorId; }
}