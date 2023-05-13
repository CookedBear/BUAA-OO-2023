package test;

import java.util.HashMap;

public class Group {
    public int groupId;
    public HashMap<Integer, Person> people = new HashMap<>();

    public Group(int groupId) {
        this.groupId = groupId;
    }
}
