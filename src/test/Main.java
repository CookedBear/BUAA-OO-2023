package test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import mine.exceptions.MyEqualRelationException;
import mine.main.MyNetwork;
import spec1.exceptions.EqualPersonIdException;

import static mine.exceptions.ExceptionCounter.initCauses;

public class Main {

    public static boolean DEBUG = true;
    public static int INS_NUM;
    public static boolean MROK_ABLE = false;
    public static boolean MROKTEST = false;

    public static int PERSONID = 1;
    public static int GROUPID = 1;
    public static int MESSAGEID = 1;
    public static ArrayList<String> NAMEPOOL = new ArrayList<>();
    public static HashMap<Integer, Person> PERSONPOOL = new HashMap<>();
    public static HashMap<Integer, Group> GROUPPOOL = new HashMap<>();
    public static HashMap<Integer, Message> MESSAGEPOOL = new HashMap<>();

    public static ArrayList<Integer> FAKEIDPOOL = new ArrayList<>();
    public static ArrayList<Integer> FAKEGROUPPOOL = new ArrayList<>();
    public static HashMap<Arc, Integer> ARCPOOL = new HashMap<>();
    public static ArrayList<String> OUTPUTS = new ArrayList<>();
    public static ArrayList<String> MROKOUTS = new ArrayList<>();

    public static void main(String[] args) throws MyEqualRelationException, EqualPersonIdException {
        Random random = new Random();
        modeSelect(args, random);
        fakePoolInit(random);

        generateCode(random);

        printout();
    }

    public static void modeSelect(String[] args, Random random) {
        if (args.length > 0) {
            switch (args[0]) {
                case "-strong":
                    if (random.nextInt(3) == 0) {
                        MROKTEST = true;
                    }
                    if (!MROKTEST) {
                        INS_NUM = (random.nextInt(499) + 1) * 20;
                    } else {
                        INS_NUM = (random.nextInt(499) + 1) * 20;
                    }
                    break;
                case "-mutual":
                    INS_NUM = (random.nextInt(199) + 1) * 10;
                    MROK_ABLE = false;
                    break;
                case "-mutual_manual":
                    INS_NUM = Integer.parseInt(args[1]);
                    MROK_ABLE = false;
                    break;
                case "-mroktest":
                    MROKTEST = true;
                    INS_NUM = random.nextInt(50) + 5;
                    break;
                case "-manual":
                    if (args.length < 2) {
                        System.out.println("Wrong parameter type!");
                        System.exit(-1);
                    }
                    INS_NUM = Integer.parseInt(args[1]);
                    MROK_ABLE = true;
                    if (random.nextInt(3) == 0) {
                        MROKTEST = true;
                    }
                    break;
                case "-normal":
                default:
                    if (!MROKTEST) {
                        INS_NUM = random.nextInt(200) + 30;
                    } else {
                        INS_NUM = random.nextInt(200) + 30;
                    }
            }
        } else {
            MROK_ABLE = true;
            MROKTEST = random.nextBoolean();
            if (!MROKTEST) {
                INS_NUM = random.nextInt(50) + 30;
            } else {
                INS_NUM = random.nextInt(50) + 30;
            }
        }

        String modeName = (args.length == 0) ? "normal" : args[0].substring(1);
        System.out.printf("Now mode: %s\nINS_NUM = %d\nMROK = %b\n",modeName, INS_NUM, MROKTEST);
    }

    public static void fakePoolInit(Random random) {
        for (int i = 0; i < INS_NUM / 30 + 1; i++) {
            int id = random.nextInt(10000) - 20000;
            while (FAKEIDPOOL.contains(id)) {
                id = random.nextInt(10000) - 20000;
            }
            FAKEIDPOOL.add(id);
        }
        for (int i = 0; i < INS_NUM / 100 + 1; i++) {
            int id = random.nextInt(10000);
            while (FAKEGROUPPOOL.contains(id)) {
                id = random.nextInt(10000);
            }
            FAKEGROUPPOOL.add(id);
        }
    }

