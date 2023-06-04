package service;

import instance.Book;
import instance.Request;
import instance.Student;
import tool.DateCal;
import tool.PrintAction;

import java.util.ArrayList;
import java.util.HashMap;

public class Reserve {
    private static final String NAME = "ordering librarian";

    // book 不含学校名
    private HashMap<Student, Integer> reserveCount;
    private int createDate;

    public boolean reserve(Student student, Book book, int date, ArrayList<Request> reserveList) {
        if (date != createDate) {
            reserveCount = new HashMap<>();
            createDate = date;
        }
        if (book.getType() == 1) {
            if (student.isHasTypeB()) { return false; }
        } else {
            if (student.hasBookC(book)) { return false; }
        }
        if (!reserveCount.containsKey(student)) { reserveCount.put(student, 0); }

        if (reserveCount.get(student) < 3) {
            Request request = new Request(DateCal.getDateOutput(date), student, book);
            if (!reserveList.contains(request)) {
                reserveList.add(request);
                PrintAction.ordered(DateCal.getDateOutput(date), student, book);
                reserveCount.put(student, reserveCount.get(student) + 1);
            }
        } else {
            return false;
        }
        return true;
    }

    public void deliver(HashMap<Book, Integer> pool, ArrayList<Request> reserveList,
                       HashMap<String, Student> studentPool, String dateOutput) {
        // if students get B from 'deliver' or 'rent', need to flush
        int idx = 0;
        while (idx < reserveList.size()) {
            Request request = reserveList.get(idx);
            Book book = request.getBook();
            if (!pool.containsKey(book) || pool.get(book) == 0) {
                idx++;
                continue;
            }
            pool.put(book, pool.get(book) - 1);
            PrintAction.rented(DateCal.getDate(dateOutput), request.getStudent(), book, NAME);
            request.getStudent().rentBook(book);
            ArrayList<Integer> removedElements = new ArrayList<>();
            int back = 0;
            for (int i = 0; i < reserveList.size(); i++) {
                Request value = reserveList.get(i);
                if (request.getStudent().equals(value.getStudent())) {
                    if ((book.getType() == 1 && value.getBook().getType() == 1) ||
                        (book.getType() == 2 && value.getBook().getName().equals(book.getName()))) {
                        removedElements.add(i);
                    }
                }
            }
            for (int iidx = removedElements.size() - 1; iidx >= 0; iidx--) {
                int i = removedElements.get(iidx);
                if (i < idx) { back++; }
                reserveList.remove(i);
            }
            idx -= back;
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