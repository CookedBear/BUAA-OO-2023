package test;

public class Arc {
    public int p1Id;
    public int p2Id;
    public int value;

    public Arc(int p1Id, int p2Id, int value) {
        this.p1Id = Math.min(p1Id, p2Id);
        this.p2Id = Math.max(p1Id, p2Id);
        this.value = value;
    }

    public Arc() {

    }
}
