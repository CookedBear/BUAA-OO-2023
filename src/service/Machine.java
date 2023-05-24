package service;

import instance.Book;
import instance.Student;
import tool.PrintAction;

import java.util.HashMap;

public class Machine {
    private static final String NAME = "self-service machine";

    public static void queryBook(String dateOutput, Student student, Book book) {
        PrintAction.queried(dateOutput, student, book);
    }

    public static void rentTypeC(HashMap<Book, Integer> rentFailedPool,
                                 Student student, Book book, String dateOutput) {
        if (student.hasBookC(book)) {
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
