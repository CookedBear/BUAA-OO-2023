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
            Reserve.flushWith(student);
            PrintAction.rented(dateOutput, student, book, NAME);
        }
    }

    public static void returnTypeB(HashMap<Book, Integer> rentFailedPool,
                                   Student student, Book book, String dateOutput) {
        int state = student.returnBook(book);
        if (state != 2) {
            if (rentFailedPool.containsKey(book)) {
                rentFailedPool.put(book, rentFailedPool.get(book) + 1);
            } else {
                rentFailedPool.put(book, 1);
            }
        }

        if (state == 0) {
            PrintAction.returned(dateOutput, student, book, NAME);
        } else if (state == 1) {
            PrintAction.punished(dateOutput, student, NAME);
            PrintAction.returned(dateOutput, student, book, NAME);
            Back.repair(dateOutput, book);
        } else if (state == 2) {
            PrintAction.punished(dateOutput, student, NAME);
            PrintAction.returned(dateOutput, student, book, NAME);
        }
    }

    public static void lostBook(Student student, Book book, String dateOutput) {
        PrintAction.punished(dateOutput, student, NAME);
    }
}
