package service;

import instance.Book;
import instance.Student;
import tool.DateCal;
import tool.PrintAction;

import java.util.HashMap;

public class Purchase {
    private static final String NAME = "purchasing department";

    public static void rent(Student student, Book book, int date) {
        student.rentBook(book, DateCal.getDateOutput(date));
        PrintAction.rented(date, student, book, NAME);
    }

    public static void buyIn(Book book, int num, HashMap<Book, Integer> pool,
                             HashMap<Book, Integer> count, String schoolName, int date) {
        Book newBook = new Book(book.getName(), schoolName, true);
        if (!pool.containsKey(newBook)) {
            pool.put(newBook, num);
        } else {
            pool.put(newBook, pool.get(newBook) + num);
        }
        if (!count.containsKey(newBook)) {
            count.put(newBook, num);
        } else {
            count.put(newBook, count.get(newBook) + num);
        }
        PrintAction.bought(DateCal.getDateOutput(date), newBook, NAME);
    }
}
