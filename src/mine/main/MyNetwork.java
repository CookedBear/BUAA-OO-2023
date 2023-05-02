package mine.main;

import com.oocourse.spec2.exceptions.AcquaintanceNotFoundException;
import com.oocourse.spec2.exceptions.EqualGroupIdException;
import com.oocourse.spec2.exceptions.EqualMessageIdException;
import com.oocourse.spec2.exceptions.EqualPersonIdException;
import com.oocourse.spec2.exceptions.EqualRelationException;
import com.oocourse.spec2.exceptions.GroupIdNotFoundException;
import com.oocourse.spec2.exceptions.MessageIdNotFoundException;
import com.oocourse.spec2.exceptions.PersonIdNotFoundException;
import com.oocourse.spec2.exceptions.RelationNotFoundException;

import com.oocourse.spec2.main.Group;
import com.oocourse.spec2.main.Message;
import com.oocourse.spec2.main.Network;
import com.oocourse.spec2.main.Person;

import mine.exceptions.MyAcquaintanceNotFoundException;
import mine.exceptions.MyEqualGroupIdException;
import mine.exceptions.MyEqualMessageIdException;
import mine.exceptions.MyEqualPersonIdException;
import mine.exceptions.MyEqualRelationException;
import mine.exceptions.MyGroupIdNotFoundException;
import mine.exceptions.MyMessageIdNotFoundException;
import mine.exceptions.MyPersonIdNotFoundException;
import mine.exceptions.MyRelationNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MyNetwork implements Network {
    private final HashMap<Integer, Person> people = new HashMap<>();
    private final HashMap<Integer, Group> groups = new HashMap<>();
    private final HashMap<Integer, Message> messages = new HashMap<>();

    private final HashMap<Integer, Integer> couples = new HashMap<>();

    private int triCount = 0;
    private final Union unionMap = new Union();

    // people 集合时刻不为空，且不重复

    public MyNetwork() {
    }

    public boolean contains(int id) {
        return people.containsKey(id);
    }

    public Person getPerson(int id) {
        return people.getOrDefault(id, null);
    }

    public void addPerson(Person person) throws EqualPersonIdException {
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

        dynamicTri(id1, id2, true);

        ((MyPerson) p1).addRelation((MyPerson) p2, value);
        ((MyPerson) p2).addRelation((MyPerson) p1, value);

        for (int groupId : ((MyPerson) p1).getGroupList()) {
            ((MyGroup) groups.get(groupId)).flushCachedValueSum();
        }
        for (int groupId : ((MyPerson) p2).getGroupList()) {
            ((MyGroup) groups.get(groupId)).flushCachedValueSum();
        }
        unionMap.union(Math.min(p1.getId(), p2.getId()),
                Math.max(p1.getId(), p2.getId()));
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

        return (unionMap.find(id1) == unionMap.find(id2));
    }

    public int queryBlockSum() {

        ArrayList<Integer> peoples = new ArrayList<>(people.keySet());
        HashMap<Integer, Integer> blockMap = new HashMap<>();

        for (Integer integer : peoples) {
            blockMap.put(unionMap.find(integer), 114514);
        }

        return blockMap.size();
    }

    public int queryTripleSum() {
        return triCount;
    }

    public boolean queryTripleSumOkTest(HashMap<Integer, HashMap<Integer, Integer>> beforeData,
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

    public void dynamicTri(int id1, int id2, boolean add) {
        MyPerson p1 = (MyPerson) ((((MyPerson) people.get(id1)).getAcquaintance().size() <=
                ((MyPerson) people.get(id2)).getAcquaintance().size()) ?
                people.get(id1) : people.get(id2));
        Person p2 = (p1.getId() == id2) ? people.get(id1) : people.get(id2);
        HashMap<Integer, Person> nodes = p1.getAcquaintance();
        for (Person tempP : nodes.values()) {
            if (tempP.isLinked(p2)) {
                if (add) {
                    triCount++;
                } else {
                    triCount--;
                }
            }
        }
    }

    public HashMap<Integer, HashMap<Integer, Integer>> traverse2Map() {
        // generate structure as testData
        HashMap<Integer, HashMap<Integer, Integer>> returnMap = new HashMap<>();

        for (int peopleId : people.keySet()) {
            HashMap<Integer, Integer> nowMap = new HashMap<>();
            HashMap<Integer, Integer> values = ((MyPerson) people.get(peopleId)).getValue();
            HashMap<Integer, Person> acquaintance = ((MyPerson) people.
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
                    int i = 1;
                }
            }
        }
    }
    // ------------------------------------------------------------------------

    public void addGroup(Group group) throws EqualGroupIdException {
        if (groups.containsKey(group.getId())) {
            throw new MyEqualGroupIdException(group.getId());
        }
        groups.put(group.getId(), group);
    }

    public Group getGroup(int id) {
        return groups.getOrDefault(id, null);
    }

    public void addToGroup(int id1, int id2) throws GroupIdNotFoundException,
            PersonIdNotFoundException, EqualPersonIdException {
        if (!groups.containsKey(id2)) {
            throw new MyGroupIdNotFoundException(id2);
        }
        if (!people.containsKey(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        }
        Group group = groups.get(id2);
        if (((MyGroup) group).containsPerson(id1)) {
            throw new MyEqualPersonIdException(id1);
        }
        Person person = people.get(id1);

        if (group.getSize() > 1111) {
            // bie-bie
        } else {
            group.addPerson(person);
            ((MyPerson) person).addInGroup(id2);
        }


    }

    public int queryGroupValueSum(int id) throws GroupIdNotFoundException {
        if (!groups.containsKey(id)) {
            throw new MyGroupIdNotFoundException(id);
        }
        return groups.get(id).getValueSum();
    }

    public int queryGroupAgeVar(int id) throws GroupIdNotFoundException {
        if (!groups.containsKey(id)) {
            throw new MyGroupIdNotFoundException(id);
        }
        return groups.get(id).getAgeVar();
    }

    public void delFromGroup(int id1, int id2)
            throws GroupIdNotFoundException, PersonIdNotFoundException, EqualPersonIdException {
        if (!groups.containsKey(id2)) {
            throw new MyGroupIdNotFoundException(id2);
        }
        if (!people.containsKey(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        }
        Group group = groups.get(id2);
        if (!((MyGroup) group).containsPerson(id1)) {
            throw new MyEqualPersonIdException(id1);
        }
        group.delPerson(people.get(id1));
    }

    public boolean containsMessage(int id) {
        return messages.containsKey(id);
    }

    public void addMessage(Message message) throws
            EqualMessageIdException, EqualPersonIdException {
        if (messages.containsKey(message.getId())) {
            throw new MyEqualMessageIdException(message.getId());
        }
        if (message.getType() == 0 &&
                message.getPerson1().getId() == message.getPerson2().getId()) {
            throw new MyEqualPersonIdException(message.getId());
        }
        messages.put(message.getId(), message);
    }

    public Message getMessage(int id) {
        return messages.getOrDefault(id, null);
    }

    public void sendMessage(int id) throws
            RelationNotFoundException, MessageIdNotFoundException, PersonIdNotFoundException {
        if (!messages.containsKey(id)) {
            throw new MyMessageIdNotFoundException(id);
        }
        Message message = messages.get(id);
        if (message.getType() == 0 && !message.getPerson1().isLinked(message.getPerson2())) {
            throw new MyRelationNotFoundException(message.getPerson1().getId(),
                    message.getPerson2().getId());
        }
        if (message.getType() == 1 && !message.getGroup().hasPerson(message.getPerson1())) {
            throw new MyPersonIdNotFoundException(message.getPerson1().getId());
        }

        Person person1 = message.getPerson1();
        int socialValue = message.getSocialValue();
        if (message.getType() == 0) {
            Person person2 = message.getPerson2();
            person1.addSocialValue(socialValue);
            person2.addSocialValue(socialValue);
            ((MyPerson) person2).addMessage(message);
        } else {
            HashMap<Integer, Person> peopleList = ((MyGroup) message.getGroup()).getPeople();
            for (Person person : peopleList.values()) {
                person.addSocialValue(socialValue);
            }
        }
        messages.remove(message.getId());
    }

    public int querySocialValue(int id) throws PersonIdNotFoundException {
        if (!people.containsKey(id)) {
            throw new MyPersonIdNotFoundException(id);
        }
        return people.get(id).getSocialValue();
    }

    public List<Message> queryReceivedMessages(int id) throws PersonIdNotFoundException {
        if (people.containsKey(id)) {
            return people.get(id).getReceivedMessages();
        } else {
            throw new MyPersonIdNotFoundException(id);
        }
    }

    public int queryBestAcquaintance(int id) throws
            PersonIdNotFoundException, AcquaintanceNotFoundException {
        if (!people.containsKey(id)) {
            throw new MyPersonIdNotFoundException(id);
        }
        Person person = people.get(id);
        if (((MyPerson) person).getAcquaintance().isEmpty()) {
            throw new MyAcquaintanceNotFoundException(id);
        }

        return ((MyPerson) person).getCouple();
    }

    public int queryCoupleSum() {
        couples.clear();
        for (Person person : people.values()) {
            if (!((MyPerson) person).getAcquaintance().isEmpty()) {
                couples.put(person.getId(), ((MyPerson) person).getCouple());
            }
        }
        int coupleSum = 0;
        for (int id1 : couples.keySet()) {
            if (couples.get(couples.get(id1)) == id1) {
                coupleSum++;
            }
        }
        return (coupleSum / 2);
    }

    public void modifyRelation(int id1, int id2, int value) throws PersonIdNotFoundException,
            EqualPersonIdException, RelationNotFoundException {
        if (!people.containsKey(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        } else if (!people.containsKey(id2)) {
            throw new MyPersonIdNotFoundException(id2);
        } else if (id1 == id2) {
            throw new MyEqualPersonIdException(id1);
        } else if (!people.get(id1).isLinked(people.get(id2))) {
            throw new MyRelationNotFoundException(id1, id2);
        }

        Person person1 = people.get(id1);
        Person person2 = people.get(id2);

        if (person1.queryValue(person2) + value > 0) {
            ((MyPerson) person1).addValue(id2, value);
            ((MyPerson) person2).addValue(id1, value);
        } else {
            // relation broken cause: person/group cache failure, tri changed, union map rebuild
            ((MyPerson) person1).delRelation(id2);
            ((MyPerson) person2).delRelation(id1);

            dynamicTri(id1, id2, false);

            unionMap.setVisited(people.size());
            unionMap.rebuildPart(id1, id1, people);
            unionMap.setVisited(people.size());
            unionMap.rebuildPart(id2, id2, people);
        }
        for (int groupId : ((MyPerson) person1).getGroupList()) {
            ((MyGroup) groups.get(groupId)).flushCachedValueSum();
        }
        for (int groupId : ((MyPerson) person2).getGroupList()) {
            ((MyGroup) groups.get(groupId)).flushCachedValueSum();
        }
    }

    public int modifyRelationOKTest(int id1, int id2, int value,
                                    HashMap<Integer, HashMap<Integer, Integer>> beforeData,
                                    HashMap<Integer, HashMap<Integer, Integer>> afterData) {
        try {
            generateNetWork(beforeData);
        } catch (Exception e) {
            return (beforeData.equals(afterData)) ? 0 : -1; }
        if (!beforeData.containsKey(id1) ||
                !beforeData.containsKey(id2) ||
                id1 == id2 ||
                !beforeData.get(id1).containsKey(id2)) {
            return (beforeData.equals(afterData)) ? 0 : -1; }

        if (beforeData.size() != afterData.size()) {
            return 1; }
        for (int beforeId : beforeData.keySet()) {
            if (!afterData.containsKey(beforeId)) {
                return 2; } }
        for (int beforeId : beforeData.keySet()) {
            if (beforeId != id1 && beforeId != id2) {
                HashMap<Integer, Integer> beforePerson = beforeData.get(beforeId);
                HashMap<Integer, Integer> afterPerson = afterData.get(beforeId);
                if (!beforePerson.equals(afterPerson)) {
                    return 3; } }
        }
        HashMap<Integer, Integer> ap1acq = afterData.get(id1);
        HashMap<Integer, Integer> ap2acq = afterData.get(id2);
        HashMap<Integer, Integer> bp1acq = beforeData.get(id1);
        HashMap<Integer, Integer> bp2acq = beforeData.get(id2);
        if (bp1acq.get(id2) + value > 0) {
            if (!afterData.get(id1).containsKey(id2) || !afterData.get(id2).containsKey(id1)) {
                return 4; }
            if (value + bp1acq.get(id2) != ap1acq.get(id2)) {
                return 5; }
            if (value + bp2acq.get(id1) != ap2acq.get(id1)) {
                return 6; }
            if (bp1acq.size() != ap1acq.size()) {
                return 7; }
            if (bp2acq.size() != ap2acq.size()) {
                return 8; }
            for (int acq1 : bp1acq.keySet()) {
                if (!ap1acq.containsKey(acq1)) {
                    return 9; } }
            for (int acq2 : bp2acq.keySet()) {
                if (!ap2acq.containsKey(acq2)) {
                    return 10; } }
            for (int acq1 : bp1acq.keySet()) {
                if (acq1 != id2 && !Objects.equals(bp1acq.get(acq1), ap1acq.get(acq1))) {
                    return 11; } }
            for (int acq2 : bp2acq.keySet()) {
                if (acq2 != id1 && !Objects.equals(bp2acq.get(acq2), ap2acq.get(acq2))) {
                    return 12; } }
        } else {
            if (ap1acq.containsKey(id2) || ap2acq.containsKey(id1)) {
                return 15; }
            if (ap1acq.size() + 1 != bp1acq.size()) {
                return 16; }
            if (ap2acq.size() + 1 != bp2acq.size()) {
                return 17; }
        }
        return 0;
    }
}
