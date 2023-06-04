package instance;

import java.util.Objects;

public class Book {
    private final int type;
    private final String name;
    private String school;
    private final boolean shared;

    public Book(String name) {
        this.name = name;
        this.type = (name.charAt(0) == 'A') ? 0 :
                    (name.charAt(0) == 'B') ? 1 :
                    2;
        shared = true;
        school = "BadSchool";
    }

    public Book(String name, String school, boolean shared) {
        this.name = name;
        this.type = (name.charAt(0) == 'A') ? 0 :
                (name.charAt(0) == 'B') ? 1 :
                        2;
        this.school = school;
        this.shared = shared;
    }

    public int getType() { return type; }

    public String getName() { return name; }

    public String getSchool() { return school; }

    public void setSchool(String s) { this.school = s; }

    public boolean isShared() { return shared; }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Book book = (Book) o;
        return name.equals(book.name);
    }

    @Override
    public int hashCode() { return Objects.hash(name); }
}