    public static void generateCode(Random random) throws MyEqualRelationException, EqualPersonIdException {
        if (true) {
            for (int i = 0; i < INS_NUM; i++) {
                if (i <= INS_NUM / 10) {
                    addPerson(random);
                } else if (i <= INS_NUM / 5) {
                    addRelation(random);
                } else if (i <= INS_NUM * 3 / 10) {
                    addGroup(random);
                } else if (i <= INS_NUM * 2 / 5) {
                    addToGroup(random);
                } else if (i <= INS_NUM / 2) {
                    addMessage(random);
                } else {
                    randomIns(random);
                }
            }
        } else {
            for (int i = 0; i < INS_NUM; i++) {
                modifyRelationOkTest(random);
            }
        }
    }

    /*
    * add_person id(int) name(String) age(int)
    *
    * name(String) 长度不超过 10
    * age(int)值在 [0,200] 中
    * */
    public static void addPerson(Random random) {
        String name = String.valueOf(random.nextInt());
        while (NAMEPOOL.contains(name)) {
            name = String.valueOf(random.nextInt());
        }
        NAMEPOOL.add(name);
        int age = random.nextInt(201);
        PERSONPOOL.put(PERSONID, new Person(PERSONID++, name, age));
        OUTPUTS.add(String.format("ap %d %s %d\n", PERSONID - 1, name, age));
        debugF(DEBUG, String.format("addPerson id= %d name= %s age= %d", PERSONID - 1, name, age));
    }

    /*
     * add_relation id(int) id(int) value(int)
     *
     * requires:
     * value(int)值在 [1,100] 中
     *
     * exception:
     * pinf
     * er
     * */
    public static void addRelation(Random random) {
        int value = random.nextInt(100) + 1;
        Integer[] keys = PERSONPOOL.keySet().toArray(new Integer[0]);
        int type = random.nextInt(5);
        int id1 = keys[random.nextInt(keys.length)];
        int id2 = keys[random.nextInt(keys.length)];
        String ty;
        if (type <= 2) { // normal
            ty = "normal";
            if (id1 != id2 && !PERSONPOOL.get(id1).acq.containsKey(id2)) {
                PERSONPOOL.get(id1).acq.put(id2, value);
                PERSONPOOL.get(id2).acq.put(id1, value);
            }
        } else if (type == 3) { // pinf
            boolean mode = random.nextBoolean();
            boolean fake = true;
            if (mode) {
                fake = random.nextBoolean();
            }
            id1 = randomFakeId(random, fake);
            id2 = randomFakeId(random, fake);
            ty = "pinf";
        } else if (!ARCPOOL.isEmpty()){ // er
            Arc[] arcs = ARCPOOL.keySet().toArray(new Arc[0]);
            Arc arc = arcs[random.nextInt(arcs.length)];
            id1 = arc.p1Id;
            id2 = arc.p2Id;
            ty = "er";
        } else {
            ty = "normal";
        }

        ARCPOOL.put(new Arc(Math.min(id1, id2), Math.max(id1, id2), value), 1);
        OUTPUTS.add(String.format("ar %d %d %d\n", id1, id2, value));
        debugF(DEBUG, String.format("addRelation type=%s id1= %d id2= %d value= %d", ty, id1, id2, value));

    }

