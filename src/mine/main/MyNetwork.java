package mine.main;

import com.oocourse.spec3.exceptions.AcquaintanceNotFoundException;
import com.oocourse.spec3.exceptions.EmojiIdNotFoundException;
import com.oocourse.spec3.exceptions.EqualEmojiIdException;
import com.oocourse.spec3.exceptions.EqualGroupIdException;
import com.oocourse.spec3.exceptions.EqualMessageIdException;
import com.oocourse.spec3.exceptions.EqualPersonIdException;
import com.oocourse.spec3.exceptions.EqualRelationException;
import com.oocourse.spec3.exceptions.GroupIdNotFoundException;
import com.oocourse.spec3.exceptions.MessageIdNotFoundException;
import com.oocourse.spec3.exceptions.PathNotFoundException;
import com.oocourse.spec3.exceptions.PersonIdNotFoundException;
import com.oocourse.spec3.exceptions.RelationNotFoundException;

import com.oocourse.spec3.main.EmojiMessage;
import com.oocourse.spec3.main.Group;
import com.oocourse.spec3.main.Message;
import com.oocourse.spec3.main.Network;
import com.oocourse.spec3.main.Person;

import com.oocourse.spec3.main.RedEnvelopeMessage;
import mine.exceptions.MyAcquaintanceNotFoundException;
import mine.exceptions.MyEmojiIdNotFoundException;
import mine.exceptions.MyEqualEmojiIdException;
import mine.exceptions.MyEqualGroupIdException;
import mine.exceptions.MyEqualMessageIdException;
import mine.exceptions.MyEqualPersonIdException;
import mine.exceptions.MyEqualRelationException;
import mine.exceptions.MyGroupIdNotFoundException;
import mine.exceptions.MyMessageIdNotFoundException;
import mine.exceptions.MyPathNotFoundException;
import mine.exceptions.MyPersonIdNotFoundException;
import mine.exceptions.MyRelationNotFoundException;
import mine.tool.Arc;
import mine.tool.Dijkstra;
import mine.tool.Edge;
import mine.tool.Union;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyNetwork implements Network {
    private final HashMap<Integer, Person> people = new HashMap<>();
    private final HashMap<Integer, Group> groups = new HashMap<>();
    private final HashMap<Integer, Message> messages = new HashMap<>();
    private HashMap<Integer, Integer> emojis = new HashMap<>();

    private final HashMap<Integer, Integer> couples = new HashMap<>();
    private final HashMap<Arc, Integer> arcPools = new HashMap<>();
    private final HashMap<Integer, HashMap<Edge, Integer>> modifiedPools = new HashMap<>();

    private int triCount = 0;
    private final Union unionMap = new Union();

    // people 集合时刻不为空，且不重复

    public MyNetwork() { }

    public boolean contains(int id) { return people.containsKey(id); }

    public Person getPerson(int id) { return people.getOrDefault(id, null); }

    public void addPerson(Person person) throws EqualPersonIdException {
        int id = person.getId();
        if (people.containsKey(id)) { throw new MyEqualPersonIdException(id); }
        people.put(person.getId(), person);
        modifiedPools.put(person.getId(), new HashMap<>());
        unionMap.addUnion(person.getId(), person.getId());
    }

    public void addRelation(int id1, int id2, int value) throws
            PersonIdNotFoundException, EqualRelationException {

        if (!people.containsKey(id1)) { throw new MyPersonIdNotFoundException(id1); }
        if (!people.containsKey(id2)) { throw new MyPersonIdNotFoundException(id2); }
        boolean isLinked = people.get(id1).isLinked(people.get(id2));
        if (isLinked) { throw new MyEqualRelationException(id1, id2); }

        Person p1 = people.get(id1);
        Person p2 = people.get(id2);

        dynamicTri(id1, id2, true);

        ((MyPerson) p1).addRelation((MyPerson) p2, value);
        ((MyPerson) p2).addRelation((MyPerson) p1, value);
        arcPools.put(new Arc(id1, id2, value), value);
        modifiedPools.get(id1).put(new Edge(id2, value), value);
        modifiedPools.get(id2).put(new Edge(id1, value), value);

        for (int groupId : ((MyPerson) p1).getGroupList()) {
            ((MyGroup) groups.get(groupId)).flushCachedValueSum();
        }
        for (int groupId : ((MyPerson) p2).getGroupList()) {
            ((MyGroup) groups.get(groupId)).flushCachedValueSum();
        }
        unionMap.union(Math.min(p1.getId(), p2.getId()), Math.max(p1.getId(), p2.getId()));
    }

    public int queryValue(int id1, int id2) throws
            PersonIdNotFoundException, RelationNotFoundException {

        if (!people.containsKey(id1)) { throw new MyPersonIdNotFoundException(id1); }
        if (!people.containsKey(id2)) { throw new MyPersonIdNotFoundException(id2); }
        boolean isLinked = people.get(id1).isLinked(people.get(id2));
        if (!isLinked) { throw new MyRelationNotFoundException(id1, id2); }
        return people.get(id1).queryValue(people.get(id2));
    }

    public boolean isCircle(int id1, int id2) throws PersonIdNotFoundException {
        if (!people.containsKey(id1)) { throw new MyPersonIdNotFoundException(id1); }
        if (!people.containsKey(id2)) { throw new MyPersonIdNotFoundException(id2); }

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

    public void generateNetWork(ArrayList<HashMap<Integer, Integer>> data) {
        emojis = data.get(0);
        for (int messageId : data.get(1).keySet()) {
            // System.out.println(data.get(1).get(messageId));
            if (data.get(1).get(messageId) == null) {
                messages.put(messageId, new MyMessage(messageId));
            } else {
                messages.put(messageId, new MyEmojiMessage(messageId, data.get(1).get(messageId)));
            }
        }
    }
    // ------------------------------------------------------------------------

    public void addGroup(Group group) throws EqualGroupIdException {
        if (groups.containsKey(group.getId())) { throw new MyEqualGroupIdException(group.getId()); }
        groups.put(group.getId(), group);
        ((MyGroup) group).loadArcPools(arcPools);
    }

    public Group getGroup(int id) { return groups.getOrDefault(id, null); }

    public void addToGroup(int id1, int id2) throws GroupIdNotFoundException,
            PersonIdNotFoundException, EqualPersonIdException {
        if (!groups.containsKey(id2)) { throw new MyGroupIdNotFoundException(id2); }
        if (!people.containsKey(id1)) { throw new MyPersonIdNotFoundException(id1); }
        MyGroup group = (MyGroup) (groups.get(id2));
        if (group.containsPerson(id1)) { throw new MyEqualPersonIdException(id1); }
        Person person = people.get(id1);

        if (group.getSize() <= 1111) {
            group.addPerson(person);
            ((MyPerson) person).addInGroup(id2);
        }
        // bie-bie
    }

    public int queryGroupValueSum(int id) throws GroupIdNotFoundException {
        if (!groups.containsKey(id)) { throw new MyGroupIdNotFoundException(id); }
        return groups.get(id).getValueSum();
    }

    public int queryGroupAgeVar(int id) throws GroupIdNotFoundException {
        if (!groups.containsKey(id)) { throw new MyGroupIdNotFoundException(id); }

        return groups.get(id).getAgeVar();
    }

    public void delFromGroup(int id1, int id2)
            throws GroupIdNotFoundException, PersonIdNotFoundException, EqualPersonIdException {
        if (!groups.containsKey(id2)) { throw new MyGroupIdNotFoundException(id2); }
        if (!people.containsKey(id1)) { throw new MyPersonIdNotFoundException(id1); }
        MyGroup group = (MyGroup) (groups.get(id2));
        if (!group.containsPerson(id1)) { throw new MyEqualPersonIdException(id1); }

        group.delPerson(people.get(id1));
    }

    public boolean containsMessage(int id) { return messages.containsKey(id); }

    public void addMessage(Message message) throws
            EqualMessageIdException, EmojiIdNotFoundException, EqualPersonIdException {
        if (messages.containsKey(message.getId())) {
            throw new MyEqualMessageIdException(message.getId());
        }
        if (message instanceof EmojiMessage &&
            !emojis.containsKey(((EmojiMessage) message).getEmojiId())) {
            throw new MyEmojiIdNotFoundException(((EmojiMessage) message).getEmojiId());
        }
        if (message.getType() == 0 &&
            message.getPerson1().getId() == message.getPerson2().getId()) {
            throw new MyEqualPersonIdException(message.getPerson1().getId());
        }
        messages.put(message.getId(), message);
    }

    public Message getMessage(int id) { return messages.getOrDefault(id, null); }

    /*@ public normal_behavior
          @ requires containsMessage(id) && getMessage(id).getType() == 0 &&
          @          getMessage(id).getPerson1().isLinked(getMessage(id).getPerson2()) &&
          @          getMessage(id).getPerson1() != getMessage(id).getPerson2();
          @ type == 0 私聊
          @ assignable messages[*], emojiHeatList[*];
          @ assignable getMessage(id).getPerson1().socialValue, getMessage(id).getPerson1().money;
          @ assignable getMessage(id).getPerson2().messages, getMessage(id).getPerson2().socialValue, getMessage(id).getPerson2().money;
          @ ensures !containsMessage(id) && messages.length == \old(messages.length) - 1 &&
          @         (\forall int i; 0 <= i && i < \old(messages.length) && \old(messages[i].getId()) != id;
          @         (\exists int j; 0 <= j && j < messages.length; messages[j].equals(\old(messages[i]))));
          @ 少一条消息，其余消息不变
          @ ensures \old(getMessage(id)).getPerson1().getSocialValue() ==
          @         \old(getMessage(id).getPerson1().getSocialValue()) + \old(getMessage(id)).getSocialValue() &&
          @         \old(getMessage(id)).getPerson2().getSocialValue() ==
          @         \old(getMessage(id).getPerson2().getSocialValue()) + \old(getMessage(id)).getSocialValue();
          @ socialValue 增加
          @ ensures (\old(getMessage(id)) instanceof RedEnvelopeMessage) ==>
          @         (\old(getMessage(id)).getPerson1().getMoney() ==
          @         \old(getMessage(id).getPerson1().getMoney()) - ((RedEnvelopeMessage)\old(getMessage(id))).getMoney() &&
          @         \old(getMessage(id)).getPerson2().getMoney() ==
          @         \old(getMessage(id).getPerson2().getMoney()) + ((RedEnvelopeMessage)\old(getMessage(id))).getMoney());
          @ 红包就从 1 转给 2
          @ ensures (!(\old(getMessage(id)) instanceof RedEnvelopeMessage)) ==> (\not_assigned(people[*].money));
          @ 其他消息钱不变
          @ ensures (\old(getMessage(id)) instanceof EmojiMessage) ==>
          @         (\exists int i; 0 <= i && i < emojiIdList.length && emojiIdList[i] == ((EmojiMessage)\old(getMessage(id))).getEmojiId();
          @         emojiHeatList[i] == \old(emojiHeatList[i]) + 1);
          @ 表情消息给 emoji 的 heat + 1 (value + 1)
          @ ensures (!(\old(getMessage(id)) instanceof EmojiMessage)) ==> \not_assigned(emojiHeatList);
          @ 其他消息 heat 不变
          @ ensures (\forall int i; 0 <= i && i < \old(getMessage(id).getPerson2().getMessages().size());
          @          \old(getMessage(id)).getPerson2().getMessages().get(i+1) == \old(getMessage(id).getPerson2().getMessages().get(i)));
          @ 所有消息向后移动
          @ ensures \old(getMessage(id)).getPerson2().getMessages().get(0).equals(\old(getMessage(id)));
          @ 新消息头插消息队列
          @ ensures \old(getMessage(id)).getPerson2().getMessages().size() == \old(getMessage(id).getPerson2().getMessages().size()) + 1;
          @ size 变动
          @ also
          @ public normal_behavior
          @ requires containsMessage(id) && getMessage(id).getType() == 1 &&
          @           getMessage(id).getGroup().hasPerson(getMessage(id).getPerson1());
          @ type == 1 群发
          @ assignable people[*].socialValue, people[*].money, messages, emojiHeatList;
          @ ensures !containsMessage(id) && messages.length == \old(messages.length) - 1 &&
          @         (\forall int i; 0 <= i && i < \old(messages.length) && \old(messages[i].getId()) != id;
          @         (\exists int j; 0 <= j && j < messages.length; messages[j].equals(\old(messages[i]))));
          @ ensures (\forall Person p; \old(getMessage(id)).getGroup().hasPerson(p); p.getSocialValue() ==
          @         \old(p.getSocialValue()) + \old(getMessage(id)).getSocialValue());
          @ ensures (\forall int i; 0 <= i && i < people.length && !\old(getMessage(id)).getGroup().hasPerson(people[i]);
          @          \old(people[i].getSocialValue()) == people[i].getSocialValue());
          @ socialValue 的更改
          @ ensures (\old(getMessage(id)) instanceof RedEnvelopeMessage) ==>
          @          (\exists int i; i == ((RedEnvelopeMessage)\old(getMessage(id))).getMoney()/\old(getMessage(id)).getGroup().getSize();
          @           \old(getMessage(id)).getPerson1().getMoney() ==
          @           \old(getMessage(id).getPerson1().getMoney()) - i*(\old(getMessage(id)).getGroup().getSize() - 1) &&
          @           (\forall Person p; \old(getMessage(id)).getGroup().hasPerson(p) && p != \old(getMessage(id)).getPerson1();
          @           p.getMoney() == \old(p.getMoney()) + i));
          @ 组内其余人钱增加 money / group
          @ ensures (\old(getMessage(id)) instanceof RedEnvelopeMessage) ==>
          @          (\forall int i; 0 <= i && i < people.length && !\old(getMessage(id)).getGroup().hasPerson(people[i]);
          @           \old(people[i].getMoney()) == people[i].getMoney());
          @ 组外人钱不变
          @ ensures (!(\old(getMessage(id)) instanceof RedEnvelopeMessage)) ==> (\not_assigned(people[*].money));
          @ 非红包不变钱
          @ ensures (\old(getMessage(id)) instanceof EmojiMessage) ==>
          @         (\exists int i; 0 <= i && i < emojiIdList.length && emojiIdList[i] == ((EmojiMessage)\old(getMessage(id))).getEmojiId();
          @          emojiHeatList[i] == \old(emojiHeatList[i]) + 1);
          @ 表情信息对应的 emoji 的 heat + 1
          @ ensures (!(\old(getMessage(id)) instanceof EmojiMessage)) ==> \not_assigned(emojiHeatList);
          @ 非 emoji 不动 heat
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
        if (!messages.containsKey(id)) { throw new MyMessageIdNotFoundException(id); }
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

            if (message instanceof RedEnvelopeMessage) {
                person1.addMoney(- ((RedEnvelopeMessage) message).getMoney());
                person2.addMoney(+ ((RedEnvelopeMessage) message).getMoney());
            }
        } else {
            HashMap<Integer, Person> peopleList = ((MyGroup) message.getGroup()).getPeople();
            for (Person person : peopleList.values()) {
                person.addSocialValue(socialValue);
            }

            if (message instanceof RedEnvelopeMessage) {
                ((MyGroup) message.getGroup()).sendR(person1.getId(), (RedEnvelopeMessage) message);
            }
        }
        if (message instanceof EmojiMessage) {
            int emojiId = ((EmojiMessage) message).getEmojiId();
            emojis.put(emojiId, emojis.get(emojiId) + 1);
        }
        messages.remove(message.getId());
    }

    public int querySocialValue(int id) throws PersonIdNotFoundException {
        if (!people.containsKey(id)) { throw new MyPersonIdNotFoundException(id); }

        return people.get(id).getSocialValue();
    }

    public List<Message> queryReceivedMessages(int id) throws PersonIdNotFoundException {
        if (!people.containsKey(id)) { throw new MyPersonIdNotFoundException(id); }

        return people.get(id).getReceivedMessages();
    }

    public int queryBestAcquaintance(int id) throws
            PersonIdNotFoundException, AcquaintanceNotFoundException {
        if (!people.containsKey(id)) { throw new MyPersonIdNotFoundException(id); }
        MyPerson person = (MyPerson) (people.get(id));
        if (person.getAcquaintance().isEmpty()) { throw new MyAcquaintanceNotFoundException(id); }

        return person.getCouple();
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
        if (!people.containsKey(id1)) { throw new MyPersonIdNotFoundException(id1); }
        if (!people.containsKey(id2)) { throw new MyPersonIdNotFoundException(id2); }
        if (id1 == id2) { throw new MyEqualPersonIdException(id1); }
        boolean isLinked = people.get(id1).isLinked(people.get(id2));
        if (!isLinked) { throw new MyRelationNotFoundException(id1, id2); }

        Person person1 = people.get(id1);
        Person person2 = people.get(id2);

        if (person1.queryValue(person2) + value > 0) {
            ((MyPerson) person1).addValue(id2, value);
            ((MyPerson) person2).addValue(id1, value);
            int valueOld = arcPools.get(new Arc(id1, id2, 1));
            arcPools.put(new Arc(id1, id2, value + valueOld), valueOld + value);
            modifiedPools.get(id1).remove(new Edge(id2, valueOld));
            modifiedPools.get(id1).put(new Edge(id2, value + valueOld), valueOld + value);
            modifiedPools.get(id2).remove(new Edge(id1, valueOld));
            modifiedPools.get(id2).put(new Edge(id1, value + valueOld), valueOld + value);
        } else {
            // relation broken cause: person/group cache failure, tri changed, union map rebuild
            ((MyPerson) person1).delRelation(id2);
            ((MyPerson) person2).delRelation(id1);

            dynamicTri(id1, id2, false);

            unionMap.setVisited(people.size());
            unionMap.rebuildPart(id1, id1, people);
            unionMap.setVisited(people.size());
            unionMap.rebuildPart(id2, id2, people);
            int valueOld = arcPools.get(new Arc(id1, id2, 1));
            arcPools.remove(new Arc(id1, id2, 1));
            modifiedPools.get(id1).remove(new Edge(id2, valueOld));
            modifiedPools.get(id2).remove(new Edge(id1, valueOld));
        }
        for (int groupId : ((MyPerson) person1).getGroupList()) {
            ((MyGroup) groups.get(groupId)).flushCachedValueSum();
        }
        for (int groupId : ((MyPerson) person2).getGroupList()) {
            ((MyGroup) groups.get(groupId)).flushCachedValueSum();
        }
    }
    // ------------------------------------------------------------------------

    public boolean containsEmojiId(int id) { return emojis.containsKey(id); }

    public void storeEmojiId(int id) throws EqualEmojiIdException {
        if (emojis.containsKey(id)) { throw new MyEqualEmojiIdException(id); }

        emojis.put(id, 0);
    }

    public int queryMoney(int id) throws PersonIdNotFoundException {
        if (!people.containsKey(id)) { throw new MyPersonIdNotFoundException(id); }

        return people.get(id).getMoney();
    }

    public int queryPopularity(int id) throws EmojiIdNotFoundException {
        if (!emojis.containsKey(id)) { throw new MyEmojiIdNotFoundException(id); }

        return emojis.get(id);
    }

    public int deleteColdEmoji(int limit) {
        /* 1.先改 emojis，每个 emoji 去删除自己的 Message，两重循环
         * 2.遍历 Message，让 EmojiMessage 去查自己的 emoji 还在不在，不在了自己也就没了，一重循环
         * 这里所有能遍历的 EmojiMessage 都还没 send，所以不会改变什么 SocialValue 值
         */
        emojis.keySet().removeIf(emojiId -> (emojis.get(emojiId) < limit));

        messages.keySet().removeIf(messageId ->
                messages.get(messageId) instanceof EmojiMessage &&
                !emojis.containsKey(((EmojiMessage) messages.get(messageId)).getEmojiId()));

        return emojis.size();
    }

    public void clearNotices(int personId) throws PersonIdNotFoundException {
        if (!people.containsKey(personId)) { throw new MyPersonIdNotFoundException(personId); }

        ((MyPerson) people.get(personId)).clearNotices();
    }

    /*@ public normal_behavior
  @ requires contains(id) && (\exists Person[] path;
  @         path.length >= 4;
  @         path[0].equals(getPerson(id)) &&
  @         path[path.length - 1].equals(getPerson(id)) &&
  @         (\forall int i; 1 <= i && i < path.length; path[i - 1].isLinked(path[i])) &&
  @         (\forall int i, j; 1 <= i && i < j && j < path.length; !path[i].equals(path[j])));
  @ path 是一个环，节点不允许多次访问，首尾均是自己，长度 >= 4
  @ ensures (\exists Person[] pathM;
  @         pathM.length >= 4 &&
  @         pathM[0].equals(getPerson(id)) &&
  @         pathM[pathM.length - 1].equals(getPerson(id)) &&
  @         (\forall int i; 1 <= i && i < pathM.length; pathM[i - 1].isLinked(pathM[i])) &&
  @         (\forall int i, j; 1 <= i && i < j && j < pathM.length; !pathM[i].equals(pathM[j]));
  @         (\forall Person[] path;
  @         path.length >= 4 &&
  @         path[0].equals(getPerson(id)) &&
  @         path[path.length - 1].equals(getPerson(id)) &&
  @         (\forall int i; 1 <= i && i < path.length; path[i - 1].isLinked(path[i])) &&
  @         (\forall int i, j; 1 <= i && i < j && j < path.length; !path[i].equals(path[j]));
  @         (\sum int i; 1 <= i && i < path.length; path[i - 1].queryValue(path[i])) >=
  @         (\sum int i; 1 <= i && i < pathM.length; pathM[i - 1].queryValue(pathM[i]))) &&
  @         \result==(\sum int i; 1 <= i && i < pathM.length; pathM[i - 1].queryValue(pathM[i])));
  @ 返回自己到自己的最短距离，至少经过两个其他的点
  @ also
  @ public exceptional_behavior
  @ signals (PersonIdNotFoundException e) !contains(id);
  @ signals (PathNotFoundException e) contains(id) && !(\exists Person[] path;
  @         path.length >= 4;
  @         path[0].equals(getPerson(id)) &&
  @         path[path.length - 1].equals(getPerson(id)) &&
  @         (\forall int i; 1 <= i && i < path.length; path[i - 1].isLinked(path[i])) &&
  @         (\forall int i, j; 1 <= i && i < j && j < path.length; !path[i].equals(path[j])));
  @*/
    public int queryLeastMoments(int id) throws PersonIdNotFoundException, PathNotFoundException {
        if (!people.containsKey(id)) { throw new MyPersonIdNotFoundException(id); }
        /* dijstra 求自己距离自己的最短路
         *
         * 类似于多源最短路径，增加一个“替身”的临时节点，替身拥有所有相同的边
         * 随后求替身到真身的 dijstra，堆优化后 O((m + n)logn)
         */

        //        int replica = 114514;
        //        while (modifiedPools.containsKey(replica)) {
        //            replica++;
        //        }
        //        // get replicaId
        //        modifiedPools.put(replica, new HashMap<>());
        //        for (Edge edge : modifiedPools.get(id).keySet()) {
        //            int to = edge.getId();
        //            int distance = edge.getDistance();
        //            modifiedPools.get(replica).put(new Edge(to, distance), distance);
        //            // other nodes don't need to request replica, so there's no need to add the reverse edge
        //        }
        //        // make fake relation
        //        int result = Dijkstra.makeDijkstra(replica, id, modifiedPools);
        //        modifiedPools.remove(replica);
        //        // restore the modifiedPool
        //        if (result == -1) { throw new MyPathNotFoundException(id); }
        //        return result;
        int result = -1;

        for (int friend : ((MyPerson) people.get(id)).getAcquaintance().keySet()) {
            int value = people.get(id).queryValue(people.get(friend));
            modifiedPools.get(id).remove(new Edge(friend, value));
            modifiedPools.get(friend).remove(new Edge(id, value));
            int temp = Dijkstra.makeDijkstra(id, friend, modifiedPools);
            if (temp != -1 && (result > temp + value || result == -1)) {
                result = temp + value;
            }
            modifiedPools.get(id).put(new Edge(friend, value), value);
            modifiedPools.get(friend).put(new Edge(id, value), value);
        }

        if (result == -1) {
            throw new MyPathNotFoundException(id);
        }
        return result;
    }

    /*
    ap 1 1 1
    ap 2 2 2
    ap 3 3 3
    ap 4 4 4
    ar 1 2 10
    ar 2 3 20
    ar 1 3 30
    ar 2 4 10
    qlm 4
    */
    /*@ public normal_behavior
      @ assignable emojiIdList, emojiHeatList, messages;
      @ 1 ensures (\forall int i; 0 <= i && i < \old(emojiIdList.length);
      @          (\old(emojiHeatList[i] >= limit) ==>
      @          (\exists int j; 0 <= j && j < emojiIdList.length; emojiIdList[j] == \old(emojiIdList[i]))));
      @ 大于等于 limit 的 emoji 要留下，允许无序，先不管小于的！
      @ 2 ensures (\forall int i; 0 <= i && i < emojiIdList.length;
      @          (\exists int j; 0 <= j && j < \old(emojiIdList.length);
      @          emojiIdList[i] == \old(emojiIdList[j]) && emojiHeatList[i] == \old(emojiHeatList[j])));
      @ 每个留下来的 emoji 的 heat 不变
      @ 3 ensures emojiIdList.length ==
      @          (\num_of int i; 0 <= i && i < \old(emojiIdList.length); emojiHeatList[i] >= limit);
      @ 只有大于等于 limit 的 emoji 才能留下
      @ 4 ensures emojiIdList.length == emojiHeatList.length;
      @ HashMap 长度一样
      @ 5 ensures (\forall int i; 0 <= i && i < \old(messages.length);
      @          (\old(messages[i]) instanceof EmojiMessage &&
      @           containsEmojiId(\old(((EmojiMessage)messages[i]).getEmojiId()))  ==>
      @           (\exists int j; 0 <= j && j < messages.length; messages[j].equals(\old(messages[i])))));
      @ 自己 emoji 没被删的 EmojiMessage 不变，同样不管被删的和其它类型的(null的)
      @ 6 ensures (\forall int i; 0 <= i && i < \old(messages.length);
      @          (!(\old(messages[i]) instanceof EmojiMessage) ==>
      @           (\exists int j; 0 <= j && j < messages.length; messages[j].equals(\old(messages[i])))));
      @ 所有非 EmojiMessage 都不变
      @ 7 ensures messages.length == (\num_of int i; 0 <= i && i <= \old(messages.length);
      @          (\old(messages[i]) instanceof EmojiMessage) ==>
      @           (containsEmojiId(\old(((EmojiMessage)messages[i]).getEmojiId()))));
      @ 只有 emoji 没被删的 EmojiMessage 才能留下
      @ 8 ensures \result == emojiIdList.length;
      @ 返回剩余 emoji 的长度
      @*/
    public int deleteColdEmojiOKTest(int limit, ArrayList<HashMap<Integer, Integer>> beforeData,
                                     ArrayList<HashMap<Integer, Integer>> afterData, int result) {
        // System.out.println(beforeData);
        // System.out.println(afterData);

        HashMap<Integer, Integer> beforeEmojis;
        HashMap<Integer, Integer> afterEmojis;
        beforeEmojis = beforeData.get(0);
        afterEmojis = afterData.get(0);

        HashMap<Integer, Integer> beforeMessages;
        HashMap<Integer, Integer> afterMessages;
        beforeMessages = beforeData.get(1);
        afterMessages = afterData.get(1);

        for (int emojiId : beforeEmojis.keySet()) {
            if (beforeEmojis.get(emojiId) >= limit && !afterEmojis.containsKey(emojiId)) {
                return 1;
            }
        } // 1
        for (int emojiId : afterEmojis.keySet()) {
            if (!beforeEmojis.containsKey(emojiId) ||
                !beforeEmojis.get(emojiId).equals(afterEmojis.get(emojiId))) {
                return 2;
            }
        } // 2
        for (int emojiId : afterEmojis.keySet()) {
            if (afterEmojis.get(emojiId) < limit) {
                return 3;
            }
        } // 3
        for (int messageId : beforeMessages.keySet()) {
            if (beforeMessages.get(messageId) != null &&  // EmojiMessage
                afterEmojis.containsKey(beforeMessages.get(messageId)) && // containsEmoji
                !afterMessages.get(messageId).equals(beforeMessages.get(messageId))) {
                // !equals (id==, emojiId!=)
                return 5;
            }
        } // 5
        for (int messageId : beforeMessages.keySet()) {
            if (beforeMessages.get(messageId) == null &&
                (!afterMessages.containsKey(messageId) || afterMessages.get(messageId) != null)) {
                return 6;
            }
        } // 6
        for (int messageId : beforeMessages.keySet()) {
            if (beforeMessages.get(messageId) != null && // is EmojiMessage
                beforeEmojis.get(beforeMessages.get(messageId)) < limit && // need del
                afterMessages.containsKey(messageId)) {  // not del
                return 7;
            }
        } // 7
        generateNetWork(beforeData);
        if (result != deleteColdEmoji(limit)) {
            return 8;
        } // 8
        return 0;
    }
}
