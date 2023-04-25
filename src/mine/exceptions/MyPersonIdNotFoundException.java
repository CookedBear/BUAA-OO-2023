package mine.exceptions;

import com.oocourse.spec1.exceptions.PersonIdNotFoundException;

public class MyPersonIdNotFoundException extends PersonIdNotFoundException {
    private final int personId;

    public MyPersonIdNotFoundException(int id) {
        this.personId = id;
        ExceptionCounter.addCount("pinf");
        ExceptionCounter.adjustCause("pinf", id);
    }

    public void print() {
        System.out.printf("pinf-%d, %d-%d\n",
                ExceptionCounter.getCount("pinf"),
                personId,
                ExceptionCounter.getCause("pinf", personId));
    }
}
