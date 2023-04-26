package mine.exceptions;

import com.oocourse.spec2.exceptions.PersonIdNotFoundException;

public class MyPersonIdNotFoundException extends PersonIdNotFoundException {
    private final int personId;
    private static final String TYPE = "pinf";

    public MyPersonIdNotFoundException(int id) {
        this.personId = id;

        ExceptionCounter.addCount(TYPE);
        ExceptionCounter.addCause(TYPE, id);
    }

    public void print() {
        System.out.printf("pinf-%d, %d-%d\n",
                ExceptionCounter.getCount(TYPE),
                personId,
                ExceptionCounter.getCause(TYPE, personId));
    }
}
