public class RequestData {
    private final int id;
    private int from;
    private int to;
    private final int finalTo;
    private long threadId;
    private boolean fin;

    RequestData(int id, int from, int to) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.finalTo = to;
        fin = false;
    }

    public long getThreadId() { return this.threadId; }

    public int getId() { return id; }

    public int getFrom() { return from; }

    public void setFrom(int reFrom) { this.from = reFrom; }

    public int getTo() { return to; }

    public void setTo(int to) { this.to = to; }

    public void setThreadId(Long threadId) { this.threadId = threadId; }

    public void requestIn(int elevatorId) {
        OutputFormat.in(id, from, elevatorId);
    }

    public void requestOut(int elevatorId) {
        OutputFormat.out(id, to, elevatorId);
    }

    public void requestOutTemp(int elevatorId, int currentFloor) {
        OutputFormat.out(id, currentFloor, elevatorId);
    }

    public boolean isUp() { return (to - from) > 0; }

    public boolean getFin() { return this.fin; }

    public void setFin(boolean fin) { this.fin = fin; }

    public RequestData cloned() {
        return new RequestData(this.id, this.from, this.to);
    }

    public boolean isFinal() { return this.to == this.finalTo; }

    public void reMake() {
        this.from = this.to;
        this.to = this.finalTo;
        this.threadId = 0;
    }
}
