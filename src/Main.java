import instance.Book;
import instance.Request;
import instance.Student;
import service.Machine;
import service.Rent;
import service.Reserve;
import tool.PrintAction;

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

    public static void main(String[] args) {
        System.out.println("Hello world!");

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
        int lastingDate = 1;
        for (Request request : REQUEST_LIST) {
            int requestDate = request.getDate();
            String dateOutput = request.getDateOutput();
            int actionType = request.getAction();
            String student = request.getStudent();
            String book = request.getBook();
            if (requestDate != lastingDate) { serverMaintain(); }
            switch (actionType) {
                case 1:
                    actionBorrow(student, book, requestDate, dateOutput);
                    break;
                case 2:
                    actionSmear();
                    break;
                case 3:
                    actionLost();
                    break;
                case 4:
                    actionReturn();
                    break;
                default:
                    System.out.println("Undefined Action!");
            }
        }
    }

    private static void serverMaintain() {

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
                int count = BOOK_POOL.get(book);
                if (count > 0) {
                    BOOK_POOL.put(book, count - 1);
                    Rent.rentTypeB(RENT_FAILED_POOL, student, book, dateOutput);
                } else {
                    Reserve.reserveTypeB();
                }
                break;
            case 2:
                count = BOOK_POOL.get(book);
                if (count > 0) {
                    BOOK_POOL.put(book, count - 1);
                    Machine.rentTypeC(RENT_FAILED_POOL, student, book, dateOutput);
                } else {
                    Reserve.reserveTypeC();
                }
                break;
            default:
                System.out.println("Undefined Book Type!");
        }
    }

    private static void actionSmear() {

    }

    private static void actionLost() {

    }

    private static void actionReturn() {

    }
}