    /*
    * query_value id(int) id(int)
    *
    * exception:
    * rnf
    * pinf
    * */
    public static void queryValue(Random random) {
        int type = random.nextInt(3);
        int id1;
        int id2;
        if (type == 0) { // real query
            Arc arc = randomArc(random);
            id1 = arc.p1Id;
            id2 = arc.p2Id;
        } else if (type == 1) { // RNF Situation
            id1 = randomPerson(random).id;
            id2 = randomPerson(random).id;
            while (PERSONPOOL.get(id1).acq.containsKey(id2)) {
                id2 = randomPerson(random).id;
            }
        } else { // PIF Situation
            boolean mode = random.nextBoolean();
            boolean fake = true;
            if (mode) {
                fake = random.nextBoolean();
            }
            id1 = randomFakeId(random, fake);
            id2 = randomFakeId(random, fake);
        }

        String ty = (type == 0) ? "normal" :
                (type == 1) ? "rnf" : "pinf";
        OUTPUTS.add(String.format("qv %d %d\n", id1, id2));
        debugF(DEBUG, String.format("queryValue type is: %s id1= %d id2 = %d", ty, id1, id2));
    }

    /*
    * query_circle id(int) id(int)
    *
    * exception:
    * pinf
    * */
    public static void queryCircle(Random random) {
        int type = random.nextInt(2);
        int id1, id2;
        if (type == 0) { // normal
            id1 = randomPerson(random).id;
            id2 = randomPerson(random).id;
        } else { // pinf
            boolean mode = random.nextBoolean();
            boolean fake = true;
            if (mode) {
                fake = random.nextBoolean();
            }
            id1 = randomFakeId(random, fake);
            id2 = randomFakeId(random, fake);
        }

        String ty = (type == 0) ? "normal" : "pinf";

        OUTPUTS.add(String.format("qc %d %d\n", id1, id2));
        debugF(DEBUG, String.format("queryCircle type is: %s id1= %d id2 = %d", ty, id1, id2));
    }

    /*
    * query_block_sum
    * */
    public static void queryBlockSum() {
        OUTPUTS.add("qbs\n");
        debugF(DEBUG, String.format("qbs person Count= %d", PERSONPOOL.size()));
    }

    /*
     * query_triple_sum
     * */
    public static void queryTriSum() {
        OUTPUTS.add("qts\n");
        debugF(DEBUG, String.format("qts person Count= %d", PERSONPOOL.size()));
    }

    /*
     * add_group id(int)
     *
     * exception:
     * egi
     */
    public static void addGroup(Random random) {
        int type = random.nextInt(4);
        int groupId;
        if (type == 0 && !GROUPPOOL.isEmpty()) {
            groupId = randomGroup(random).groupId;
        } else {
            groupId = GROUPID++;
            GROUPPOOL.put(groupId, new Group(groupId));
        }
        String ty = (type == 0) ? "egi" : "normal";
        OUTPUTS.add(String.format("ag %d\n", groupId));
        debugF(DEBUG, String.format("addGroup id= %d type= %s", groupId, ty));
    }

    /*
     * add_to_group id(int) id(int)
     *
     * exception:
     * pinf
     * ginf
     * epi
     */
    public static void addToGroup(Random random) {
        int type = random.nextInt(9);
        int personId, groupId;
        String ty;
        if (type == 0) {        // pinf
            personId = randomFakeId(random, true);
            groupId = randomGroup(random).groupId;
            ty = "pinf";
        } else if (type == 1) { //ginf
            personId = randomFakeId(random, false);
            groupId = randomFakeGroupId(random, true);
            ty = "ginf";
        } else if (type == 2) { // epi
            while (GROUPPOOL.isEmpty()) {
                addGroup(random);
            }
            Group group = randomGroup(random);
            groupId = group.groupId;
            if (group.people.isEmpty()) {
                groupId = randomGroup(random).groupId;
                personId = randomPerson(random).id;
                ty = "normal";
            } else {
                Integer[] keys = group.people.keySet().toArray(new Integer[0]);
                personId = keys[random.nextInt(keys.length)];
                ty = "epi";
            }
        } else {
            groupId = randomGroup(random).groupId;
            personId = randomPerson(random).id;
            ty = "normal";
        }
        OUTPUTS.add(String.format("atg %d %d\n", personId, groupId));
        debugF(DEBUG, String.format("addToGroup pid= %d gid= %d type= %s", personId, groupId, ty));
    }

