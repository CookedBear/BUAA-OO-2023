package mine.tool;

import com.oocourse.spec3.main.Person;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Kruskal {

    public static HashMap<Arc, Integer> makeKruskal(HashMap<Integer, Person> people,
                                                    HashMap<Arc, Integer> arcPools) {
        ArrayList<Arc> sortedArc = new ArrayList<>(arcPools.keySet());
        HashMap<Arc, Integer> usedArc = new HashMap<>();
        Union unionMap = new Union();
        Collections.sort(sortedArc);

        for (Person person : people.values()) {
            unionMap.addUnion(person.getId(), person.getId());
        }
        for (Arc arc : sortedArc) {
            int p1Id = arc.getPerson1();
            int p2Id = arc.getPerson2();
            if (unionMap.find(p1Id) != unionMap.find(p2Id)) {
                usedArc.put(arc, 1);
                unionMap.union(Math.min(p1Id, p2Id), Math.max(p1Id, p2Id));
            }
        }
        return usedArc;
    }
}
