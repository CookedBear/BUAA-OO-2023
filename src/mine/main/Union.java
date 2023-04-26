package mine.main;

import com.oocourse.spec2.main.Person;

import java.util.HashMap;

public class Union {
    private final HashMap<Integer, Integer> relation = new HashMap<>();
    private int[] visited;

    public void union(int p, int q) { // reNewRelation
        int proot = find(p);
        int qroot = find(q);
        relation.put(Math.min(proot, qroot),
                     Math.max(proot, qroot));
    }

    public int find(int pid) {     // getRelation + reNew
        int father = relation.get(pid);
        if (pid != father) {
            relation.put(pid, find(father));
        }
        return relation.get(pid);
    }

    public void addUnion(int pid1, int pid2) {  relation.put(pid1, pid2); }

    public void setVisited(int size) { visited = new int[size]; }

    public void rebuildPart(int nowId, int targetId, HashMap<Integer, Person> people) {
        relation.put(nowId, targetId);
        visited[nowId - 1] = 1;
        for (int midId : ((MyPerson) people.get(nowId)).getAcquaintance().keySet()) {
            if (visited[midId - 1] != 1) {
                rebuildPart(midId, targetId, people);
            }
        }
    }
}