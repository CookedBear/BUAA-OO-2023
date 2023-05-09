package mine.exceptions;

import com.oocourse.spec2.exceptions.EqualGroupIdException;

public class MyEqualGroupIdException extends EqualGroupIdException {
    private final int id;
    private static final String TYPE = "egi";

    public MyEqualGroupIdException(int id) {
        this.id = id;
        ExceptionCounter.addCount(TYPE);
        ExceptionCounter.addCause(TYPE, id);
    }

    public void print() {
        System.out.printf("egi-%d, %d-%d\n",
                ExceptionCounter.getCount(TYPE),
                id,
                ExceptionCounter.getCause(TYPE, id));
    }
}
