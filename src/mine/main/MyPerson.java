package mine.main;

import com.oocourse.spec2.main.Message;
import com.oocourse.spec2.main.Person;
import mine.exceptions.MyEqualRelationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MyPerson implements Person {
    private final int id;
    private final String name;
    private final int age;
    private final HashMap<Integer, Person> acquaintance = new HashMap<>();
    private final HashMap<Integer, Integer> value = new HashMap<>();
    private int socialValue;
    private int valueSum;
    private int coupleId;
    private boolean cachedCoupleId = false;
    private int bestValue = -1;
    private final LinkedList<Message> messages = new LinkedList<>();

    public MyPerson(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.socialValue = 0;
        this.valueSum = 0;
        this.coupleId = 0;
    }

    public int getId() { return this.id; }

    public String getName() { return this.name; }

    public int getAge() { return this.age; }

    public boolean equals(Object obj) {
        if (!(obj instanceof Person)) {
            return false;
        } else {
            return (((Person) obj).getId() == id);
        }
    }

    public boolean isLinked(Person person) {
        if (person.equals(this)) {
            return true;
        } else {
            return acquaintance.containsKey(person.getId());
        }
    }

    public int queryValue(Person person) {
        int values = 0;
        for (Person p : acquaintance.values()) {
            if (person.equals(p)) {
                values = value.get(person.getId());
            }
        }
        return values;
    }

    /**
     * @param o the object to be compared.
     */
    @Override
    public int compareTo(Person o) { return this.id - o.getId(); }

    /*@ public normal_behavior
      @ assignable socialValue;
      @ ensures socialValue == \old(socialValue) + num;
      @*/
    public void addSocialValue(int num) { this.socialValue += num; }

    //@ ensures \result == socialValue;
    public int getSocialValue() { return this.socialValue; }

    /*@ ensures (\result.size() == messages.length) &&
  @           (\forall int i; 0 <= i && i < messages.length;
  @             messages[i] == \result.get(i));
  @*/
    public List<Message> getMessages() { return new ArrayList<>(this.messages); }

    /*@ public normal_behavior
      @ assignable \nothing;
      @ ensures (\forall int i; 0 <= i && i < messages.length && i <= 4;
      @           \result.contains(messages[i]) && \result.get(i) == messages[i]);
      @ ensures \result.size() == ((messages.length < 5)? messages.length: 5);
      @*/
    public List<Message> getReceivedMessages() {
        int size = Math.min(messages.size(), 5);
        ArrayList<Message> returnList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            returnList.add(messages.get(i));
        }
        return returnList;
        // 可以缓存 returnList 以加速访问
    }

    public void addRelation(MyPerson p2, int values) throws MyEqualRelationException {
        if (acquaintance.containsKey(p2.id)) {
            throw new MyEqualRelationException(id, p2.id);
        }

        acquaintance.put(p2.id, p2);
        value.put(p2.id, values);
        valueSum += values;
        if (cachedCoupleId) {
            if (values > bestValue) {
                coupleId = p2.getId();
                bestValue = values;
            }
        } else {
            coupleId = getCouple();
        }
    }

    public HashMap<Integer, Person> getAcquaintance() { return this.acquaintance; }

    public HashMap<Integer, Integer> getValue() { return this.value; }

    // cached
    public int getValueSum() { return valueSum; }

    public void addMessage(Message m) { this.messages.addFirst(m); }

    // cached
    public int getCouple() {
        if (!cachedCoupleId) {
            cachedCoupleId = true;
            bestValue = -1;
            for (Person p2 : acquaintance.values()) {
                int values = value.get(p2.getId());
                if (values > bestValue) {
                    coupleId = p2.getId();
                    bestValue = values;
                }
            }
        }
        return coupleId;
    }

    public void addValue(int id, int value) {
        valueSum += value;
        cachedCoupleId = false;

        this.value.put(id, this.value.get(id) + value);
    }

    public void delRelation(int id) {
        valueSum -= value.get(id);
        cachedCoupleId = false;

        value.remove(id);
        acquaintance.remove(id);
    }
}
