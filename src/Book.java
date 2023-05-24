public class Book {
    private final int type;
    private String name;

    public Book(String name) {
        this.name = name;
        this.type = (name.charAt(0) == 'A') ? 0 :
                    (name.charAt(0) == 'B') ? 1 :
                    2;
    }
}
