package test;

public class Book {
    public int type;
    public String name;

    public Book(String s) {
        type = (s.charAt(0) == 'A') ? 1 : // A
               (s.charAt(0) == 'B') ? 2 : // B
               3;                         // C
        name = s;
    }
}
