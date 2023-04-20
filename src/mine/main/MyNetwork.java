package mine.main;

import com.oocourse.spec1.exceptions.EqualPersonIdException;
import com.oocourse.spec1.exceptions.EqualRelationException;
import com.oocourse.spec1.exceptions.PersonIdNotFoundException;
import com.oocourse.spec1.exceptions.RelationNotFoundException;
import com.oocourse.spec1.main.Network;
import com.oocourse.spec1.main.Person;
import mine.exceptions.MyEqualPersonIdException;
import mine.exceptions.MyEqualRelationException;
import mine.exceptions.MyPersonIdNotFoundException;
import mine.exceptions.MyRelationNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;

public class MyNetwork implements Network {
    private final HashMap<Integer, Person> people = new HashMap<>();
    private Union unionMap = new Union();

    // people 集合时刻不为空，且不重复

    public MyNetwork() { }

    public boolean contains(int id) { return people.containsKey(id); }

    public Person getPerson(int id) {
        return people.getOrDefault(id, null);
    }

    public void addPerson(/*@ non_null @*/Person person) throws EqualPersonIdException {
        if (people.containsKey(person.getId())) {
            throw new MyEqualPersonIdException(person.getId());
        }
        people.put(person.getId(), person);
        unionMap.addUnion(person.getId(), person.getId());
    }

