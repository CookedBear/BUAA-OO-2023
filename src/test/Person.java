package test;

import java.util.HashMap;

public class Person {
    public int id;
    public String name;
    public int age;
    public HashMap<Integer, Integer> acq = new HashMap<>();

    public Person(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

}
