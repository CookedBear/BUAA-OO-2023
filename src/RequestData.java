public class RequestData {
    private final int id;
    private int from;
    private final int to;
    private long threadId;

    RequestData(int id, int from, int to) {
        this.id = id;
        this.from = from;
        this.to = to;
    }

    public long getThreadId() { return this.threadId; }

    public int getId() { return id; }

    public int getFrom() { return from; }

    public void setFrom(int reFrom) { this.from = reFrom; }

    public int getTo() { return to; }

    public void setThreadId(Long threadId) { this.threadId = threadId; }

    public void requestIn(int elevatorId) {
        OutputFormat.in(id, from, elevatorId);
    }

    public void requestOut(int elevatorId) {
        OutputFormat.out(id, to, elevatorId);
    }

    public void requestOutTemp(int elevatorId, int currentFloor) {OutputFormat.out(id, currentFloor, elevatorId);}

    public boolean isUp() { return (to - from) > 0; }
}