    /*
     * del_from_group id(int) id(int)
     *
     * exception:
     * pinf
     * ginf
     */
    public static void delFromGroup(Random random) {
        int groupId, personId;
        int type = random.nextInt(3);
        String ty;
        if (type == 0) {        // pinf
            groupId = randomGroup(random).groupId;
            personId = randomFakeId(random, random.nextBoolean());
            ty = "pinf";
        } else if (type == 1) { // ginf
            groupId = randomFakeGroupId(random, true);
            personId = randomPerson(random).id;
            ty = "ginf";
        } else {
            Group group = randomGroup(random);
            groupId = group.groupId;
            if (group.people.isEmpty()) {
                addToGroup(random);
                return;
            }
            Integer[] keys = group.people.keySet().toArray(new Integer[0]);
            personId = keys[random.nextInt(keys.length)];
            ty = "normal";
        }
        OUTPUTS.add(String.format("dfg %d %d\n", personId, groupId));
        debugF(DEBUG, String.format("delFromGroup pid= %d gid= %d type= %s", personId, groupId, ty));
    }

    /*
     * query_group_value_sum id(int)
     *
     * exception:
     * ginf
     */
    public static void queryGroupValueSum(Random random) {
        boolean type = random.nextBoolean();
        int groupId = randomFakeGroupId(random, type);
        OUTPUTS.add(String.format("qgvs %d\n", groupId));
        debugF(DEBUG, String.format("queryGroupValueSum gid= %d type= %s", groupId, (type) ? "ginf" : "normal"));
    }

    public static void queryAgeVar(Random random) {
        boolean type = random.nextBoolean();
        int groupId = randomFakeGroupId(random, type);
        OUTPUTS.add(String.format("qgav %d\n", groupId));
        debugF(DEBUG, String.format("queryGroupAgeVar gid= %d type= %s", groupId, (type) ? "ginf" : "normal"));
    }

    public static void queryBestAcquaintance(Random random) {
        int type = random.nextInt(4);
        int personId;
        String ty;
        if (type == 0) {
            personId = randomPerson(random).id;
            ty = "pinf";
        } else {
            personId = randomFakeId(random, true);
            ty = "normal";
        }
        OUTPUTS.add(String.format("qba %d\n", personId));
        debugF(DEBUG, String.format("queryBestAcquaintance pid= %d type=%s", personId, ty));
    }

    public static void queryCoupleSum() {
        OUTPUTS.add("qcs\n");
        debugF(DEBUG, "qcs");
    }

    public static void modifyRelation(Random random) {
        int value;
        Integer[] keys = PERSONPOOL.keySet().toArray(new Integer[0]);
        int type = random.nextInt(4);
        int id1, id2;
        String ty;
        if (type == 0) {
            id1 = randomFakeId(random, random.nextBoolean());
            id2 = randomFakeId(random, random.nextBoolean());
            value = random.nextInt(100) - 200;
            ty = "pinf";
        } else if (type == 1) {
            Arc arc = randomArc(random);
            id1 = arc.p1Id;
            id2 = arc.p2Id;
            int oldValue = arc.value;
            value = random.nextInt(oldValue);
            arc.value += value;
            ty = "normal_add";
        } else {
            Arc arc = randomArc(random);
            id1 = arc.p1Id;
            id2 = arc.p2Id;
            int oldValue = arc.value;
            value = - (oldValue + 1);
            ARCPOOL.remove(arc);
            ty = "normal_del";
        }

        OUTPUTS.add(String.format("mr %d %d %d\n", id1, id2, value));
        debugF(DEBUG, String.format("modifyRelation p1id= %d p2id= %d type= %s", id1, id2, ty));
    }

