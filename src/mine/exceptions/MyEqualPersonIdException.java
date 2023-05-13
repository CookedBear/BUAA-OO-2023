package mine.exceptions;

import spec1.exceptions.EqualPersonIdException;

public class MyEqualPersonIdException extends EqualPersonIdException {
    private final int personId;

    public MyEqualPersonIdException(int id) {
        this.personId = id;
        ExceptionCounter.adjustCount("epi");
        ExceptionCounter.adjustCause("epi", id);
    }

    public void print() {
        System.out.printf("epi-%d, %d-%d\n",
                ExceptionCounter.getCount("epi"),
                personId,
                ExceptionCounter.getCause("epi", personId));
    }
}
