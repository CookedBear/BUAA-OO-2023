import instance.Book;
import instance.Request;
import instance.Student;
import service.Machine;
import service.Rent;
import service.Reserve;
import tool.DateCal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static final HashMap<Book, Integer> BOOK_POOL = new HashMap<>();
    private static final HashMap<String, Student> STUDENT_POOL = new HashMap<>();
    private static final ArrayList<Request> REQUEST_LIST = new ArrayList<>();
    private static final HashMap<Book, Integer> RENT_FAILED_POOL = new HashMap<>();
    private static int lastingDate = 1;

    public static void main(String[] args) {
        // System.out.println("2023-02-" + String.format("%02d", 1));

        Scanner scanner = new Scanner(System.in);
        initBooks(scanner);
        initRequests(scanner);
        runServer();


    }

    private static void initBooks(Scanner scanner) {
        int people = scanner.nextInt();
        String patternString = "([\\w-]+) (\\d+)";
        Pattern pattern = Pattern.compile(patternString);
        String requestLine;
        for (int i = 0; i <= people; i++) {
            requestLine = scanner.nextLine();
            Matcher matcher = pattern.matcher(requestLine);
            if (matcher.find()) {
                String bookName = matcher.group(1);
                int count = Integer.parseInt(matcher.group(2));
                // System.out.println("store book " + bookName + " has " + count);
                BOOK_POOL.put(new Book(bookName), count);
            }
        }
    }

    private static void initRequests(Scanner scanner) {
        String patternString = "\\[([\\w-]+)] (\\d+) (\\w+) ([\\w-]+)";
        Pattern pattern = Pattern.compile(patternString);
        int requests = scanner.nextInt();
        String requestLine;
        for (int i = 0; i <= requests; i++) {
            requestLine = scanner.nextLine();
            Matcher matcher = pattern.matcher(requestLine);
            if (matcher.find()) {
                String date = matcher.group(1);
                String student = matcher.group(2);
                String action = matcher.group(3);
                String book = matcher.group(4);
                // System.out.println(date+student+action+book);
                REQUEST_LIST.add(new Request(date, student, action, book));
            }
        }
    }

    private static void runServer() {
        for (int requestDate = 1; requestDate <= 365; requestDate++) {
            serverMaintain(requestDate, DateCal.getDateOutput(requestDate));
            while (true) {
                Request request = REQUEST_LIST.get(0);
                if (request.getDate() != requestDate) { break; }
                String dateOutput = request.getDateOutput();
                int actionType = request.getAction();
                String student = request.getStudent();
                String book = request.getBook();
                switch (actionType) {
                    case 1:
                        actionBorrow(student, book, requestDate, dateOutput);
                        break;
                    case 2:
                        actionSmear(student, book);
                        break;
                    case 3:
                        actionLost(student, book, dateOutput);
                        break;
                    case 4:
                        actionReturn(student, book, dateOutput);
                        break;
                    default:
                        System.out.println("Undefined Action!");
                }
                REQUEST_LIST.remove(0);
                if (REQUEST_LIST.isEmpty()) { return; }
            }
        }
    }

    private static void serverMaintain(int date, String dateOutput) {
        if ((lastingDate - 1) / 3 != (date - 1) / 3) {
            // System.out.println("Recollect books!");
            Reserve.deliver(RENT_FAILED_POOL, STUDENT_POOL, dateOutput);
            for (Book book : RENT_FAILED_POOL.keySet()) {
                BOOK_POOL.put(book, BOOK_POOL.get(book) + RENT_FAILED_POOL.get(book));
            }
            RENT_FAILED_POOL.clear();
        }
        lastingDate = date;
    }

    private static void actionBorrow(String studentName, String bookName,
                                     int date, String dateOutput) {
        Book book = new Book(bookName);
        Student student;
        if (STUDENT_POOL.containsKey(studentName)) {
            student = STUDENT_POOL.get(studentName);
        } else {
            student = new Student(studentName);
            STUDENT_POOL.put(studentName, student);
        }
        int bookType = book.getType();
        Machine.queryBook(dateOutput, student, book);
        switch (bookType) {
            case 0:
                return;
            case 1:
            case 2:
                int count = BOOK_POOL.get(book);
                if (count > 0) {
                    BOOK_POOL.put(book, count - 1);
                    if (bookType == 1) {
                        Rent.rentTypeB(RENT_FAILED_POOL, student, book, dateOutput);
                    } else {
                        Machine.rentTypeC(RENT_FAILED_POOL, student, book, dateOutput);
                    }
                } else {
                    Reserve.reserve(student, book, date, dateOutput);
                }
                break;
            default:
                System.out.println("Undefined Book Type!");
        }
    }

    private static void actionSmear(String studentName, String bookName) {
        Student student = STUDENT_POOL.get(studentName);
        student.smashBook(new Book(bookName));
    }

    private static void actionLost(String studentName, String bookName, String dateOutput) {
        Student student = STUDENT_POOL.get(studentName);
        student.lostBook(new Book(bookName), dateOutput);
    }

    private static void actionReturn(String studentName, String bookName,
                                     String dateOutput) {
        Student student = STUDENT_POOL.get(studentName);
        Book book = new Book(bookName);
        if (book.getType() == 1) {
            Rent.returnTypeB(RENT_FAILED_POOL, student, book, dateOutput);
        } else {
            Machine.returnTypeC(RENT_FAILED_POOL, student, book, dateOutput);
        }
    }
}

/*
3
A-0000 3
B-0000 2
C-0000 1
11
[2023-01-01] 11114514 borrowed C-0000
[2023-01-01] 11919810 borrowed C-0000
[2023-01-01] 11114514 borrowed C-0000
[2023-01-01] 11114514 borrowed B-0000
[2023-01-01] 11114514 borrowed A-0000
[2023-01-01] 11114514 borrowed A-0000
[2023-01-01] 11114514 borrowed A-0000
[2023-01-01] 11114514 borrowed A-0000
[2023-01-02] 11114514 returned C-0000
[2023-01-03] 10101010 borrowed C-0000
[2023-01-07] 11919810 returned C-0000
 */

/*

// 按顺序 'reserve' 书：01-04会把 B-1 借给 2
3
B-1 1
B-2 1
B-3 1
7
[2023-01-01] 1 borrowed B-1
[2023-01-01] 3 borrowed B-2
[2023-01-01] 1 borrowed B-1
[2023-01-01] 2 borrowed B-1
[2023-01-01] 2 borrowed B-2
[2023-01-02] 1 returned B-1
[2023-01-05] 1 borrowed B-1

// 如果 'rent' 了一本 B，清除掉所有待借的 B： 01-04 不会把 B-1 借给 3
3
B-1 1
B-2 1
B-3 1
7
[2023-01-01] 1 borrowed B-1
[2023-01-01] 2 borrowed B-2
[2023-01-01] 3 borrowed B-1
[2023-01-01] 3 borrowed B-2
[2023-01-01] 3 borrowed B-3
[2023-01-01] 1 returned B-1
[2023-01-05] 1 borrowed B-2
 */