package mine.tool;

import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Queue;

public class Dijkstra {
    public static int makeDijkstra(int from, int to,
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

        dist.put(from, 0);
        queue.add(new Edge(from, 0));

        while (!queue.isEmpty()) {
            Edge now = queue.poll();
            int target = now.getId();
            if (visited.containsKey(target) ||
                dist.getOrDefault(target, 2147483647) < now.getDistance()) {
                continue;
            }
            visited.put(target, 0);
            for (Edge edge : modifiedPools.get(target).keySet()) {
                int next = edge.getId();
                int nextValue = edge.getDistance();
                if (!visited.containsKey(next) &&
                    dist.getOrDefault(next, 2147483647) > nextValue + dist.get(target)) {
                    dist.put(next, nextValue + dist.get(target));
                    queue.add(new Edge(next, dist.get(next)));
                }
            }
        }
        return dist.getOrDefault(to, -1);
    }
}
  