    /*@ public normal_behavior
      @ requires contains(id1) && contains(id2) && !getPerson(id1).isLinked(getPerson(id2));
      @ assignable people[*];
      @ ensures people.length == \old(people.length);
      @ ensures (\forall int i; 0 <= i && i < \old(people.length); \not_modified(\old(people[i])));
      @ ensures (\forall int i; 0 <= i && i < people.length && \old(people[i].getId()) != id1 &&
      @     \old(people[i].getId()) != id2; \not_assigned(people[i]));
      @ ensures getPerson(id1).isLinked(getPerson(id2)) && getPerson(id2).isLinked(getPerson(id1));
      @ ensures getPerson(id1).queryValue(getPerson(id2)) == value;
      @ ensures getPerson(id2).queryValue(getPerson(id1)) == value;
      @ ensures (\forall int i; 0 <= i && i < \old(getPerson(id1).acquaintance.length);
      @         not_assigned(getPerson(id1).acquaintance[i],getPerson(id1).value[i]));
      @ ensures (\forall int i; 0 <= i && i < \old(getPerson(id2).acquaintance.length);
      @         not_assigned(getPerson(id2).acquaintance[i],getPerson(id2).value[i]));
      @ ensures getPerson(id1).value.length == getPerson(id1).acquaintance.length;
      @ ensures getPerson(id2).value.length == getPerson(id2).acquaintance.length;
      @ ensures \old(getPerson(id1).value.length) == getPerson(id1).acquaintance.length - 1;
      @ ensures \old(getPerson(id2).value.length) == getPerson(id2).acquaintance.length - 1;
      @ also
      @ public exceptional_behavior
      @ assignable \nothing;
      @ requires !contains(id1) || !contains(id2) || getPerson(id1).isLinked(getPerson(id2));
      @ signals (PersonIdNotFoundException e) !contains(id1);
      @ signals (PersonIdNotFoundException e) contains(id1) && !contains(id2);
      @ signals (EqualRelationException e) contains(id1) && contains(id2) &&
      @         getPerson(id1).isLinked(getPerson(id2));
      @*/
    public void addRelation(int id1, int id2, int value) throws
            PersonIdNotFoundException, EqualRelationException {
        if (!people.containsKey(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        } else if (!people.containsKey(id2)) {
            throw new MyPersonIdNotFoundException(id2);
        } else if (people.get(id1).isLinked(people.get(id2))) {
            throw new MyEqualRelationException(id1, id2);
        }
        Person p1 = people.get(id1);
        Person p2 = people.get(id2);

        ((MyPerson) p1).addRelation((MyPerson) p2, value);
        ((MyPerson) p2).addRelation((MyPerson) p1, value);
        unionMap.union(p1.getId(), p2.getId());
    }

    /*@ public normal_behavior
      @ requires contains(id1) && contains(id2) && getPerson(id1).isLinked(getPerson(id2));
      @ ensures \result == getPerson(id1).queryValue(getPerson(id2));
      @ also
      @ public exceptional_behavior
      @ signals (PersonIdNotFoundException e) !contains(id1);
      @ signals (PersonIdNotFoundException e) contains(id1) && !contains(id2);
      @ signals (RelationNotFoundException e) contains(id1) && contains(id2) &&
      @         !getPerson(id1).isLinked(getPerson(id2));
      @*/
    public /*@ pure @*/ int queryValue(int id1, int id2) throws
            PersonIdNotFoundException, RelationNotFoundException {
        if (!people.containsKey(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        } else if (!people.containsKey(id2)) {
            throw new MyPersonIdNotFoundException(id2);
        } else if (!people.get(id1).isLinked(people.get(id2))) {
            throw new MyRelationNotFoundException(id1, id2);
        }
        return people.get(id1).queryValue(people.get(id2));
    }

    public boolean isCircle(int id1, int id2) throws PersonIdNotFoundException {
        if (!people.containsKey(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        } else if (!people.containsKey(id2)) {
            throw new MyPersonIdNotFoundException(id2);
        }
        // 并查集寻找 id1 到 id2 的连通性，无需保存路径
        // System.out.printf("isCircle: %d to %d, find is: %d - %d\n", id1, id2,
        // unionMap.find(id1),unionMap.find(id2));
        return (unionMap.find(id1) == unionMap.find(id2));
    }

    public int queryBlockSum() {
        ArrayList<Integer> peoples = new ArrayList<>(people.keySet());
        int count = 0;
        for (int i = 0; i < peoples.size(); i++) {
            boolean able = true;
            for (int j = 0; j < i; j++) {
                boolean flag = false;
                try {
                    flag = !isCircle(peoples.get(i), peoples.get(j));
                } catch (PersonIdNotFoundException e) {
                    e.print();
                }
                if (!flag) {
                    able = false;
                    break;
                }
            }
            if (able) {
                count++;
            }
        }
        return count;
    }

    public int queryTripleSum() {
        ArrayList<Person> peoples = new ArrayList<>(people.values());
        int count = 0;
        for (int i = 0; i < peoples.size(); i++) {
            for (int j = (i + 1); j < peoples.size(); j++) {
                if (!peoples.get(i).isLinked(peoples.get(j))) {
                    continue;
                }
                for (int k = (j + 1); k < peoples.size(); k++) {
                    if (!peoples.get(j).isLinked(peoples.get(k))) {
                        continue;
                    }
                    if (peoples.get(i).isLinked(peoples.get(k))) {
                        count++;
                    }
                }
            }
        }

        return count;
    }

    public boolean queryTripleSumOKTest(HashMap<Integer, HashMap<Integer, Integer>> beforeData,
                                        HashMap<Integer, HashMap<Integer, Integer>> afterData,
                                        int result) {
        int age = 114514;
        for (int peopleId : beforeData.keySet()) {
            try {
                addPerson(new MyPerson(peopleId, "BUAA-OO is best class!", age));
            } catch (Exception e) {
                return false;
            }
        }

        for (int peopleId : beforeData.keySet()) {
            for (int p2Id : beforeData.get(peopleId).keySet()) {
                int value = beforeData.get(peopleId).get(p2Id);
                try {
                    addRelation(peopleId, p2Id, value);
                } catch (Exception e) {
                    assert e instanceof MyEqualRelationException;
                    if (((MyEqualRelationException) e).getTimes(true) > 2 ||
                        ((MyEqualRelationException) e).getTimes(false) > 2) {
                        return false;
                    }
                }
            }
        }

        int ans = queryTripleSum();

        if (ans != result) {
            return false;
        }

        HashMap<Integer, HashMap<Integer, Integer>> ansMap = traverseData();

        return ansMap.equals(afterData);
    }

    public HashMap<Integer, HashMap<Integer, Integer>> traverseData() {
        // generate structure as testData
        HashMap<Integer, HashMap<Integer, Integer>> returnMap = new HashMap<>();

        for (int peopleId : people.keySet()) {
            HashMap<Integer, Integer> nowMap = new HashMap<>();
            HashMap<Integer, Integer> values = ((MyPerson) people.get(peopleId)).getValue();
            HashMap<Integer, Person>  acquaintance = ((MyPerson) people.
                    get(peopleId)).getAcquaintance();
            for (int pid : acquaintance.keySet()) {
                nowMap.put(pid, values.get(pid));
            }
            returnMap.put(peopleId, nowMap);
        }
        return returnMap;
    }

}
