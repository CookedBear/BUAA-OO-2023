public class Request {
    public int from;
    public int to;
    public int id;
    public double time;
    public int elevatorId;
    public boolean inElevator = false;
    public boolean timeSafe;
    public boolean processed = false;


    Request(int from, int to, int id, double time) {
        this.from = from;
        this.to = to;
        this.id = id;
        this.time = time;
    }

    public double calculateElc() {
        return 0.4 * Math.abs(from - to) + 0.1 * 2;
    }

    public double calculateTime() {
        return Math.abs(from - to) * 0.4;
    }
}
