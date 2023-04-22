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
    private int triCount = 0;
    private final Union unionMap = new Union();

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

        dynamicTri(id1, id2);

        ((MyPerson) p1).addRelation((MyPerson) p2, value);
        ((MyPerson) p2).addRelation((MyPerson) p1, value);
        unionMap.union(p1.getId(), p2.getId());
    }

    public int queryValue(int id1, int id2) throws
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
        //        ArrayList<Person> peoples = new ArrayList<>(people.values());
        //        int count = 0;
        //        for (int i = 0; i < peoples.size(); i++) {
        //            for (int j = (i + 1); j < peoples.size(); j++) {
        //                if (!peoples.get(i).isLinked(peoples.get(j))) {
        //                    continue;
        //                }
        //                for (int k = (j + 1); k < peoples.size(); k++) {
        //                    if (!peoples.get(j).isLinked(peoples.get(k))) {
        //                        continue;
        //                    }
        //                    if (peoples.get(i).isLinked(peoples.get(k))) {
        //                        count++;
        //                    }
        //                }
        //            }
        //        }
        //
        //        return count;
        return triCount;
    }

    public boolean queryTripleSumOKTest(HashMap<Integer, HashMap<Integer, Integer>> beforeData,
                                        HashMap<Integer, HashMap<Integer, Integer>> afterData,
                                        int result) {
        try {
            generateNetWork(beforeData);
        } catch (Exception e) {
            return false;
        }

        if (queryTripleSum() != result) {
            return false;
        }

        HashMap<Integer, HashMap<Integer, Integer>> ansMap = traverse2Map();

        return ansMap.equals(afterData);
    }

    public void dynamicTri(int id1, int id2) {
        MyPerson p1 = (MyPerson) ((((MyPerson) people.get(id1)).getAcquaintance().size() <=
                                   ((MyPerson) people.get(id2)).getAcquaintance().size()) ?
                                   people.get(id1) : people.get(id2));
        Person p2 = (p1.getId() == id2) ? people.get(id1) : people.get(id2);
        HashMap<Integer, Person> nodes = p1.getAcquaintance();
        for (Person tempP : nodes.values()) {
            if (tempP.isLinked(p2)) {
                triCount++;
            }
        }
    }

    public HashMap<Integer, HashMap<Integer, Integer>> traverse2Map() {
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

    public void generateNetWork(HashMap<Integer, HashMap<Integer, Integer>> data) throws
            EqualPersonIdException {
        int age = 114514;
        for (int peopleId : data.keySet()) {
            addPerson(new MyPerson(peopleId, "BUAA-OO is best class!", age));
        }

        for (int p1Id : data.keySet()) {
            for (int p2Id : data.get(p1Id).keySet()) {
                int value = data.get(p1Id).get(p2Id);
                try {
                    addRelation(p1Id, p2Id, value);
                } catch (Exception ignored) {

                }
            }
        }
    }
}
