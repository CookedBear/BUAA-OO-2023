import instance.Book;
import instance.Request;
import instance.School;
import instance.Student;
import tool.DateCal;
import tool.PrintAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static final ArrayList<School> SCHOOL_POOL = new ArrayList<>();
    private static final ArrayList<Request> REQUEST_LIST = new ArrayList<>();
    // 保存所有输入的列表
    private static final ArrayList<Request> RESERVE_LIST = new ArrayList<>();
    // 保存所有预定请求的列表
    private static final ArrayList<Request> TRANS_LIST = new ArrayList<>();
    // 保存当日借出校际书目的列表: book -> student
    private static final ArrayList<Request> RETURN_LIST = new ArrayList<>();
    // 保存所有当日校际还书的列表，只使用 Student(from) & Book(to) & date 字段
    private static int lastingDate = 0;
    private static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {
        // System.out.println("2023-02-" + String.format("%02d", 1));

        initSchool();
        initRequests();
        runServer();

    }

    private static void initSchool() {
        int school = SCANNER.nextInt();
        String patternString = "(\\w+) (\\d+)";
        Pattern pattern = Pattern.compile(patternString);
        String schoolLine = SCANNER.nextLine();
        for (int i = 0; i < school; i++) {
            schoolLine = SCANNER.nextLine();
            Matcher matcher = pattern.matcher(schoolLine);
            if (matcher.find()) {
                String schoolName = matcher.group(1);
                int bookCount = Integer.parseInt(matcher.group(2));
                School school1 = new School(schoolName);
                initBooks(school1, bookCount);
                SCHOOL_POOL.add(school1);
            }
        }
    }

    private static void initBooks(School school, int bookCounts) {
        String patternString = "([\\w-]+) (\\d+) ([YN])";
        Pattern pattern = Pattern.compile(patternString);
        String requestLine;
        HashMap<Book, Integer> bookShelf = school.getBookShelf();
        HashMap<Book, Integer> bookCount = school.getBookCount();
        for (int i = 0; i < bookCounts; i++) {
            requestLine = SCANNER.nextLine();
            Matcher matcher = pattern.matcher(requestLine);
            if (matcher.find()) {
                String bookName = matcher.group(1);
                int count = Integer.parseInt(matcher.group(2));
                boolean shared = (matcher.group(3).equals("Y"));
                // System.out.println("store book " + bookName + " has " + count);
                bookShelf.put(new Book(bookName, school.getName(), shared), count);
                bookCount.put(new Book(bookName, school.getName(), shared), count);
            }
        }
    }

    private static void initRequests() {
        String patternString = "\\[([\\w-]+)] (\\w+)-(\\d+) (\\w+) ([\\w-]+)";
        Pattern pattern = Pattern.compile(patternString);
        int requests = SCANNER.nextInt();
        String requestLine = SCANNER.nextLine();
        for (int i = 0; i < requests; i++) {
            requestLine = SCANNER.nextLine();
            Matcher matcher = pattern.matcher(requestLine);
            if (matcher.find()) {
                String date = matcher.group(1);
                String school = matcher.group(2);
                String student = matcher.group(3);
                String action = matcher.group(4);
                String book = matcher.group(5);
                // System.out.println(date+student+action+book);
                REQUEST_LIST.add(new Request(date, new Student(student, school),
                                action, new Book(book, school, false)));
            }
        }
    }

    private static void runServer() {
        // initRequest 中生成的 Book 都不含目标学校名，使用前需要进行更改// 现在含了，但是 shared 不一定对
        for (int requestDate = 1; requestDate <= 365; requestDate++) {
            serverTransIn();
            serverTransBack(requestDate);
            serverGiveOut(requestDate);

            serverMaintain(requestDate, DateCal.getDateOutput(requestDate));
            while (true) {
                Request request = REQUEST_LIST.get(0);
                if (request.getDate() != requestDate) { break; }

                int actionType = request.getAction();
                Student student = request.getStudent();
                School school = getSchool(student.getSchool());

                switch (actionType) {
                    case 1:
                        school.actionBorrow(request, RESERVE_LIST);
                        break;
                    case 2:
                        school.actionSmear(request);
                        break;
                    case 3:
                        Request request1 = school.actionLost(request);
                        if (request1 != null) {
                            getSchool(request1.getBook().getSchool()).deleteBook(request1);
                        }
                        break;
                    case 4:
                        school.actionReturn(request, RETURN_LIST);
                        break;
                    default:
                        System.out.println("Undefined Action!");
                }
                REQUEST_LIST.remove(0);
                if (REQUEST_LIST.isEmpty()) { break; }
            }
            serverTransOut();
            transOutPrint();
            if (REQUEST_LIST.isEmpty()) { break; }
        }
    }

    private static void serverMaintain(int date, String dateOutput) {
        if ((lastingDate - 1) / 3 != (date - 1) / 3 || date == 1) {
            serverBuyIn(date);
            System.out.printf("[%s] arranging librarian arranged all the books\n", dateOutput);
            for (School school : SCHOOL_POOL) {
                school.deliver(dateOutput);
            }
            // System.out.println("Recollect books!");
            //            Reserve.deliver(RENT_FAILED_POOL, STUDENT_POOL, dateOutput);
            //            for (Book book : RENT_FAILED_POOL.keySet()) {
            //                BOOK_POOL.put(book, BOOK_POOL.get(book) + RENT_FAILED_POOL.get(book));
            //            }
            //            RENT_FAILED_POOL.clear();

        }
        lastingDate = date;
    }

    private static void serverTransOut() {
        for (Request request : RESERVE_LIST) {
            Student student = request.getStudent();
            student = getSchool(student.getSchool()).getStudent(student);
            Book book = request.getBook();


            int f = 0;
            if (1 == try2Add(student, book)) { continue; }
            School school = getSchool(student.getSchool());

            for (School s1 : SCHOOL_POOL) {
                if (s1.hasAvailable(book)) {
                    if ((f = try2Add(student, book)) != 0) { // f = 1: 手上有B，f = 2: 手上没、校际已有B
                        // 校际，请求不合法（学生有B借B/校际给过B，C同理），跳过此次请求，避免送书过多
                        break;
                    }
                    TRANS_LIST.add(new Request(request.getDateOutput(),
                            student, s1.transOut(book)));
                    f = 1;
                    break;
                }
            }
            if (f != 0) { continue; }
            // 请求未被校际借阅满足
            school.reserveBook(request);
        }
        RESERVE_LIST.clear();
    }

    private static void transOutPrint() {
        for (Request request : RETURN_LIST) {
            PrintAction.transOut(request.getDateOutput(), request.getBook(),
                    "purchasing department", request.getStudent().getSchool());
        }
        for (Request request : TRANS_LIST) {
            PrintAction.transOut(request.getDateOutput(), request.getBook(),
                    "purchasing department", request.getBook().getSchool());
        }
    }

    private static void serverTransIn() {
        for (School school : SCHOOL_POOL) {
            school.beginTrans();
            for (Request request : TRANS_LIST) {
                if (request.getStudent().getSchool().equals(school.getName())) {
                    school.transIn(request);
                }
            }
        }
        TRANS_LIST.clear();
    }

    public static void serverTransBack(int date) {
        for (Request request : RETURN_LIST) {
            Book book = request.getBook();
            School school = getSchool(book.getSchool());
            school.transBack(book, date);
        }
        RETURN_LIST.clear();
    }

    private static void serverGiveOut(int date) {
        for (School school : SCHOOL_POOL) {
            school.giveOut(date);
        }
    }

    private static void serverBuyIn(int date) {
        for (School school : SCHOOL_POOL) {
            school.buyIn(date);
        }
    }

    private static int try2Add(Student student, Book book) {
        if ((book.getType() == 1 && student.isHasTypeB()) ||
            (book.getType() == 2 && student.hasBookC(book))) {
            return 1;
        }
        for (Request request : TRANS_LIST) {
            Student res = request.getStudent();
            if (student.getName().equals(res.getName()) &&
                student.getSchool().equals(res.getSchool()) &&
                ((book.getType() == 1 && request.getBook().getType() == 1) ||
                 (book.getType() == 2 && request.getBook().getName().equals(book.getName())))) {
                return 2;
            }
        }
        return 0;
    }

    private static School getSchool(String name) {
        for (School school : SCHOOL_POOL) {
            if (school.getName().equals(name)) {
                return school;
            }
        }
        System.out.println("cannot find school in 'getSchool'!");
        return new School("BadName");
    }
}
/*
看看 lostBook 能不能真的把书丢掉
 */

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