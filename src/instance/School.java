package instance;

import service.Arrange;
import service.Machine;
import service.Purchase;
import service.Rent;
import service.Reserve;
import tool.DateCal;
import tool.PrintAction;

import java.util.ArrayList;
import java.util.HashMap;

public class School {
    private final String name;
    private final Reserve reserve = new Reserve();

    private final HashMap<Book, Integer> bookShelf = new HashMap<>();
    private final HashMap<Book, Integer> bookCount = new HashMap<>();
    private final HashMap<String, Student> studentPool = new HashMap<>();
    private final HashMap<Book, Integer> rentFailedPool = new HashMap<>();

    private final ArrayList<Request> reserveList = new ArrayList<>();
    private final ArrayList<Request> buyList = new ArrayList<>();
    private final ArrayList<Request> transList = new ArrayList<>();

    public School(String name) { this.name = name; }

    public void actionBorrow(Request request, ArrayList<Request> requestList) {
        Student student = getStudent(request.getStudent());

        Book book = getBook(request.getBook()); // 本地化书籍
        int bookType = book.getType();
        int date = request.getDate();
        Machine.queryBook(date, student, book);

        switch (bookType) {
            case 0:
                return;
            case 1:
            case 2:
                int count = bookShelf.getOrDefault(book, 0);
                if (count > 0) {
                    bookShelf.put(book, count - 1);
                    if (bookType == 1) {
                        Rent.rentTypeB(rentFailedPool, student,
                                book, date, reserveList, buyList);
                    } else {
                        Machine.rentTypeC(rentFailedPool, student,
                                book, date, reserveList, buyList);
                    }
                } else {
                    for (Request request1 : requestList) {
                        if (request.getStudent().getSchool().
                                equals(request1.getStudent().getSchool()) &&
                            request.getStudent().getName().
                                equals(request1.getStudent().getName()) &&
                            request.getBook().getName().equals(request1.getBook().getName())) {
                            return; // 存在同一个人的同一本请求
                        }
                    }
                    requestList.add(request); // 本地没有的书都直接返回，这时仍然没有学校
                }
                break;
            default:
                System.out.println("Undefined Book Type!");
        }
    }

    public void actionSmear(Request request) {
        Student student = getStudent(request.getStudent());
        Book book = request.getBook();
        student.smashBook(book); // 交给学生处理无学校的书籍
    }

    public Request actionLost(Request request) {
        Student student = getStudent(request.getStudent());
        Book book = student.getBook(request.getBook()); // 删掉的是哪个学校的书？
        if (book.getSchool().equals(name)) {
            bookCount.put(book, bookCount.get(book) - 1);
            student.lostBook(book, request.getDateOutput()); // 交给学生处理无学校的书籍
            return null;
        } else {
            // 丢书的时候没有把 book 所属的学校改过来，导致返回后去了错误的学校删除书籍，RE
            request.setBook(book);
            student.lostBook(book, request.getDateOutput());
            // 把别的学校书丢了之后，没把学生的书删掉，同时没输出 punish
            return request;
        }
    }

    public void actionReturn(Request request, ArrayList<Request> returnList) {
        Student student = getStudent(request.getStudent());
        Book book = student.getBook(request.getBook()); // 处理无学校书籍
        if (book.getSchool().equals(name)) {
            // 本学校书籍，直接归还至图书管理员处即可
            if (book.getType() == 1) {
                Rent.returnTypeB(rentFailedPool, student, book, request.getDateOutput());
            } else {
                Machine.returnTypeC(rentFailedPool, student, book, request.getDateOutput());
            }
        } else {
            // 需要归还到 returnList 中等待分发给各个学校
            if (book.getType() == 1) {
                Rent.returnTypeB(new HashMap<>(), student, book, request.getDateOutput());
            } else {
                Machine.returnTypeC(new HashMap<>(), student, book, request.getDateOutput());
            }
            Request returnRequest = new Request(request.getDateOutput(), student, book);
            returnList.add(returnRequest);
        }
    }

