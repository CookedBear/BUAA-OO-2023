package mine.main;

import com.oocourse.spec3.main.Group;
import com.oocourse.spec3.main.Person;
import com.oocourse.spec3.main.RedEnvelopeMessage;
import mine.tool.Arc;

import java.util.HashMap;

public class MyGroup implements Group {
    private final int id;
    private final HashMap<Integer, Person> people = new HashMap<>();
    private int ageSum = 0;
    private int ageVar = 0;
    private boolean cachedValueSum = true;
    private boolean cachedAgeVar = true;
    private HashMap<Arc, Integer> arcPools;

    public MyGroup(int id) { this.id = id; }

    public int getId() { return this.id; }

    public boolean equals(Object obj) {
        if (obj instanceof MyGroup) {
            return (((MyGroup) obj).getId() == this.id);
        } else {
            return false;
        }
    }

    public void addPerson(Person person) {
        people.put(person.getId(), person);
        ageSum += person.getAge();
        flush();
    }

    public boolean hasPerson(Person person) {
        return people.containsKey(person.getId());
    }

    public int getValueSum() {
        int valueSum = 0;
        //        for (Person p1 : people.values()) {
        //            for (Person p2 : people.values()) {
        //                if (p1.isLinked(p2)) {
        //                    valueSum += p1.queryValue(p2);
        //                }
        //            }
        //        }
        for (Arc arc : arcPools.keySet()) {
            if (people.containsKey(arc.getPerson1()) && people.containsKey(arc.getPerson2())) {
                valueSum += (2 * arcPools.get(arc));
            }
        }
        return valueSum;
    }

    public int getAgeMean() {
        if (people.isEmpty()) {
            return 0;
        } else {
            return ageSum / people.size();
        }
    }

    public int getAgeVar() {
        if (people.isEmpty()) {
            return 0;
        } else {
            if (!cachedAgeVar) {
                int ageMean = getAgeMean();
                int ageVar = 0;
                for (Person person : people.values()) {
                    ageVar += ((person.getAge() - ageMean) * (person.getAge() - ageMean));
                }
                cachedAgeVar = true;
                this.ageVar = ageVar / people.size();
            }
            return ageVar;
        }
    }

    public void delPerson(Person person) {
        people.remove(person.getId());
        ageSum -= person.getAge();
        flush();
    }

    public int getSize() { return this.people.size(); }

    public boolean containsPerson(int id) { return people.containsKey(id); }

    public HashMap<Integer, Person> getPeople() { return this.people; }

    public void flush() {
        cachedValueSum = false;
        cachedAgeVar = false;
    }

    public void flushCachedValueSum() { cachedValueSum = false; }

    public void loadArcPools(HashMap<Arc, Integer> arcPools) { this.arcPools = arcPools; }

    public void sendR(int senderId, RedEnvelopeMessage red) {
        int piece = red.getMoney() / people.size();
        Person sender = people.get(senderId);
        sender.addMoney(- piece * people.size());
        for (Person receiver : people.values()) {
            receiver.addMoney(piece);
        }
    }
}
