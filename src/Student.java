import java.util.HashMap;

public class Student {
    public String name;
    public HashMap<String, Book> bags = new HashMap<>();
    public boolean hasB;

    public Student(String s) {
        name = s;
        hasB = false;
    }


}
