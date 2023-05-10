package mine.exceptions;

import com.oocourse.spec3.exceptions.PathNotFoundException;

public class MyPathNotFoundException extends PathNotFoundException {
    private final int id;
    private static final String TYPE = "pnf";

    public MyPathNotFoundException(int id) {
        this.id = id;
        ExceptionCounter.addCount(TYPE);
        ExceptionCounter.addCause(TYPE, id);
    }

    public void print() {
        System.out.printf(TYPE + "-%d, %d-%d\n",
                ExceptionCounter.getCount(TYPE),
                id,
                ExceptionCounter.getCause(TYPE, id));
    }
}