    public static void querySocialValue(Random random) {
        int type = random.nextInt(5);
        int personId;
        String ty;
        if (type == 0) {
            personId = randomFakeId(random, true);
            ty = "pinf";
        } else {
            personId = randomPerson(random).id;
            ty = "normal";
        }
        OUTPUTS.add(String.format("qsv %d\n", personId));
        debugF(DEBUG, String.format("querySocialValue pid= %d type= %s", personId, ty));
    }

    public static void addMessage(Random random) {
        int type = random.nextInt(4);
        String ty;
        int p1id;
        int target;
        int messageType = 0;
        if (type == 0 && !MESSAGEPOOL.isEmpty()) {
            Message message = randomMessage(random);
            p1id = message.person1Id;
            messageType = message.type;
            target = message.target;
            ty = "emi";
        } else if (type == 1) {
            p1id = randomFakeId(random, true);
            target = random.nextInt();
            messageType = random.nextInt(2);
            ty = "pinf";
        } else {
            p1id = randomPerson(random).id;
            messageType = random.nextInt(2);
            if (messageType == 0) {
                target = randomPerson(random).id;
                ty = "normal_p2p";
            } else {
                target = randomGroup(random).groupId;
                ty = "normal_p2g";
            }
        }
        int mid = MESSAGEID++;
        if (type > 1) {
            MESSAGEPOOL.put(mid, new Message(mid, p1id, messageType, target));
        }
        int socialValue = random.nextInt(100);

        OUTPUTS.add(String.format("am %d %d %d %d %d\n", mid, socialValue, messageType, p1id, target));
        debugF(DEBUG, String.format("addMessage mid= %d type= %s", mid, ty));
    }

    public static void sendMessage(Random random) {
        int type = random.nextInt(3);
        int messageId;
        String ty;
        if (type == 0) {
            messageId = random.nextInt();
            ty = "minf";
        } else {
            messageId = randomMessage(random).messageId;
            if (MESSAGEPOOL.get(messageId).sent) {
                addMessage(random);
            }
            while (MESSAGEPOOL.get(messageId).sent) {
                messageId = randomMessage(random).messageId;
            }
            MESSAGEPOOL.get(messageId).sent = true;
            ty = "normal";
        }
        OUTPUTS.add(String.format("sm %d\n", messageId));
        debugF(DEBUG, String.format("sendMessage mid= %d type= %s", messageId, ty));
    }

    public static void modifyRelationOkTest(Random random) throws MyEqualRelationException, EqualPersonIdException {
        PERSONPOOL.clear();
        NAMEPOOL.clear();
        int type = random.nextInt(4);
        int number = random.nextInt(11);
        int lines = random.nextInt((number * number > 0) ? (number * number) : 1);

        for (int i = 0; i < number; i++) {
            addPerson(random);
        }
        for (int i = 0; i < lines; i++) {
            addRelation(random);
        }

        HashMap<Integer, HashMap<Integer, Integer>> beforeData = generateBeforeData();

        MyNetwork testNet = new MyNetwork();
        initCauses();
        OUTPUTS.clear();

        testNet.generateNetWork(beforeData);
        int result = testNet.queryTripleSum();
        String ty;
        HashMap<Integer, HashMap<Integer, Integer>> afterData = testNet.traverse2Map();

        int trick1 = 0;
        int trick2 = 0;
        if (type == 0) { // correct
            ty = "correct";
        } else if (type <= 2) { //result is wrong
            result += (random.nextInt(10) - 4);
            ty = "result wrong";
        } else { // not "pure"
            trick1 = 1;
            ty = "pure wrong";
        }

        String resultString = map2string(beforeData, trick2) + " " + map2string(afterData, trick1) + " " + result;

        MROKOUTS.add(String.format("qtsok %s\n", resultString));
        debugF(DEBUG, String.format("qtsok type=%s Count= %d", ty, PERSONPOOL.size()));
    }

