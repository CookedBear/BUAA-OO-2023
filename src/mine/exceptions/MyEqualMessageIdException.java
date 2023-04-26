package mine.exceptions;

import com.oocourse.spec2.exceptions.EqualMessageIdException;

public class MyEqualMessageIdException extends EqualMessageIdException {
    private final int id;
    private static final String TYPE = "emi";

    public MyEqualMessageIdException(int id) {
        this.id = id;
        ExceptionCounter.addCount(TYPE);
        ExceptionCounter.addCause(TYPE, id);
    }

    public void print() {
        System.out.printf("emi-%d, %d-%d\n",
                ExceptionCounter.getCount(TYPE),
                id,
                ExceptionCounter.getCause(TYPE, id));
    }
}