    public void deleteBook(Request request) {
        Book book = getBook(request.getBook());
        bookCount.put(book, bookCount.get(book) - 1);
    }

    public void reserveBook(Request request) {
        Student s1 = getStudent(request.getStudent());
        for (Book book1 : bookCount.keySet()) {
            if (book1.getName().equals(request.getBook().getName()) &&
                    bookCount.get(book1) > 0) {
                // reserve Book
                reserve.reserve(s1, book1, request.getDate(), reserveList);
                return;
            }
        }
        // buy Book
        Request buyRe = new Request(request.getDateOutput(), s1, request.getBook());
        buyList.add(buyRe);
        reserve.reserve(s1, request.getBook(), request.getDate(), reserveList);

    }

    public void transBack(Book book, int date) {
        PrintAction.transIn(DateCal.getDateOutput(date), book, "purchasing department", name);
        if (!rentFailedPool.containsKey(book)) {
            rentFailedPool.put(book, 1);
        } else {
            rentFailedPool.put(book, rentFailedPool.get(book) + 1);
        }
    }

    public boolean hasAvailable(Book book) {
        for (Book book1 : bookShelf.keySet()) {
            if (book1.getName().equals(book.getName()) &&
                book1.isShared() &&
                bookShelf.get(book1) > 0) {
                return true;
            }
        }
        return false;
    }

    public Book transOut(Book book) {
        Book newBook = getBook(book); // 本地化书籍
        bookShelf.put(newBook, bookShelf.get(newBook) - 1);
        return newBook;
    }

    public void transIn(Request request) {
        transList.add(request);
        PrintAction.transIn(DateCal.getDateOutput(request.getDate() + 1),
                request.getBook(), "purchasing department", name);
    }

    public void beginTrans() { transList.clear(); }

    public void giveOut(int date) {
        for (Request request : transList) {
            Student student = getStudent(request.getStudent());
            Book book = request.getBook();
            Purchase.rent(student, book, date);
            Arrange.flushWith(student, book, reserveList);
            Arrange.flushWith(student, book, buyList);
        }
    }

    public void buyIn(int date) {
        HashMap<Book, Integer> boughtCount = new HashMap<>();
        for (Request request : buyList) {
            boughtCount.put(request.getBook(), boughtCount.getOrDefault(request.getBook(), 0) + 1);
        }
        for (Book book : boughtCount.keySet()) {
            if (boughtCount.get(book) < 3) { boughtCount.put(book, 3); }
            Purchase.buyIn(book, boughtCount.get(book), rentFailedPool, bookCount, name, date);
        }

        buyList.clear();
    }

    public void deliver(String dateOutput) {
        reserve.deliver(rentFailedPool, reserveList, studentPool, dateOutput);
        for (Book book : rentFailedPool.keySet()) {
            bookShelf.put(book, bookShelf.getOrDefault(book, 0) + rentFailedPool.get(book));
        }
        rentFailedPool.clear();
    }

    private Student getStudent(Student student) {
        String name1 = student.getName();
        if (!studentPool.containsKey(name1)) { studentPool.put(name1, student); }

        return studentPool.get(name1);
    }

    private Book getBook(Book book) {
        Book[] books = bookShelf.keySet().toArray(new Book[0]);
        for (Book book1 : books) {
            if (book1.getName().equals(book.getName())) {
                return book1;
            }
        }
        // System.out.println("Bad Book in 'getBook'!");
        return book;
    }

    public String getName() { return name; }

    public HashMap<Book, Integer> getBookShelf() { return bookShelf; }

    public HashMap<Book, Integer> getBookCount() { return bookCount; }

    public HashMap<String, Student> getStudentPool() { return studentPool; }

    public HashMap<Book, Integer> getRentFailedPool() { return rentFailedPool; }

}