    public static HashMap<Integer, HashMap<Integer, Integer>> generateBeforeData() {
        HashMap<Integer, HashMap<Integer, Integer>> returnMap = new HashMap<>();

        for (Person p : PERSONPOOL.values()) {
            HashMap<Integer, Integer> pMap = new HashMap<>();
            for (int acqs : p.acq.keySet()) {
                pMap.put(acqs, p.acq.get(acqs));
            }
            returnMap.put(p.id, pMap);
        }

        return returnMap;
    }

    public static String map2string(HashMap<Integer, HashMap<Integer, Integer>> dataMap, int trick) {
        StringBuilder sb = new StringBuilder();
        boolean used = false;
        sb.append(dataMap.size()).append(" ");
        for (int id : dataMap.keySet()) {
            sb.append(id).append(" ");
        }
        for (int id : dataMap.keySet()) {
            sb.append(dataMap.get(id).size()).append(" ");
            for (int acqId : dataMap.get(id).keySet()) {
                sb.append(acqId).append(" ");
                if (!used && trick == 1) {
                    sb.append(dataMap.get(id).get(acqId) + 1).append(" ");
                } else {
                    sb.append(dataMap.get(id).get(acqId)).append(" ");
                }
            }
        }

        return sb.substring(0, sb.toString().length() - 1);
    }
    public static void printout() {
        if (!MROKTEST) {
            for (String s : OUTPUTS) {
                System.out.print(s);
            }
        } else {
            for (String s : OUTPUTS) {
                System.out.print(s);
            }
        }
    }

    public static void debugF(boolean isTest, String word) {
        if (isTest) {
            System.out.println("Debugf: " + word);
        }
    }

    public static Arc randomArc(Random random) {
        Arc[] arcs = ARCPOOL.keySet().toArray(new Arc[0]);
        return arcs[random.nextInt(arcs.length)];
    }

    public static Person randomPerson(Random random) {
        Integer[] keys = PERSONPOOL.keySet().toArray(new Integer[0]);
        return PERSONPOOL.get(keys[random.nextInt(keys.length)]);
    }

    public static Group randomGroup(Random random) {
        Integer[] keys = GROUPPOOL.keySet().toArray(new Integer[0]);
        return GROUPPOOL.get(keys[random.nextInt(keys.length)]);
    }

    public static Message randomMessage(Random random) {
        Integer[] keys = MESSAGEPOOL.keySet().toArray(new Integer[0]);
        return MESSAGEPOOL.get(keys[random.nextInt(keys.length)]);
    }

    public static int randomFakeId(Random random, boolean fake) {
        if (fake) {
            return FAKEIDPOOL.get(random.nextInt(FAKEIDPOOL.size()));
        } else {
            return randomPerson(random).id;
        }
    }

    public static int randomFakeGroupId(Random random, boolean fake) {
        if (fake) {
            return FAKEGROUPPOOL.get(random.nextInt(FAKEGROUPPOOL.size()));
        } else {
            return randomGroup(random).groupId;
        }
    }

    public static void randomIns(Random random) {
        int type = random.nextInt(17);
        switch (type) {
            case 0:
                addPerson(random);
                break;
            case 1:
                addRelation(random);
                break;
            case 2:
                queryValue(random);
                break;
            case 3:
                queryCircle(random);
                break;
            case 4:
                queryBlockSum();
                break;
            case 5:
                queryTriSum();
                break;
            case 6:
                addGroup(random);
                break;
            case 7:
                addToGroup(random);
                break;
            case 8:
                delFromGroup(random);
                break;
            case 9:
                queryGroupValueSum(random);
                break;
            case 10:
                queryAgeVar(random);
                break;
            case 11:
                queryBestAcquaintance(random);
                break;
            case 12:
                queryCoupleSum();
                break;
            case 13:
                modifyRelation(random);
                break;
            case 14:
                querySocialValue(random);
                break;
            case 15:
                addMessage(random);
                break;
            case 16:
                sendMessage(random);
                break;
        }
    }
}