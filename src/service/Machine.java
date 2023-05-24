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

    public static void returnTypeC(HashMap<Book, Integer> rentFailedPool,
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
}
