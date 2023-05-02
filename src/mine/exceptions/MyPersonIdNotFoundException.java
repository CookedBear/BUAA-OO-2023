package mine.exceptions;

import spec1.exceptions.PersonIdNotFoundException;

public class MyPersonIdNotFoundException extends PersonIdNotFoundException {
    private final int personId;

    public MyPersonIdNotFoundException(int id) {
        this.personId = id;
        ExceptionCounter.adjustCount("pinf");
        ExceptionCounter.adjustCause("pinf", id);
    }

    public void print() {
        System.out.printf("pinf-%d, %d-%d\n",
                ExceptionCounter.getCount("pinf"),
                personId,
                ExceptionCounter.getCause("pinf", personId));
    }
}
