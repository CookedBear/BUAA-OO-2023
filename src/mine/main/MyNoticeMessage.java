package mine.main;

import com.oocourse.spec3.main.Group;
import com.oocourse.spec3.main.Message;
import com.oocourse.spec3.main.NoticeMessage;
import com.oocourse.spec3.main.Person;

public class MyNoticeMessage implements NoticeMessage {
    private final int id;
    private final int type;
    private final int socialValue;
    private final Person person1;
    private final Person person2;
    private final Group group;
    private final String string;

    public MyNoticeMessage(int messageId, String noticeString,
                           Person messagePerson1, Person messagePerson2) {
        this.id = messageId;
        this.type = 0;
        this.socialValue = noticeString.length();
        this.person1 = messagePerson1;
        this.person2 = messagePerson2;
        this.group = null;
        this.string = noticeString;
    }

    public MyNoticeMessage(int messageId, String noticeString,
                           Person messagePerson1, Group messageGroup) {
        this.id = messageId;
        this.type = 1;
        this.socialValue = noticeString.length();
        this.person1 = messagePerson1;
        this.person2 = null;
        this.group = messageGroup;
        this.string = noticeString;
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

    public String getString() { return this.string; }
}
