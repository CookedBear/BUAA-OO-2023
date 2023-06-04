package service;

import instance.Book;
import instance.Request;
import instance.Student;
import tool.DateCal;
import tool.PrintAction;

import java.util.ArrayList;
import java.util.HashMap;

public class Rent {
    private static final String NAME = "borrowing and returning librarian";

    public static void rentTypeB(HashMap<Book, Integer> rentFailed, Student student,
                                 Book book, int date, ArrayList<Request> reserveList,
                                 ArrayList<Request> buyList) {
        if (student.isHasTypeB()) {
            PrintAction.failed(DateCal.getDateOutput(date), student, book, NAME);
            if (rentFailed.containsKey(book)) {
                rentFailed.put(book, rentFailed.get(book) + 1);
            } else {
                rentFailed.put(book, 1);
            }
        } else {
            student.rentBook(book);
            Arrange.flushWith(student, book, reserveList);
            Arrange.flushWith(student, book, buyList);
            PrintAction.rented(date, student, book, NAME);
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
            Back.repair(dateOutput, book, student.getSchool());
        } else if (state == 2) {
            PrintAction.punished(dateOutput, student, NAME);
            PrintAction.returned(dateOutput, student, book, NAME);
        }
    }

    public static void lostBook(Student student, String dateOutput) {
        PrintAction.punished(dateOutput, student, NAME);
    }
}
