package instance;

import service.Rent;
import tool.PrintAction;

import java.util.HashMap;

public class Student {
    private final String name;
    private final String school;
    private boolean hasTypeB;
    private final HashMap<Book, Integer> bookState = new HashMap<>();

    public Student(String name, String school) {
        this.name = name;
        this.hasTypeB = false;
        this.school = school;
    }

    public String getName() { return name; }

    public String getSchool() { return school; }

    public boolean isHasTypeB() { return hasTypeB; }

    public boolean hasBookC(Book book) { return bookState.containsKey(book); }

    public void rentBook(Book book, String dateOutput) {
        bookState.put(book, 0);
        if (book.getType() == 1) {
            hasTypeB = true;
        }
        PrintAction.stateTrans(dateOutput, book, "stored", "rented");
    }

    public int returnBook(Book book, String dateOutput) {
        int state = bookState.get(book);
        bookState.remove(book);
        if (book.getType() == 1) { hasTypeB = false; }
        if (state == 0) {
            PrintAction.stateTrans(dateOutput, book, "rented", "stored");
        } else if (state == 1) {
            PrintAction.stateTrans(dateOutput, book, "smashed", "stored");
        }
        return state;
    }

    public void smashBook(Book book, String dateOutput) {
        Book newBook = getBook(book);
        bookState.put(newBook, 1); // 防止覆盖书籍的学校信息
        PrintAction.stateTrans(dateOutput, book, "rented", "smashed");
    }

    public void lostBook(Book book, String dateOutput) {
        bookState.remove(book);
        if (book.getType() == 1) { hasTypeB = false; }
        Rent.lostBook(this, dateOutput);
        PrintAction.stateTrans(dateOutput, book, "rented", "lost");
    }

    public Book getBook(Book book) {
        Book[] books = bookState.keySet().toArray(new Book[0]);
        for (Book book1 : books) {
            if (book1.getName().equals(book.getName())) {
                return book1;
            }
        }
        System.out.println("Bad Book in 'getBook'!");
        return new Book("BadBook");
    }
}
