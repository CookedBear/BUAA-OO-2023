import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

public class Main {

    public static boolean DEBUG = true;

    public static int BOOK_NUMBER;
    public static int STUDENT_NUMBER;
    public static int INS_NUMBER = 100;
    public static int TOTAL_DATE;

    public static Random random = new Random();

    public static ArrayList<Integer> DATE_LIST = new ArrayList<>();
    public static HashMap<String, Book> BOOK_POOL = new HashMap<>();
    public static HashMap<String, Student> STUDENT_POOL = new HashMap<>();
    public static ArrayList<String> INS_POOL = new ArrayList<>();

    public static void main(String[] args) {

        initArgs();
        initDate();
        initBooks();
        initStudents();

        generateIns();

        dataPrint();
    }

    public static void initArgs() {
        BOOK_NUMBER = random.nextInt(10) + 5;
        STUDENT_NUMBER = random.nextInt(5) + 3;
        TOTAL_DATE = random.nextInt(10) * 5 + 5;
        debugF(String.format("\nBOOK_NUMBER = %d\nSTUDENT_NUMBER = %d\nTOTAL_DATE = %d\nINS_NUMBER = 100\n",
                BOOK_NUMBER, STUDENT_NUMBER, TOTAL_DATE),
                DEBUG);
    }

    public static void initDate() {
        for (int i = 0; i < INS_NUMBER; i++) {
            int date = random.nextInt(TOTAL_DATE / 2) + 1;
            DATE_LIST.add(date * 2);
        }
        Collections.sort(DATE_LIST);
        debugF("Dates: " + DATE_LIST + "\n", DEBUG);
    }

    public static void initBooks() {
        for (int i = 0; i < BOOK_NUMBER; i++) {
            String bookName = String.format("%04d", i);
            String type;
            int r = random.nextInt(10);
            if (r <= 1) {
                type = "A-";
            } else if (r <= 5) {
                type = "B-";
            } else {
                type = "C-";
            }
            bookName = type + bookName;
            Book book = new Book(bookName);
            BOOK_POOL.put(bookName, book);

            debugF(("Created book: " + bookName), DEBUG);
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

    public static void generateIns() {
        for (int i = 0; i < INS_NUMBER; i++) {
            String code;

        }
    }

    public static void dataPrint() {
        System.out.println(BOOK_NUMBER);
        for (String name : BOOK_POOL.keySet()) {
            System.out.println(name);
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
    }
}