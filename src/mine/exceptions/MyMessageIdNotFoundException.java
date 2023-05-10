package mine.exceptions;

import com.oocourse.spec3.exceptions.MessageIdNotFoundException;

public class MyMessageIdNotFoundException extends MessageIdNotFoundException {
    private final int id;
    private static final String TYPE = "minf";

    public MyMessageIdNotFoundException(int id) {
        this.id = id;
        ExceptionCounter.addCount(TYPE);
        ExceptionCounter.addCause(TYPE, id);
    }

    public void print() {
        System.out.printf("minf-%d, %d-%d\n",
                ExceptionCounter.getCount(TYPE),
                id,
                ExceptionCounter.getCause(TYPE, id));
    }
}
