package mine.main;

import spec1.main.Person;
import mine.exceptions.MyEqualRelationException;

import java.util.HashMap;

public class MyPerson implements Person {
    private final int id;
    private final String name;
    private final int age;
    private final HashMap<Integer, Person> acquaintance = new HashMap<>();
    private final HashMap<Integer, Integer> value = new HashMap<>();

    public MyPerson(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
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

    public void addRelation(MyPerson p2, int values) throws MyEqualRelationException {
        if (acquaintance.containsKey(p2.id)) {
            throw new MyEqualRelationException(id, p2.id);
        } else {
            acquaintance.put(p2.id, p2);
            value.put(p2.id, values);
        }
    }

    public HashMap<Integer, Person> getAcquaintance() { return this.acquaintance; }

    public HashMap<Integer, Integer> getValue() { return this.value; }
}
