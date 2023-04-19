package mine.exceptions;

import com.oocourse.spec1.exceptions.PersonIdNotFoundException;

public class MyPersonIdNotFoundException extends PersonIdNotFoundException {
    private final int personId;
    public MyPersonIdNotFoundException(int id) {
        this.personId = id;
        ExceptionCounter.adjustCount("pinf");
        ExceptionCounter.adjustCause("pinf", id);
    }

    public void print() {
        System.out.printf("pinf-%d, id-%d\n",
                ExceptionCounter.getCount("pinf"),
                ExceptionCounter.getCause("pinf", personId));
    }
}
