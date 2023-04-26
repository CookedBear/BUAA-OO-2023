package mine.main;

import com.oocourse.spec2.main.Group;
import com.oocourse.spec2.main.Person;

import java.util.HashMap;

public class MyGroup implements Group {
    private final int id;
    private final HashMap<Integer, Person> people = new HashMap<>();
    private int valueSum = 0;
    private int ageMean = 0;
    private int ageVar = 0;
    private boolean cachedValueSum = true;
    private boolean cachedAgeMean = true;
    private boolean cachedAgeVar = true;


    public MyGroup(int id) { this.id = id; }

    //@ ensures \result == id;
    public int getId() { return this.id; }

    /*@ also
      @ public normal_behavior
      @ requires obj != null && obj instanceof Group;
      @ assignable \nothing;
      @ ensures \result == (((Group) obj).getId() == id);
      @ also
      @ public normal_behavior
      @ requires obj == null || !(obj instanceof Group);
      @ assignable \nothing;
      @ ensures \result == false;
      @*/
    public boolean equals(Object obj) {
        if (obj instanceof MyGroup) {
            return (((MyGroup) obj).getId() == this.id);
        } else {
            return false;
        }
    }

    /*@ public normal_behavior
      @ requires !hasPerson(person);
      @ assignable people[*];
      @ ensures (\forall Person p; \old(hasPerson(p)); hasPerson(p));
      @ ensures \old(people.length) == people.length - 1;
      @ ensures hasPerson(person);
      @*/
    public void addPerson(Person person) {
        people.put(person.getId(), person);
        flush();
    }

    //@ ensures \result == (\exists int i; 0 <= i && i < people.length; people[i].equals(person));
    public boolean hasPerson(Person person) { return people.containsKey(person.getId()); }

    /*@ ensures \result == (\sum int i; 0 <= i && i < people.length;
      @          (\sum int j; 0 <= j && j < people.length &&
      @           people[i].isLinked(people[j]); people[i].queryValue(people[j])));
      @*/
    public int getValueSum() {
        if (!cachedValueSum) {
            int valueSum = 0;
            for (Person person : people.values()) {
                valueSum += (((MyPerson) person).getValueSum());
            }
            cachedValueSum = true;
            this.valueSum = valueSum;
            System.out.println("not cached!");
        }
        return valueSum;
    }

    /*@ ensures \result == (people.length == 0? 0:
      @          ((\sum int i; 0 <= i && i < people.length; people[i].getAge()) / people.length));
      @*/
    public int getAgeMean() {
        if (people.isEmpty()) {
            return 0;
        } else {
            if (!cachedAgeMean) {
                int ageSum = 0;
                for (Person person : people.values()) {
                    ageSum += (person.getAge());
                }
                cachedAgeMean = true;
                ageMean = ageSum / people.size();
            }
            return ageMean;
        }
    }

    /*@ ensures \result == (people.length == 0? 0 : ((\sum int i; 0 <= i && i < people.length;
      @          (people[i].getAge() - getAgeMean()) * (people[i].getAge() - getAgeMean())) /
      @           people.length));
      @*/
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
                this.ageVar = ageVar;
            }
            return ageVar;
        }
    }

    /*@ public normal_behavior
      @ requires hasPerson(person) == true;
      @ assignable people[*];
      @ ensures (\forall Person p; hasPerson(p); \old(hasPerson(p)));
      @ ensures \old(people.length) == people.length + 1;
      @ ensures hasPerson(person) == false;
      @*/
    public void delPerson(Person person) { people.remove(person.getId()); }

    //@ ensures \result == people.length;
    public int getSize() { return this.people.size(); }

    public boolean containsPerson(int id) { return people.containsKey(id); }

    public HashMap<Integer, Person> getPeople() { return this.people; }

    public void flush() {
        cachedValueSum = false;
        cachedAgeMean = false;
        cachedAgeVar = false;
    }
}
