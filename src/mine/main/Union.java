package mine.main;

import java.util.HashMap;

public class Union {
    private final HashMap<Integer, Integer> relation = new HashMap<>();

    public void union(int p, int q) { // reNewRelation
        int proot = find(p);
        int qroot = find(q);
        relation.put(proot, qroot);
    }

    public int find(int pid) {     // getRelation + reNew
        int father = relation.get(pid);
        if (pid != father) {
            relation.put(pid, find(father));
        }
        return relation.get(pid);
    }

    public void addUnion(int pid1, int pid2) {  relation.put(pid1, pid2); }
}