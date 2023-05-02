package test;

public class Message {
    public int messageId;
    public int person1Id;
    public int target;
    public int type;
    public boolean sent = false;

    public Message(int messageId, int person1Id, int type, int target) {
        this.messageId = messageId;
        this.person1Id = person1Id;
        this.type = type;
        this.target = target;
    }
}
