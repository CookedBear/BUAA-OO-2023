package service;

import instance.Book;
import instance.Student;
import tool.PrintAction;

import java.util.HashMap;

public class Rent {
    private static final String NAME = "borrowing and returning librarian";

    public static void rentTypeB(HashMap<Book, Integer> rentFailedPool,
                                 Student student, Book book, String dateOutput) {
        if (student.isHasTypeB()) {
            if (rentFailedPool.containsKey(book)) {
                rentFailedPool.put(book, rentFailedPool.get(book) + 1);
            } else {
                rentFailedPool.put(book, 1);
            }
        } else {
            student.rentBook(book);
            PrintAction.rented(dateOutput, student, book, NAME);
        }
    }
}
