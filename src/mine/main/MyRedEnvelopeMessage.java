package mine.main;

import com.oocourse.spec3.main.Group;
import com.oocourse.spec3.main.Message;
import com.oocourse.spec3.main.Person;
import com.oocourse.spec3.main.RedEnvelopeMessage;

public class MyRedEnvelopeMessage implements RedEnvelopeMessage {
    private final int id;
    private final int type;
    private final int socialValue;
    private final Person person1;
    private final Person person2;
    private final Group group;
    private final int money;

    public MyRedEnvelopeMessage(int messageId, int luckyMoney,
                                Person messagePerson1, Person messagePerson2) {
        this.id = messageId;
        this.type = 0;
        this.socialValue = luckyMoney * 5;
        this.person1 = messagePerson1;
        this.person2 = messagePerson2;
        this.group = null;
        this.money = luckyMoney;
    }

    public MyRedEnvelopeMessage(int messageId, int luckyMoney,
                                Person messagePerson1, Group messageGroup) {
        this.id = messageId;
        this.type = 1;
        this.socialValue = luckyMoney * 5;
        this.person1 = messagePerson1;
        this.person2 = null;
        this.group = messageGroup;
        this.money = luckyMoney;
    }

    public int getType() { return this.type; }

    public int getId() { return this.id; }

    public int getSocialValue() { return this.socialValue; }

    public Person getPerson1() { return this.person1; }

    public Person getPerson2() { return this.person2; }

    public Group getGroup() { return this.group; }

    public boolean equals(Object obj) {
        if (obj instanceof Message) {
            return (((Message) obj).getId() == this.id);
        } else {
            return false;
        }
    }

    public int getMoney() { return this.money; }
}
