package mine.exceptions;

import com.oocourse.spec3.exceptions.AcquaintanceNotFoundException;

public class MyAcquaintanceNotFoundException extends AcquaintanceNotFoundException {
    private final int id;
    private static final String TYPE = "anf";

    public MyAcquaintanceNotFoundException(int id) {
        this.id = id;
        ExceptionCounter.addCount(TYPE);
        ExceptionCounter.addCause(TYPE, id);
    }

    public void print() {
        System.out.printf("anf-%d, %d-%d\n",
                ExceptionCounter.getCount(TYPE),
                id,
                ExceptionCounter.getCause(TYPE, id));
    }
}
