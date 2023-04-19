package mine.exceptions;

import com.oocourse.spec1.exceptions.EqualRelationException;

public class MyEqualRelationException extends EqualRelationException {
    private final int personId1;
    private final int personId2;

    public MyEqualRelationException(int id1, int id2) {
        this.personId1 = Math.min(id1, id2);
        this.personId2 = (personId1 == id1) ? id2 : id1;

        if (id1 == id2) {
            ExceptionCounter.adjustCause("er", id1);
        } else {
            ExceptionCounter.adjustCause("er", id1);
            ExceptionCounter.adjustCause("er", id2);
        }
        ExceptionCounter.adjustCount("er");
    }

    public void print() {
        System.out.printf("er-%d, id1-%d, id2-%d\n",
                ExceptionCounter.getCount("er"),
                ExceptionCounter.getCause("er", personId1),
                ExceptionCounter.getCause("er", personId2));
    }
}
