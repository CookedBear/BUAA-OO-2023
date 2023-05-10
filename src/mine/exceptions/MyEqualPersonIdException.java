package mine.exceptions;

import com.oocourse.spec3.exceptions.EqualPersonIdException;

public class MyEqualPersonIdException extends EqualPersonIdException {
    private final int personId;
    private static final String TYPE = "epi";

    public MyEqualPersonIdException(int id) {
        this.personId = id;

        ExceptionCounter.addCount(TYPE);
        ExceptionCounter.addCause(TYPE, id);
    }

    public void print() {
        System.out.printf("epi-%d, %d-%d\n",
                ExceptionCounter.getCount(TYPE),
                personId,
                ExceptionCounter.getCause(TYPE, personId));
    }
}
