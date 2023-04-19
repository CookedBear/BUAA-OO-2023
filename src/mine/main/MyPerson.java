package mine.main;

import com.oocourse.spec1.main.Person;
import mine.exceptions.MyEqualRelationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class MyPerson implements Person {
    private final int id;
    private final String name;
    private final int age;
    private final HashMap<Integer, Person> acquaintance = new HashMap<>();
    private final HashMap<Integer, Integer> value = new HashMap<>();

    MyPerson(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    /*@ invariant acquaintance!= null && value != null && acquaintance.length == value.length &&
      @  (\forall int i,j; 0 <= i && i < j && j < acquaintance.length;
      @   !acquaintance[i].equals(acquaintance[j]));*/

    //@ ensures \result == id;
    public /*@ pure @*/ int getId() { return this.id; }

    //@ ensures \result.equals(name);
    public /*@ pure @*/ String getName() { return this.name; }

    //@ ensures \result == age;
    public /*@ pure @*/ int getAge() { return this.age; }

    /*@ also
      @ public normal_behavior
      @ requires obj != null && obj instanceof Person;
      @ assignable \nothing;
      @ ensures \result == (((Person) obj).getId() == id) && (((Person) obj).getName().equals(name)) &&
      @                    (((Person) obj).getAge() == age) &&
      @                    (\forall int i; 0 <= i && i < ((Person) obj).getAcquaintance().length; acquaintance[i].equals(((Person) obj).getAcquaintance().get(i))) &&
      @                    (\forall int i; 0 <= i && i < ((Person) obj).getAcquaintance().length; value[i] == (((Person) obj).queryValue(((Person) obj).getAcquaintance().get(i)))) &&
      @                    (((Person) obj).getAcquaintance().length == acquaintance.length);
      @ also
      @ public normal_behavior
      @ requires obj == null || !(obj instanceof Person);
      @ assignable \nothing;
      @ ensures \result == false;
      @*/
    public /*@ pure @*/ boolean equals(Object obj) {
        if (!(obj instanceof Person)) {
            return false;
        } else {
            return (((Person) obj).getId() == id);
        }
    }

    /*@ public normal_behavior
      @ assignable \nothing;
      @ ensures \result == (\exists int i; 0 <= i && i < acquaintance.length;
      @                     acquaintance[i].equals(person) || person.equals(this);
      @*/
    public /*@ pure @*/ boolean isLinked(Person person) {
        if (person.equals(this)) {
            return true;
        } else {
            return acquaintance.containsKey(person.getId());
        }
    }

    /*@ public normal_behavior
      @ requires (\exists int i; 0 <= i && i < acquaintance.length;
      @          acquaintance[i].equals(person));
      @ assignable \nothing;
      @ ensures (\exists int i; 0 <= i && i < acquaintance.length;
      @         acquaintance[i].equals(person) && \result == value[i]);
      @ also
      @ public normal_behavior
      @ requires (\forall int i; 0 <= i && i < acquaintance.length;
      @          acquaintance[i].getId() != person.getId());
      @ ensures \result == 0;
      @*/
    public /*@ pure @*/ int queryValue(Person person) {
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
