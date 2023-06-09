package service;

import instance.Book;
import instance.Request;
import instance.Student;

import java.util.ArrayList;

public class Arrange {
    private static final String NAME = "arranging librarian";

    public static void flushWith(Student student, Book book, ArrayList<Request> target) {
        ArrayList<Request> removedList = new ArrayList<>();
        for (Request request : target) {
            if (request.getStudent().equals(student) &&
                ((book.getType() == 1 && request.getBook().getType() == 1) ||
                (book.getType() == 2 && book.getName().equals(request.getBook().getName()))) &&
                !removedList.contains(request)) {
                removedList.add(request);
            }
        }
        for (Request request : removedList) {
            target.remove(request);
        }
    }
}
