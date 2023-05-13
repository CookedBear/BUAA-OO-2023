package mine.tool;

import com.oocourse.spec3.main.Person;
import mine.main.MyPerson;

import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Queue;

public class Dijkstra {
    public static int makeDijkstra(int from, HashMap<Integer, Person> people,
                                   HashMap<Arc, Integer> arcPools,
                                   HashMap<Integer, HashMap<Edge, Integer>> modifiedPools) {
        //        int[] vis = new int[maxn];
        //        dis = new int[maxn];
        //        for(int i = 0 ; i < maxn ; i++) dis[i] = 2147483647;
        //        Queue<Edge> que= new PriorityQueue<>();
        //        que.add(new Edge(s, 0));
        //        dis[s] = 0;
        //        while(!que.isEmpty()) {
        //            Edge now = que.poll();
        //            int u = now.getId();
        //            if(dis[u] < now.getDistance())continue;
        //            if(vis[u] == 1)continue;
        //            vis[u] = 1;
        //            for(int i = 0; i < e.get(u).size() ; i++) {
        //                int next = e.get(u).get(i).getId();
        //                int cost = e.get(u).get(i).getDistance();
        //                if(vis[next] == 0 && dis[next] > dis[u] + cost) {
        //                    dis[next] = dis[u] + cost;
        //                    que.add(new Edge(next,dis[next]));
        //                }
        //            }
        //        }
        HashMap<Integer, Integer> dist = new HashMap<>();
        Queue<Edge> queue = new PriorityQueue<>();
        HashMap<Integer, Integer> visited = new HashMap<>();
        HashMap<Arc, Integer> tempArcPools = new HashMap<>();

        dist.put(from, 0);
        queue.add(new Edge(from, 0, from));

        while (!queue.isEmpty()) {
            Edge now = queue.poll();
            int target = now.getId();
            int fro = now.getFrom();
            if (visited.containsKey(target) ||
                dist.getOrDefault(target, 2147483647) < now.getDistance()) {
                continue; }
            visited.put(target, 0);
            if (fro != target) {
                Arc tempArc = new Arc(fro, target, 0);
                int updValue = arcPools.get(tempArc);
                tempArc.updValue(updValue);
                tempArcPools.put(tempArc, updValue); }
            for (Edge edge : modifiedPools.get(target).keySet()) {
                int next = edge.getId();
                int nextValue = edge.getDistance();
                if (!visited.containsKey(next) &&
                    dist.getOrDefault(next, 2147483647) > nextValue + dist.get(target)) {
                    dist.put(next, nextValue + dist.get(target));
                    queue.add(new Edge(next, dist.get(next), target)); } } }
        HashMap<Arc, Integer> usedArc = Kruskal.makeKruskal(people, tempArcPools);
        HashMap<Integer, Integer> roots = new HashMap<>();
        dfsForRoot(from, roots, people, usedArc);
        int node = 0;
        int top = 2147483647;
        for (Arc arc : arcPools.keySet()) {
            if (!usedArc.containsKey(arc)) {
                int p1 = arc.getPerson1();
                int p2 = arc.getPerson2();
                if (p1 != from && p2 != from) {
                    if (!roots.containsKey(p1) || !roots.containsKey(p2)) {
                        continue;
                    }
                    if (!roots.get(p1).equals(roots.get(p2))) {
                        int temp = dist.get(p1) + dist.get(p2) + arcPools.get(arc);
                        top = Math.min(temp, top);
                    }
                } else {
                    Arc tempArc = new Arc(p1, p2, 1);
                    if (!usedArc.containsKey(tempArc)) {
                        p1 = (p2 == from) ? p1 : p2;
                        int temp = dist.get(p1) + arcPools.get(tempArc);
                        top = Math.min(temp, top);
                        node = p1;
                    }
                }
            }
        }
        node = node * 1;
        return (top == 2147483647) ? -1 : top;
    }

    private static void dfsForRoot(int rootId, HashMap<Integer, Integer> roots,
                                   HashMap<Integer, Person> people,
                                   HashMap<Arc, Integer> usedArc) {
        HashMap<Integer, Boolean> visited = new HashMap<>();
        visited.put(rootId, true);
        for (int acqId : ((MyPerson) people.get(rootId)).getAcquaintance().keySet()) {
            if (usedArc.containsKey(new Arc(rootId, acqId, 1))) {
                dfsRoot(acqId, rootId, acqId, visited, roots, people, usedArc);
            }
        }
    }

    private static void dfsRoot(int pid, int sender, int target, HashMap<Integer, Boolean> visited,
                                HashMap<Integer, Integer> roots, HashMap<Integer, Person> people,
                                HashMap<Arc, Integer> usedArc) {
        visited.put(pid, true);
        roots.put(pid, target);
        for (int acqId : people.keySet()) {
            if (!visited.containsKey(acqId) && usedArc.containsKey(new Arc(acqId, pid, 1))) {
                dfsRoot(acqId, pid, target, visited, roots, people, usedArc);
            }
        }
    }
}
  