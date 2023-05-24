package instance;

import java.util.HashMap;

public class Student {
    private final String name;
    private boolean hasTypeB;
    private final HashMap<Book, Integer> bags = new HashMap<>();

    public Student(String name) {
        this.name = name;
        this.hasTypeB = false;
    }

    public String getName() { return name; }

    public boolean isHasTypeB() { return hasTypeB; }

    public boolean hasBookC(Book book) { return bags.containsKey(book); }

    public void rentBook(Book book) { bags.put(book, 1); }
}
