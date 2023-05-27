package service;

import instance.Book;
import instance.Request;
import instance.Student;
import tool.PrintAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Reserve {
    private static final String NAME = "ordering librarian";
    private static final HashMap<Book, LinkedList<Student>> RESERVE_BOOK_LIST = new HashMap<>();
    private static final LinkedList<Request> REQUEST_LIST = new LinkedList<>();
    private static HashMap<Student, Integer> reserveCount;
    private static int createDate;

    public static void reserve(Student student, Book book, int date, String dateOutput) {
        if (date != createDate) {
            reserveCount = new HashMap<>();
            createDate = date;
        }
        if (book.getType() == 1) {
            if (student.isHasTypeB()) { return; }
        } else {
            if (student.hasBookC(book)) { return; }
        }
        if (!reserveCount.containsKey(student)) {
            reserveCount.put(student, 0);
        }

        if (reserveCount.get(student) < 3) {

            Request request = new Request(dateOutput, student.getName(), book.getName());
            if (!REQUEST_LIST.contains(request)) {
                REQUEST_LIST.add(request);
                PrintAction.ordered(dateOutput, student, book);
                reserveCount.put(student, reserveCount.get(student) + 1);
            }
        }
    }

    public static void deliver(HashMap<Book, Integer> pool,
                               HashMap<String, Student> studentPool, String dateOutput) {
        // if students get B from 'deliver' or 'rent', need to flush
        int idx = 0;
        while (idx < REQUEST_LIST.size()) {
            Request request = REQUEST_LIST.get(idx);
            Book book = new Book(request.getBook());
            if (!pool.containsKey(book) || pool.get(book) == 0) {
                idx++;
                continue;
            }
            pool.put(book, pool.get(book) - 1);
            PrintAction.rented(dateOutput, studentPool.get(request.getStudent()), book, NAME);
            studentPool.get(request.getStudent()).rentBook(book);
            ArrayList<Integer> removedElements = new ArrayList<>();
            int back = 0;
            for (int i = 0; i < REQUEST_LIST.size(); i++) {
                Request value = REQUEST_LIST.get(i);
                if (request.getStudent().equals(value.getStudent())) {
                    if ((book.getType() == 1 && value.getBook().charAt(0) == 'B') ||
                            (book.getType() == 2 && value.getBook().equals(request.getBook()))) {
                        removedElements.add(i);
                    }
                }
            }
            for (int iidx = removedElements.size() - 1; iidx >= 0; iidx--) {
                int i = removedElements.get(iidx);
                if (i < idx) { back++; }
                REQUEST_LIST.remove(i);
            }
            idx -= back;
        }
    }

    public static void flushWith(Student student) {
        ArrayList<Request> removedList = new ArrayList<>();
        for (Request request : REQUEST_LIST) {
            if (request.getStudent().equals(student.getName()) &&
                    request.getBook().charAt(0) == 'B' &&
                    !removedList.contains(request)) {
                removedList.add(request);
            }
        }
        for (Request request : removedList) {
            REQUEST_LIST.remove(request);
        }
    }
}
//        for (Book book : pool.keySet()) {
//            if (!RESERVE_BOOK_LIST.containsKey(book)) { continue; }
//            LinkedList<Student> students = RESERVE_BOOK_LIST.get(book);
//            while (pool.get(book) > 0 && !students.isEmpty()) {
//                PrintAction.rented(dateOutput, students.getFirst(), book, NAME);
//                pool.put(book, pool.get(book) - 1);
//                Student removedStudent = students.getFirst();
//
//            }
//        }
/*
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
 */