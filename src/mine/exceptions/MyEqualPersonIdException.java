package mine.exceptions;

import com.oocourse.spec1.exceptions.EqualPersonIdException;

public class MyEqualPersonIdException extends EqualPersonIdException{
    private final int personId;


    public MyEqualPersonIdException(int id) {
        this.personId = id;
        ExceptionCounter.adjustCount("epi");
        ExceptionCounter.adjustCause("epi", id);
    }
    public void print() {
        System.out.printf("epi-%d, id-%d\n",
                ExceptionCounter.getCount("epi"),
                ExceptionCounter.getCause("epi", personId));
    }
}
