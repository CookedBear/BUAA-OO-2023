package test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import mine.exceptions.MyEqualRelationException;
import mine.main.MyNetwork;
import spec1.exceptions.EqualPersonIdException;

import static mine.exceptions.ExceptionCounter.initCauses;

public class Main {

    public static boolean DEBUG = false;
    public static int INS_NUM;
    public static boolean QTSOK_ABLE = false;
    public static boolean QTSOKTEST = false;

    public static int PERSONID = 1;
    public static ArrayList<String> NAMEPOOL = new ArrayList<>();
    public static HashMap<Integer, Person> PERSONPOOL = new HashMap<>();
    public static ArrayList<Integer> FAKEIDPOOL = new ArrayList<>();
    public static HashMap<Arc, Integer> ARCPOOL = new HashMap<>();
    public static ArrayList<String> OUTPUTS = new ArrayList<>();
    public static ArrayList<String> QTSOKOUTS = new ArrayList<>();

    public static void main(String[] args) throws MyEqualRelationException, EqualPersonIdException {
        Random random = new Random();
        modeSelect(args, random);

        generateCode(random);

        printout();
    }

    public static void modeSelect(String[] args, Random random) {
        if (args.length > 0) {
            switch (args[0]) {
                case "-strong":
                    QTSOK_ABLE = true;
                    if (random.nextInt(3) == 0) {
                        QTSOKTEST = true;
                    }
                    if (!QTSOKTEST) {
                        INS_NUM = (random.nextInt(499) + 1) * 20;
                    } else {
                        INS_NUM = random.nextInt(10) * 20 + 8;
                    }
                    break;
                case "-mutual":
                    INS_NUM = (random.nextInt(199) + 1) * 5;
                    QTSOK_ABLE = false;
                    break;
                case "-mutual_manual":
                    INS_NUM = Integer.parseInt(args[1]);
                    QTSOK_ABLE = false;
                    break;
                case "-qtsoktest":
                    QTSOK_ABLE = true;
                    QTSOKTEST = true;
                    INS_NUM = random.nextInt(50) + 5;
                    break;
                case "-manual":
                    if (args.length < 2) {
                        System.out.println("Wrong parameter type!");
                        System.exit(-1);
                    }
                    System.out.printf("Now mode: manual\nINS_NUM = %d\nQTSOK = %b\n", Integer.parseInt(args[1]), QTSOKTEST);
                    INS_NUM = Integer.parseInt(args[1]);
                    QTSOK_ABLE = true;
                    if (random.nextInt(3) == 0) {
                        QTSOKTEST = true;
                    }
                    break;
                case "-normal":
                default:
                    QTSOK_ABLE = true;
                    if (random.nextInt(3) == 0) {
                        QTSOKTEST = true;
                    }
//                    if (QTSOK_ABLE) {
//                        QTSOKTEST = random.nextBoolean();
//                    }
                    if (!QTSOKTEST) {
                        INS_NUM = random.nextInt(200) + 30;
                    } else {
                        INS_NUM = random.nextInt(5) + 5;
                    }
            }
        } else {
            QTSOK_ABLE = true;
            QTSOKTEST = random.nextBoolean();
            if (!QTSOKTEST) {
                INS_NUM = random.nextInt(50) + 30;
            } else {
                INS_NUM = random.nextInt(5) + 5;
            }
        }
        for (int i = 0; i < INS_NUM / 30 + 1; i++) {
            int id = random.nextInt(10000) - 20000;
            while (FAKEIDPOOL.contains(id)) {
                id = random.nextInt(10000) - 20000;
            }
            FAKEIDPOOL.add(id);
        }
    }

    public static void generateCode(Random random) throws MyEqualRelationException, EqualPersonIdException {
        if (!QTSOKTEST) {
            for (int i = 0; i < INS_NUM; i++) {
                if (i <= INS_NUM / 6) {
                    addPerson(random);
                } else if (i <= INS_NUM / 3 * 2) {
                    addRelation(random);
                } else {
                    randomIns(random);
                }
            }
        } else {
            for (int i = 0; i < INS_NUM; i++) {
                queryTriSumOkTest(random);
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
        debugf(DEBUG, String.format("addPerson id= %d name= %s age= %d", PERSONID - 1, name, age));
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

        ARCPOOL.put(new Arc(Math.min(id1, id2), Math.max(id1, id2)), 1);
        OUTPUTS.add(String.format("ar %d %d %d\n", id1, id2, value));
        debugf(DEBUG, String.format("addRelation type=%s id1= %d id2= %d value= %d", ty, id1, id2, value));

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
        debugf(DEBUG, String.format("queryValue type is: %s id1= %d id2 = %d", ty, id1, id2));
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
        debugf(DEBUG, String.format("queryCircle type is: %s id1= %d id2 = %d", ty, id1, id2));
    }

    /*
    * query_block_sum
    * */
    public static void queryBlockSum() {
        OUTPUTS.add("qbs\n");
        debugf(DEBUG, String.format("qbs person Count= %d", PERSONPOOL.size()));
    }

    /*
     * query_triple_sum
     * */
    public static void queryTriSum() {
        OUTPUTS.add("qts\n");
        debugf(DEBUG, String.format("qts person Count= %d", PERSONPOOL.size()));
    }

    public static void queryTriSumOkTest(Random random) throws MyEqualRelationException, EqualPersonIdException {
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

        QTSOKOUTS.add(String.format("qtsok %s\n", resultString));
        debugf(DEBUG, String.format("qtsok type=%s Count= %d", ty, PERSONPOOL.size()));
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
        if (!QTSOKTEST) {
            for (String s : OUTPUTS) {
                System.out.print(s);
            }
        } else {
            for (String s : QTSOKOUTS) {
                System.out.print(s);
            }
        }
    }

    public static void debugf(boolean isTest, String word) {
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

    public static int randomFakeId(Random random, boolean fake) {
        if (fake) {
            return FAKEIDPOOL.get(random.nextInt(FAKEIDPOOL.size()));
        } else {
            return randomPerson(random).id;
        }
    }

    public static void randomIns(Random random) {
        int type = random.nextInt(6);
        if (type == 0) {
            addPerson(random);
        } else if (type == 1) {
            addRelation(random);
        } else if (type == 2) {
            queryValue(random);
        } else if (type == 3) {
            queryCircle(random);
        } else if (type == 4) {
            queryBlockSum();
        } else {
            queryTriSum();
        }
    }
}