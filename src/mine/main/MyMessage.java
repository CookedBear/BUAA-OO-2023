package mine.main;

import com.oocourse.spec2.main.Group;
import com.oocourse.spec2.main.Message;
import com.oocourse.spec2.main.Person;

public class MyMessage implements Message {
    /*@ public instance model int id;
      @ public instance model int socialValue;
      @ public instance model int type;
      @ public instance model non_null Person person1;
      @ public instance model nullable Person person2;
      @ public instance model nullable Group group;
      @*/
    private final int id;
    private final int type;
    private final int socialValue;
    private final Person person1;
    private final Person person2;
    private final Group group;

    public MyMessage(int messageId, int messageSocialValue,
                     Person messagePerson1, Person messagePerson2) {
        this.id = messageId;
        this.type = 0;
        this.socialValue = messageSocialValue;
        this.person1 = messagePerson1;
        this.person2 = messagePerson2;
        this.group = null;
    }

    public MyMessage(int messageId, int messageSocialValue,
                     Person messagePerson1, Group messageGroup) {
        this.id = messageId;
        this.type = 1;
        this.socialValue = messageSocialValue;
        this.person1 = messagePerson1;
        this.person2 = null;
        this.group = messageGroup;
    }

    public /*@ pure @*/ int getType() { return this.type; }

    public /*@ pure @*/ int getId() { return this.id; }

    public /*@ pure @*/ int getSocialValue() { return this.socialValue; }

    public /*@ pure @*/ Person getPerson1() { return this.person1; }

    public /*@ pure @*/ Person getPerson2() { return this.person2; }

    public /*@ pure @*/ Group getGroup() { return this.group; }

    public /*@ pure @*/ boolean equals(Object obj) {
        if (obj instanceof Message) {
            return (((Message) obj).getId() == this.id);
        } else {
            return false;
        }
    }
}
