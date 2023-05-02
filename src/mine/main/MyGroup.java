package mine.main;

import com.oocourse.spec2.main.Group;
import com.oocourse.spec2.main.Person;

import java.util.HashMap;

public class MyGroup implements Group {
    private final int id;
    private final HashMap<Integer, Person> people = new HashMap<>();
    private int valueSum = 0;
    private int ageSum = 0;
    private int ageVar = 0;
    private boolean cachedValueSum = true;
    private boolean cachedAgeVar = true;

    public MyGroup(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

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
        if (!cachedValueSum) {
            int valueSum = 0;
            for (Person person : people.values()) {
                valueSum += (((MyPerson) person).getValueSum());
            }
            cachedValueSum = true;
            this.valueSum = valueSum;
            // System.out.println("not cached!");
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
                this.ageVar = ageVar;
            }
            return ageVar;
        }
    }

    public void delPerson(Person person) {
        people.remove(person.getId());
        ageSum -= person.getAge();
    }

    public int getSize() {
        return this.people.size();
    }

    public boolean containsPerson(int id) {
        return people.containsKey(id);
    }

    public HashMap<Integer, Person> getPeople() {
        return this.people;
    }

    public void flush() {
        cachedValueSum = false;
        cachedAgeVar = false;
    }

    public void flushCachedValueSum() {
        cachedValueSum = false;
    }
}
