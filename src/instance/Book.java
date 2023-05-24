package instance;

import java.util.Objects;

public class Book {
    private final int type;
    private final String name;

    public Book(String name) {
        this.name = name;
        this.type = (name.charAt(0) == 'A') ? 0 :
                    (name.charAt(0) == 'B') ? 1 :
                    2;
    }

    public int getType() { return type; }

    public String getName() { return name; }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Book book = (Book) o;
        return name.equals(book.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
