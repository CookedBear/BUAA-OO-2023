package instance;

import tool.DateCal;

import java.util.Objects;

public class Request {
    private final Student student;
    private final int date;
    private final String dateOutput;
    private final int action;
    /*
     * borrow = 1
     * smeared = 2
     * lost = 3
     * return = 4
     */
    private final Book book;

    public Request(String date, Student student, String action, Book book) {
        this.student = student;
        this.date = DateCal.getDate(date);
        this.dateOutput = date;
        this.action = (action.equals("borrowed")) ? 1 :
                      (action.equals("smeared")) ? 2 :
                      (action.equals("lost")) ? 3 : 4;
        this.book = book;
    }

    public Request(String date, Student student, Book book) {
        this.student = student;
        this.date = DateCal.getDate(date);
        this.dateOutput = date;
        this.action = 0;
        this.book = book;
    }

    public int getDate() { return date; }

    public String getDateOutput() { return dateOutput; }

    public int getAction() { return action; }

    public Student getStudent() { return student; }

    public Book getBook() { return book; }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Request request = (Request) o;
        return action == request.action &&
                Objects.equals(student, request.student) &&
                Objects.equals(book, request.book);
    }

    @Override
    public int hashCode() { return Objects.hash(student, action, book); }
}
