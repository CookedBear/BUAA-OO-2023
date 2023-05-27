
import instance.*;
import service.*;
import tool.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static boolean DEBUG = false;

    public static int BOOK_NUMBER;
    public static int STUDENT_NUMBER;
    public static int INS_NUMBER = 100;
    public static int TOTAL_DATE;

    public static Random random = new Random();
    public static String[] ACTIONS = {"borrowed", "smeared", "lost", "returned"};

    public static ArrayList<Integer> DATE_LIST = new ArrayList<>();
    public static HashMap<Book, Integer> BOOK_POOL = new HashMap<>();
    public static HashMap<Book, Integer> BOOK_COUNT_POOL = new HashMap<>();
    public static HashMap<Book, Integer> BOOK_OUTPUT_POOL = new HashMap<>();
    public static HashMap<String, Student> STUDENT_POOL = new HashMap<>();
    public static ArrayList<String> INS_POOL = new ArrayList<>();
    private static final HashMap<Book, Integer> RENT_FAILED_POOL = new HashMap<>();
    private static int lastingDate = 1;

    static FileWriter fw;

    static {
        try {
            fw = new FileWriter("./debug.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) {

        initArgs();
        initDate();
        initBooks();
        initStudents();

        generateIns();

        if (!DEBUG) { dataPrint(); }
    }

    public static void initArgs() {
        BOOK_NUMBER = random.nextInt(6) + 3;
        STUDENT_NUMBER = random.nextInt(5) + 3;
        TOTAL_DATE = random.nextInt(10) * 5 + 5;
        debugF(String.format("\nBOOK_NUMBER = %d\nSTUDENT_NUMBER = %d\nTOTAL_DATE = %d\nINS_NUMBER = 100\n",
                BOOK_NUMBER, STUDENT_NUMBER, TOTAL_DATE),
                DEBUG);
    }

    public static void initDate() {
        for (int i = 0; i < INS_NUMBER; i++) {
            int date = random.nextInt(TOTAL_DATE) + 1;
            DATE_LIST.add(date * 2);
        }
        Collections.sort(DATE_LIST);
        debugF("Dates: " + DATE_LIST + "\n", DEBUG);
    }

    public static void initBooks() {
        for (int i = 0; i < BOOK_NUMBER; i++) {
            String bookName = String.format("%04d", i);
            String type;
            int r = random.nextInt(20);
            if (r <= 1) {
                type = "A-";
            } else if (r <= 10) {
                type = "B-";
            } else {
                type = "C-";
            }
            bookName = type + bookName;
            Book book = new Book(bookName);
            int temp = random.nextInt(9);
            int cnt = (int) (Math.sqrt(temp) + 1);
            BOOK_POOL.put(book, cnt);
            BOOK_COUNT_POOL.put(book, cnt);
            BOOK_OUTPUT_POOL.put(book, cnt);

            debugF(("Created book: " + bookName + " * " + cnt), DEBUG);
        }
        if (DEBUG) { System.out.println(); }
    }

    public static void initStudents() {
        for (int i = 0; i < STUDENT_NUMBER; i++) {
            String name = String.format("%08d", random.nextInt(100000000));
            while (STUDENT_POOL.containsKey(name)) {
                name = String.format("%08d", random.nextInt(100000000));
            }
            Student student = new Student(name);
            STUDENT_POOL.put(name, student);
            debugF("Created student: " + name, DEBUG);
        }
        if (DEBUG) { System.out.println(); }
    }

    /*
     * 1. rent C (with copy / without copy)
     * 2. rent B (with B / without B)
     * 3. smash book
     * 4. lost book
     * 5. return book (return B / return C)
     */
    public static void generateIns() {
        for (int i = 0; i < INS_NUMBER; i++) {
            String code;
            Student student = randomStudent();
            int actions = random.nextInt(20);
            String action = (!student.hasBook() || actions <= 0) ? ACTIONS[0] :
                            (!student.allSmash() && actions <= 7) ? ACTIONS[1] :
                            (!student.allSmash() && actions <= 7) ? ACTIONS[2] :
                            ACTIONS[3];
            Book book = randomBook(action, student);
            if (action.equals(ACTIONS[2])) {
                BOOK_COUNT_POOL.put(book, BOOK_COUNT_POOL.get(book) - 1);
            }
            String date = test.DateCal.getDateOutput(DATE_LIST.get(i));
            code = String.format("[%s] %s %s %s", date, student.getName(), action, book.getName());
            // [2023-01-01] 1 borrowed B-1
            INS_POOL.add(code);
            debugF(" Code " + String.format("%03d", (i + 1)) + " : " + code, DEBUG);
            runRequests(code);
            if (libraryEmpty()) { debugF("Library is EMPTY!", DEBUG); return; }
        }
    }

    private static void runRequests(String input) {
        String patternString = "\\[([\\w-]+)] (\\d+) (\\w+) ([\\w-]+)";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            String date = matcher.group(1);
            String student = matcher.group(2);
            String action = matcher.group(3);
            String book = matcher.group(4);
            runServer(new Request(date, student, action, book));
        }
    }

    private static void runServer(Request request) {
        int requestDate = request.getDate();
        while (requestDate > lastingDate) {
            lastingDate++;
            serverMaintain(lastingDate, DateCal.getDateOutput(lastingDate));
        }

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
    }

    private static void serverMaintain(int date, String dateOutput) {
        if ((date - 2) / 3 != (date - 1) / 3) {
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

    public static void dataPrint() {
        System.out.println(BOOK_NUMBER);
        for (Book name : BOOK_OUTPUT_POOL.keySet()) {
            System.out.println(name.getName() + " " + BOOK_OUTPUT_POOL.get(name));
        }

        System.out.println(INS_NUMBER);
        for (String code : INS_POOL) {
            System.out.println(code);
        }
    }

    public static void debugF(String str, boolean debug) {
        if (debug) {
            System.out.println("Debug: " + str);
        }
        try {
            fw.write(str + "\n");
        } catch (Exception ignored) {

        }
    }

    public static Student randomStudent() {
        String[] keys = STUDENT_POOL.keySet().toArray(new String[0]);
        return STUDENT_POOL.get(keys[random.nextInt(keys.length)]);
    }

    public static Book randomBook(String action, Student student) {
        ArrayList<Book> books = new ArrayList<>();
        HashMap<Book, Integer> randomPool = new HashMap<>();
        if (action.equals(ACTIONS[0])) { // borrowed: ANYONE
            for (Book book : BOOK_COUNT_POOL.keySet()) {
                if (BOOK_COUNT_POOL.get(book) > 0) {
                    books.add(book);
                }
            }
        } else if (action.equals(ACTIONS[1]) || (action.equals(ACTIONS[2]))) { // smash: HAS & NOT smashed & NOT lost, lost: HAS & NOT smashed
            HashMap<Book, Integer> tempPool = student.getBag();
            for (Book book : tempPool.keySet()) {
                if (tempPool.get(book) != 1) {
                    books.add(book);
                }
            }
        } else if (action.equals(ACTIONS[3])) { // return: HAS
            randomPool = student.getBag();
            books.addAll(randomPool.keySet());
        }

        return books.get(random.nextInt(books.size()));
    }

    public static boolean libraryEmpty() {
        int totalCount = 0;
        for (int cnt : BOOK_COUNT_POOL.values()) {
            totalCount += cnt;
        }
        return totalCount == 0;
    }

}