public class Request {
    public int requestId;
    public int from;
    public int to;
    public double inputTime;
    public double finalTime = -1;
    public double actionTime = -1;

    Request(int id, int from, int to, double inputTime) {
        this.requestId = id;
        this.from = from;
        this.to = to;
        this.inputTime = inputTime;
    }
}
