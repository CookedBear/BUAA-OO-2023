package mine.exceptions;

import com.oocourse.spec3.exceptions.EqualRelationException;

public class MyEqualRelationException extends EqualRelationException {
    private final int personId1;
    private final int personId2;
    private static final String TYPE = "er";

    public MyEqualRelationException(int id1, int id2) {
        this.personId1 = Math.min(id1, id2);
        this.personId2 = (personId1 == id1) ? id2 : id1;

        ExceptionCounter.addCount(TYPE);
        if (id1 == id2) {
            ExceptionCounter.addCause(TYPE, id1);
        } else {
            ExceptionCounter.addCause(TYPE, id1);
            ExceptionCounter.addCause(TYPE, id2);
        }
    }

    public void print() {
        System.out.printf("er-%d, %d-%d, %d-%d\n",
                ExceptionCounter.getCount(TYPE),
                personId1,
                ExceptionCounter.getCause(TYPE, personId1),
                personId2,
                ExceptionCounter.getCause(TYPE, personId2));
    }
}
