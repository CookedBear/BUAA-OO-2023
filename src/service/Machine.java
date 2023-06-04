package service;

import instance.Book;
import instance.Request;
import instance.Student;
import tool.DateCal;
import tool.PrintAction;

import java.util.ArrayList;
import java.util.HashMap;

public class Machine {
    private static final String NAME = "self-service machine";

    public static void queryBook(int date, Student student, Book book) {
        PrintAction.queried(date, student, book);
    }

    public static void rentTypeC(HashMap<Book, Integer> rentFailedPool,
                                 Student student, Book book, int date,
                                 ArrayList<Request> reserveList,
                                 ArrayList<Request> buyList) {
        if (student.hasBookC(book)) {
            PrintAction.failed(DateCal.getDateOutput(date), student, book, NAME);
            if (rentFailedPool.containsKey(book)) {
                rentFailedPool.put(book, rentFailedPool.get(book) + 1);
            } else {
                rentFailedPool.put(book, 1);
            }
        } else {
            student.rentBook(book, DateCal.getDateOutput(date));
            Arrange.flushWith(student, book, reserveList);
            Arrange.flushWith(student, book, buyList);
            PrintAction.rented(date, student, book, NAME);
        }
    }

    public static void returnTypeC(HashMap<Book, Integer> rentFailedPool,
                                   Student student, Book book, String dateOutput) {
        int state = student.returnBook(book, dateOutput);
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
            PrintAction.punished(dateOutput, student, "borrowing and returning librarian");
            PrintAction.returned(dateOutput, student, book, NAME);
            Back.repair(dateOutput, book, student.getSchool());
        } else if (state == 2) {
            PrintAction.punished(dateOutput, student, "borrowing and returning librarian");
            PrintAction.returned(dateOutput, student, book, NAME);
        }
    }
}
