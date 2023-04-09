import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

public class AlGraph {
    private static final int MVNUM = 170;
    private HashMap<Integer, ArrayList<Node>> nodeMap = new HashMap<>();
    private ArrayList<Node> nodeList = new ArrayList<>();
    private int vexNum;
    private int[] visited = new int[100];// 顶点访问标志数组，每次遍历时须重新初始化，数组中元素全部置为0
    private ArrayList<Integer> routes = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> ans = new ArrayList<>();

    // 构建图

    public void addNode(Integer elevatorId, Integer[] ableFloor) {
        nodeMap.put(elevatorId, new ArrayList<>());
        Node newNode = new Node(elevatorId, ableFloor);
        nodeList.add(newNode);
        for (Node nodes : nodeList) {
            Integer[] floor = nodes.getFloor();
            for (int j = 1; j <= 11; j++) {
                if (elevatorId.equals(nodes.getElevatorId())) {
                    continue;
                }
                if (floor[j] == 1 && Objects.equals(floor[j], ableFloor[j])) {
                    // System.out.println("create arc!");
                    addArc(newNode, nodes);
                    break;
                }
            }
        }
    }

    public void addArc(Node node1, Node node2) {
        ArrayList<Node> nodeList = this.nodeMap.get(node1.getElevatorId());
        nodeList.add(node2);
        nodeList = this.nodeMap.get(node2.getElevatorId());
        nodeList.add(node1);
    }

    // 输出邻接表
    public void showAL() {
        System.out.println("size = " + nodeMap.size());
        for (Integer node : nodeMap.keySet()) {
            // System.out.println(node.toString());
            System.out.println(node.toString() + ":" + nodeMap.get(node).toString());
        }
        System.out.println("AL fin");
    }

    public void startDfs(int start, int fin) {

        addNode(-1, getF(start));
        addNode(-2, getF(fin));
        // showAL();
        visited = new int[100];
        //OutputFormat.say("Start traverse");
        dfsTraverse(-1, -2);
        //OutputFormat.say("Finish traverse");
        // ans.clear();
        delNode(-1);
        delNode(-2);
    }

    // 深度优先遍历
    public void dfsTraverse(int start, int fin) {
        if (routes.size() >= 7 && ans.size() != 0) {
            return;
        }
        int startIdx = 0;
        for (int i = 0; i < nodeList.size(); i++) {
            if (nodeList.get(i).getElevatorId() == start) {
                startIdx = i;
                break;
            }
        }
        // System.out.println("Set node "+ start +" as " + startIdx + " visited!");
        visited[startIdx] = 1;
        routes.add(start);

        if (start == fin) {
            ans.add(getClone(routes));
            // System.out.println("Found routes!");
            // System.out.println(routes);
            // throw new StopMsgException();
        } else {
            for (Node node : nodeMap.get(start)) {
                if (visited[nodeList.indexOf(node)] != 1) {
                    dfsTraverse(node.getElevatorId(), fin);
                }
            }
        }
        visited[startIdx] = 0;
        // System.out.println("Set node " + startIdx + " UNvisited!");
        routes.remove(routes.size() - 1);
    }

    public ArrayList<Answer> printAns(int start, int fin) {
        // System.out.println("Ans routes are:");
        ArrayList<Answer> answers = new ArrayList<>();
        for (ArrayList<Integer> route : ans) {
            answers.add(new Answer(route.size() - 3, route));
        }
        ans.clear();

        for (Answer answer : answers) {
            int pause = -1;

            if (answer.getOverTimes() == 0) {
                pause = fin;
            } else {
                Integer[] floor1 = getNode(answer.getOverList().get(1)).getFloor();
                Integer[] floor2 = getNode(answer.getOverList().get(2)).getFloor();
                // OutputFormat.say("floor1 :" + Arrays.toString(floor1));
                // OutputFormat.say("floor2 :" + Arrays.toString(floor2));
                for (int i = start; i <= fin; i++) {
                    if (floor1[i] == 1 && Objects.equals(floor1[i], floor2[i])) {
                        pause = i;
                        break;
                    }
                }
                if (pause == -1) {   // no station
                    for (int i = 1; i <= 11; i++) {
                        if (floor1[i] == 1 && Objects.equals(floor1[i], floor2[i])) {
                            pause = i;
                        }
                    }
                }
            }
            answer.setGetDownStation(pause);

        }
        Collections.sort(answers);
        for (int i = answers.size() - 1; i >= 0; i--) {
            // System.out.printf("%d - %d\n", answers.get(0).getOverTimes(),
            // answers.get(i).getOverTimes());
            if (answers.get(i).getOverTimes() > answers.get(0).getOverTimes()) {
                answers.remove(i);
            }
        }
        return answers;
    }

    public ArrayList<Integer> getClone(ArrayList<Integer> routes) {
        return new ArrayList<>(routes);
    }

    public void delNode(int elevatorId) {
        for (int i = nodeList.size() - 1; i >= 0; i--) {
            if (nodeList.get(i).getElevatorId() == elevatorId) {
                nodeList.remove(i);
            }
        }
        nodeMap.remove(elevatorId);
        for (ArrayList<Node> nodes : nodeMap.values()) {
            for (int i = nodes.size() - 1; i >= 0; i--) {
                if (nodes.get(i).getElevatorId() == elevatorId) {
                    nodes.remove(i);
                }
            }
        }
    }

    public Node getNode(int elevatorId) {
        for (int i = nodeList.size() - 1; i >= 0; i--) {
            if (elevatorId == nodeList.get(i).getElevatorId()) {
                return nodeList.get(i);
            }
        }
        return null;
    }

    private Integer[] getF(int floor) {
        switch (floor) {
            case 1: return new Integer[] {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            case 2: return new Integer[] {0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            case 3: return new Integer[] {0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0};
            case 4: return new Integer[] {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0};
            case 5: return new Integer[] {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0};
            case 6: return new Integer[] {0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0};
            case 7: return new Integer[] {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0};
            case 8: return new Integer[] {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0};
            case 9: return new Integer[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0};
            case 10: return new Integer[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0};
            default: return new Integer[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1};
        }
    }

}