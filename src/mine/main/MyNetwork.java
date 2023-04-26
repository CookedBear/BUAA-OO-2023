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

    public MyNetwork() { }

    public boolean contains(int id) { return people.containsKey(id); }

    public Person getPerson(int id) { return people.getOrDefault(id, null); }

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

    public int queryTripleSum() { return triCount; }

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
                    int i = 1;
                }
            }
        }
    }


    // ------------------------------------------------------------------------


    /*@ public normal_behavior
      @ requires !(\exists int i; 0 <= i && i < groups.length; groups[i].equals(group));
      @ assignable groups[*];
      @ ensures groups.length == \old(groups.length) + 1;
      @ ensures (\forall int i; 0 <= i && i < \old(groups.length);
      @          (\exists int j; 0 <= j && j < groups.length; groups[j] == (\old(groups[i]))));
      @ ensures (\exists int i; 0 <= i && i < groups.length; groups[i] == group);
      @ also
      @ public exceptional_behavior
      @ signals (EqualGroupIdException e) (\exists int i; 0 <= i && i < groups.length;
      @                                     groups[i].equals(group));
      @*/
    public void addGroup(Group group) throws EqualGroupIdException {
        if (groups.containsKey(group.getId())) {
            throw new MyEqualGroupIdException(group.getId());
        }
        groups.put(group.getId(), group);
    }

    /*@ public normal_behavior
      @ requires (\exists int i; 0 <= i && i < groups.length; groups[i].getId() == id);
      @ ensures (\exists int i; 0 <= i && i < groups.length; groups[i].getId() == id &&
      @         \result == groups[i]);
      @ also
      @ public normal_behavior
      @ requires (\forall int i; 0 <= i && i < groups.length; groups[i].getId() != id);
      @ ensures \result == null;
      @*/
    public Group getGroup(int id) { return groups.getOrDefault(id, null); }

    /*@ public normal_behavior
      @ requires (\exists int i; 0 <= i && i < groups.length; groups[i].getId() == id2) &&
      @           (\exists int i; 0 <= i && i < people.length; people[i].getId() == id1) &&
      @            getGroup(id2).hasPerson(getPerson(id1)) == false &&
      @             getGroup(id2).people.length <= 1111;
      @ assignable getGroup(id2).people[*];
      @ ensures (\forall Person i; \old(getGroup(id2).hasPerson(i));
      @          getGroup(id2).hasPerson(i));
      @ ensures \old(getGroup(id2).people.length) == getGroup(id2).people.length - 1;
      @ ensures getGroup(id2).hasPerson(getPerson(id1));
      @ also
      @ public normal_behavior
      @ requires (\exists int i; 0 <= i && i < groups.length; groups[i].getId() == id2) &&
      @           (\exists int i; 0 <= i && i < people.length; people[i].getId() == id1) &&
      @            getGroup(id2).hasPerson(getPerson(id1)) == false &&
      @             getGroup(id2).people.length > 1111;
      @ assignable \nothing;
      @ also
      @ public exceptional_behavior
      @ signals (GroupIdNotFoundException e) !(\exists int i; 0 <= i && i < groups.length;
      @          groups[i].getId() == id2);
      @ signals (PersonIdNotFoundException e) (\exists int i; 0 <= i && i < groups.length;
      @          groups[i].getId() == id2) && !(\exists int i; 0 <= i && i < people.length;
      @           people[i].getId() == id1);
      @ signals (EqualPersonIdException e) (\exists int i; 0 <= i && i < groups.length;
      @          groups[i].getId() == id2) && (\exists int i; 0 <= i && i < people.length;
      @           people[i].getId() == id1) && getGroup(id2).hasPerson(getPerson(id1));
      @*/
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
            // biebie
        } else {
            group.addPerson(person);
        }


    }

    /*@ public normal_behavior
      @ requires (\exists int i; 0 <= i && i < groups.length; groups[i].getId() == id);
      @ ensures \result == getGroup(id).getValueSum();
      @ also
      @ public exceptional_behavior
      @ signals (GroupIdNotFoundException e) !(\exists int i; 0 <= i && i < groups.length;
      @          groups[i].getId() == id);
      @*/
    public int queryGroupValueSum(int id) throws GroupIdNotFoundException {
        if (!groups.containsKey(id)) {
            throw new MyGroupIdNotFoundException(id);
        }
        return groups.get(id).getValueSum();
    }

    /*@ public normal_behavior
      @ requires (\exists int i; 0 <= i && i < groups.length; groups[i].getId() == id);
      @ ensures \result == getGroup(id).getAgeVar();
      @ also
      @ public exceptional_behavior
      @ signals (GroupIdNotFoundException e) !(\exists int i; 0 <= i && i < groups.length;
      @          groups[i].getId() == id);
      @*/
    public int queryGroupAgeVar(int id) throws GroupIdNotFoundException {
        if (!groups.containsKey(id)) {
            throw new MyGroupIdNotFoundException(id);
        }
        return groups.get(id).getAgeVar();
    }

    /*@ public normal_behavior
      @ requires (\exists int i; 0 <= i && i < groups.length; groups[i].getId() == id2) &&
      @           (\exists int i; 0 <= i && i < people.length; people[i].getId() == id1) &&
      @            getGroup(id2).hasPerson(getPerson(id1)) == true;
      @ assignable getGroup(id2).people[*];
      @ ensures (\forall Person i; getGroup(id2).hasPerson(i);
      @          \old(getGroup(id2).hasPerson(i)));
      @ ensures \old(getGroup(id2).people.length) == getGroup(id2).people.length + 1;
      @ ensures getGroup(id2).hasPerson(getPerson(id1)) == false;
      @ also
      @ public exceptional_behavior
      @ signals (GroupIdNotFoundException e) !(\exists int i; 0 <= i && i < groups.length;
      @          groups[i].getId() == id2);
      @ signals (PersonIdNotFoundException e) (\exists int i; 0 <= i && i < groups.length;
      @          groups[i].getId() == id2) && !(\exists int i; 0 <= i && i < people.length;
      @           people[i].getId() == id1);
      @ signals (EqualPersonIdException e) (\exists int i; 0 <= i && i < groups.length;
      @          groups[i].getId() == id2) && (\exists int i; 0 <= i && i < people.length;
      @           people[i].getId() == id1) && !getGroup(id2).hasPerson(getPerson(id1));
      @*/
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

    //@ ensures \result == (\exists int i; 0 <= i && i < messages.length; messages[i].getId() == id);
    public boolean containsMessage(int id) { return messages.containsKey(id); }

    /*@ public normal_behavior
      @ requires !(\exists int i; 0 <= i && i < messages.length; messages[i].equals(message)) &&
      @           ((message.getType() == 0) ==> (message.getPerson1() != message.getPerson2()));
      @ assignable messages;
      @ ensures messages.length == \old(messages.length) + 1;
      @ ensures (\forall int i; 0 <= i && i < \old(messages.length);
      @          (\exists int j; 0 <= j && j < messages.length; messages[j].equals(\old(messages[i]))));
      @ ensures (\exists int i; 0 <= i && i < messages.length; messages[i].equals(message));
      @ also
      @ public exceptional_behavior
      @ signals (EqualMessageIdException e) (\exists int i; 0 <= i && i < messages.length;
      @                                     messages[i].equals(message));
      @ signals (EqualPersonIdException e) !(\exists int i; 0 <= i && i < messages.length;
      @                                     messages[i].equals(message)) &&
      @                                     message.getType() == 0 && message.getPerson1() == message.getPerson2();
      @*/
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

    /*@ public normal_behavior
      @ requires containsMessage(id);
      @ ensures (\exists int i; 0 <= i && i < messages.length; messages[i].getId() == id &&
      @         \result == messages[i]);
      @ public normal_behavior
      @ requires !containsMessage(id);
      @ ensures \result == null;
      @*/
    public Message getMessage(int id) { return messages.getOrDefault(id, null); }


    /*@ public normal_behavior
      @ requires containsMessage(id) && getMessage(id).getType() == 0 &&
      @          getMessage(id).getPerson1().isLinked(getMessage(id).getPerson2()) &&
      @          getMessage(id).getPerson1() != getMessage(id).getPerson2();
      @ assignable messages;
      @ assignable getMessage(id).getPerson1().socialValue;
      @ assignable getMessage(id).getPerson2().messages, getMessage(id).getPerson2().socialValue;
      @ ensures \old(getMessage(id)).getPerson1().getSocialValue() ==
      @         \old(getMessage(id).getPerson1().getSocialValue()) + \old(getMessage(id)).getSocialValue() &&
      @         \old(getMessage(id)).getPerson2().getSocialValue() ==
      @         \old(getMessage(id).getPerson2().getSocialValue()) + \old(getMessage(id)).getSocialValue(); 两人加 social
      @ ensures !containsMessage(id) && messages.length == \old(messages.length) - 1 &&
      @         (\forall int i; 0 <= i && i < \old(messages.length) && \old(messages[i].getId()) != id;
      @         (\exists int j; 0 <= j && j < messages.length; messages[j].equals(\old(messages[i])))); 消息没了
      @ ensures (\forall int i; 0 <= i && i < \old(getMessage(id).getPerson2().getMessages().size());
      @          \old(getMessage(id)).getPerson2().getMessages().get(i+1) == \old(getMessage(id).getPerson2().getMessages().get(i)));
      @ ensures \old(getMessage(id)).getPerson2().getMessages().get(0) == \old(getMessage(id));
      @ ensures \old(getMessage(id)).getPerson2().getMessages().size() == \old(getMessage(id).getPerson2().getMessages().size()) + 1; p2 多了消息
      @ also
      @ public normal_behavior
      @ requires containsMessage(id) && getMessage(id).getType() == 1 &&
      @           getMessage(id).getGroup().hasPerson(getMessage(id).getPerson1());
      @ assignable people[*].socialValue, messages;
      @ ensures (\forall Person p; \old(getMessage(id)).getGroup().hasPerson(p); p.getSocialValue() ==
      @         \old(p.getSocialValue()) + \old(getMessage(id)).getSocialValue()); 群里每人 + social
      @ ensures (\forall int i; 0 <= i && i < people.length && !\old(getMessage(id)).getGroup().hasPerson(people[i]);
      @          \old(people[i].getSocialValue()) == people[i].getSocialValue()); 其他人 social 不变
      @ ensures !containsMessage(id) && messages.length == \old(messages.length) - 1 &&
      @         (\forall int i; 0 <= i && i < \old(messages.length) && \old(messages[i].getId()) != id;
      @         (\exists int j; 0 <= j && j < messages.length; messages[j].equals(\old(messages[i])))); 消息没了
      @ also
      @ public exceptional_behavior
      @ signals (MessageIdNotFoundException e) !containsMessage(id);
      @ signals (RelationNotFoundException e) containsMessage(id) && getMessage(id).getType() == 0 &&
      @          !(getMessage(id).getPerson1().isLinked(getMessage(id).getPerson2()));
      @ signals (PersonIdNotFoundException e) containsMessage(id) && getMessage(id).getType() == 1 &&
      @          !(getMessage(id).getGroup().hasPerson(getMessage(id).getPerson1()));
      @*/
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

    /*@ public normal_behavior
      @ requires contains(id);
      @ ensures \result == getPerson(id).getSocialValue();
      @ also
      @ public exceptional_behavior
      @ signals (PersonIdNotFoundException e) !contains(id);
      @*/
    public int querySocialValue(int id) throws PersonIdNotFoundException {
        if (!people.containsKey(id)) {
            throw new MyPersonIdNotFoundException(id);
        }
        return people.get(id).getSocialValue();
    }


    /*@ public normal_behavior
      @ requires contains(id);
      @ ensures \result == getPerson(id).getReceivedMessages();
      @ also
      @ public exceptional_behavior
      @ signals (PersonIdNotFoundException e) !contains(id);
      @*/
    public List<Message> queryReceivedMessages(int id) throws PersonIdNotFoundException {
        if (people.containsKey(id)) {
            return people.get(id).getReceivedMessages();
        } else {
            throw new MyPersonIdNotFoundException(id);
        }
    }


    /*@ public normal_behavior
      @ requires contains(id) && getPerson(id).acquaintance.length != 0;
      @ ensures \result == (\min int bestIdx;
      @     0 <= bestIdx && bestIdx < getPerson(id).acquaintance.length &&
      @     (\forall int i; 0 <= i && i < getPerson(id).acquaintance.length;
      @         getPerson(id).value[i] <= getPerson(id).value[bestIdx]);
      @     getPerson(id).acquaintance[bestIdx].getId());
      @ public exceptional_behavior
      @ signals (PersonIdNotFoundException e) !contains(id);
      @ signals (AcquaintanceNotFoundException e) contains(id) &&
      @         getPerson(id).acquaintance.length == 0;
      @*/
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


    /*@ ensures \result ==
      @         (\sum int i, j; 0 <= i && i < j && j < people.length
      @                         && people[i].acquaintance.length > 0 && queryBestAcquaintance(people[i].getId()) == people[j].getId()
      @                         && people[j].acquaintance.length > 0 && queryBestAcquaintance(people[j].getId()) == people[i].getId();
      @                         1);
      @*/
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

    /*@ public normal_behavior
  @ requires contains(id1) && contains(id2) && id1 != id2 && getPerson(id1).isLinked(getPerson(id2))
            && getPerson(id1).queryValue(getPerson(id2)) + value > 0;
  @ assignable people;
  @ 1 ensures people.length == \old(people.length); 长度不变
  @ 2 ensures (\forall int i; 0 <= i && i < \old(people.length);
  @          (\exists int j; 0 <= j && j < people.length; people[j].getId() == \old(people[i]).getId())); 人不变
  @ 3 ensures (\forall int i; 0 <= i && i < people.length && \old(people[i].getId()) != id1 &&
  @     \old(people[i].getId()) != id2; \not_assigned(people[i])); 不能对其他 people 赋值
  @ 4 ensures getPerson(id1).isLinked(getPerson(id2)) && getPerson(id2).isLinked(getPerson(id1)); id1 id2 仍相连
  @ 5 ensures getPerson(id1).queryValue(getPerson(id2)) == \old(getPerson(id1).queryValue(getPerson(id2))) + value;
  @ 6 ensures getPerson(id2).queryValue(getPerson(id1)) == \old(getPerson(id2).queryValue(getPerson(id1))) + value; values + value
  @ 7 ensures getPerson(id1).acquaintance.length == \old(getPerson(id1).acquaintance.length);
  @ 8 ensures getPerson(id2).acquaintance.length == \old(getPerson(id2).acquaintance.length); acq 长度不变
  @ 9 ensures (\forall int i; 0 <= i && i < getPerson(id1).acquaintance.length; getPerson(id1).acquaintance[i].equals(\old(getPerson(id1).acquaintance[i]));
  @ 10 ensures (\forall int i; 0 <= i && i < getPerson(id2).acquaintance.length; getPerson(id2).acquaintance[i].equals(\old(getPerson(id2).acquaintance[i])); acq 人不变
  @ 11 ensures (\forall int i; 0 <= i && i < getPerson(id1).acquaintance.length && getPerson(id1).acquaintance[i].getId() != id2;
  @             getPerson(id1).value[i] == \old(getPerson(id1).value[i])); id1 中除 id2 外，value 不变
  @ 12 ensures (\forall int i; 0 <= i && i < getPerson(id2).acquaintance.length && getPerson(id2).acquaintance[i].getId() != id1;
  @             getPerson(id2).value[i] == \old(getPerson(id2).value[i])); 反之亦然
  @ 13 ensures getPerson(id1).value.length == getPerson(id1).acquaintance.length; 保持不变
  @ 14 ensures getPerson(id2).value.length == getPerson(id2).acquaintance.length;
  @ also
  @ public normal_behavior
  @ requires contains(id1) && contains(id2) && id1 != id2 && getPerson(id1).isLinked(getPerson(id2))
  @         && getPerson(id1).queryValue(getPerson(id2)) + value <= 0;
  @ 1 ensures people.length == \old(people.length);
  @ 2 ensures (\forall int i; 0 <= i && i < \old(people.length);
  @          (\exists int j; 0 <= j && j < people.length; people[j] == \old(people[i])));
  @ 3 ensures (\forall int i; 0 <= i && i < people.length && \old(people[i].getId()) != id1 &&
  @     \old(people[i].getId()) != id2; \not_assigned(people[i]));
  @ 15 ensures !getPerson(id1).isLinked(getPerson(id2)) && !getPerson(id2).isLinked(getPerson(id1)); 断了
  @ 16 ensures \old(getPerson(id1).value.length) == getPerson(id1).acquaintance.length + 1;
  @ 17 ensures \old(getPerson(id2).value.length) == getPerson(id2).acquaintance.length + 1; 长度 - 1
  @ 18 ensures getPerson(id1).value.length == getPerson(id1).acquaintance.length;
  @ 19 ensures getPerson(id2).value.length == getPerson(id2).acquaintance.length;
  @ 20 ensures (\forall int i; 0 <= i && i < getPerson(id1).acquaintance.length;
  @         \old(getPerson(id1).acquaintance[i]) == getPerson(id1).acquaintance[i] &&
  @          \old(getPerson(id1).value[i]) == getPerson(id1).value[i]);
  @ 21 ensures (\forall int i; 0 <= i && i < getPerson(id2).acquaintance.length;
  @         \old(getPerson(id2).acquaintance[i]) == getPerson(id2).acquaintance[i] &&
  @          \old(getPerson(id2).value[i]) == getPerson(id2).value[i]); 其余不变
  @ also
  @ public exceptional_behavior
  @ assignable \nothing;
  @ requires !contains(id1) || !contains(id2) || !getPerson(id1).isLinked(getPerson(id2));
  @ signals (PersonIdNotFoundException e) !contains(id1);
  @ signals (PersonIdNotFoundException e) contains(id1) && !contains(id2);
  @ signals (EqualPersonIdException e) contains(id1) && contains(id2) && id1 == id2;
  @ signals (RelationNotFoundException e) contains(id1) && contains(id2) && id1 != id2 &&
  @         !getPerson(id1).isLinked(getPerson(id2));
  @*/
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
        for (Group group : groups.values()) {
            if (group.hasPerson(person1) || group.hasPerson(person2)) {
                ((MyGroup) group).flush();
            }
        }
    }

    public int modifyRelationOKTest(int id1, int id2, int value,
                                    HashMap<Integer, HashMap<Integer, Integer>> beforeData,
                                    HashMap<Integer, HashMap<Integer, Integer>> afterData) {
        //    try {
        //         generateNetWork(beforeData);
        //    } catch (Exception e) {
        //        return 1919810;
        //    }
        if (!beforeData.containsKey(id1) ||
            !beforeData.containsKey(id2) ||
            id1 == id2 ||
            !beforeData.get(id1).containsKey(id2)) {
            return (beforeData.equals(afterData)) ? 0 : -1;
        }

        if (beforeData.size() != afterData.size()) {
            return 1;
        }
        for (int beforeId : beforeData.keySet()) {
            if (!afterData.containsKey(beforeId)) {
                return 2;
            }
        }
        for (int beforeId : beforeData.keySet()) {
            if (beforeId != id1 && beforeId != id2) {
                HashMap<Integer, Integer> beforePerson = beforeData.get(beforeId);
                HashMap<Integer, Integer> afterPerson = afterData.get(beforeId);
                if (!beforePerson.equals(afterPerson)) {
                    return 3;
                }
            }
        }
        HashMap<Integer, Integer> ap1acq = afterData.get(id1);
        HashMap<Integer, Integer> ap2acq = afterData.get(id2);
        HashMap<Integer, Integer> bp1acq = beforeData.get(id1);
        HashMap<Integer, Integer> bp2acq = beforeData.get(id2);
        if (bp1acq.get(id2) + value > 0) {
            if (!afterData.get(id1).containsKey(id2) || !afterData.get(id2).containsKey(id1)) {
                return 4;
            }

            if (value + bp1acq.get(id2) != ap1acq.get(id2)) {
                return 5;
            }
            if (value + bp2acq.get(id1) != ap2acq.get(id1)) {
                return 6;
            }
            if (bp1acq.size() != ap1acq.size()) {
                return 7;
            }
            if (bp2acq.size() != ap2acq.size()) {
                return 8;
            }
            for (int acq1 : bp1acq.keySet()) {
                if (!ap1acq.containsKey(acq1)) {
                    return 9;
                }
            }
            for (int acq2 : bp2acq.keySet()) {
                if (!ap2acq.containsKey(acq2)) {
                    return 10;
                }
            }
            for (int acq1 : bp1acq.keySet()) {
                if (acq1 != id2 && !Objects.equals(bp1acq.get(acq1), ap1acq.get(acq1))) {
                    return 11;
                }
            }
            for (int acq2 : bp2acq.keySet()) {
                if (acq2 != id1 && !Objects.equals(bp2acq.get(acq2), ap2acq.get(acq2))) {
                    return 12;
                }
            }
        } else {
            if (ap1acq.containsKey(id2) || ap2acq.containsKey(id1)) {
                return 15;
            }
            if (ap1acq.size() + 1 != bp1acq.size()) {
                return 16;
            }
            if (ap2acq.size() + 1 != bp2acq.size()) {
                return 17;
            }
        }
        return 0;
    }
}
