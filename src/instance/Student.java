package instance;

import service.Rent;

import java.util.HashMap;

public class Student {
    private final String name;
    private boolean hasTypeB;
    private final HashMap<Book, Integer> bookState = new HashMap<>();

    public Student(String name) {
        this.name = name;
        this.hasTypeB = false;
    }

    public String getName() { return name; }

    public boolean isHasTypeB() { return hasTypeB; }

    public boolean hasBookC(Book book) { return bookState.containsKey(book); }

    public void rentBook(Book book) {
        bookState.put(book, 0);
        if (book.getType() == 1) {
            hasTypeB = true;
        }
    }

    public int returnBook(Book book) {
        int state = bookState.get(book);
        bookState.remove(book);
        if (book.getType() == 1) { hasTypeB = false; }
        return state;
    }

    public void smashBook(Book book) { bookState.put(book, 1); }

    public void lostBook(Book book, String dateOutput) {
        bookState.remove(book);
        if (book.getType() == 1) { hasTypeB = false; }
        Rent.lostBook(this, book, dateOutput);
    }
}
