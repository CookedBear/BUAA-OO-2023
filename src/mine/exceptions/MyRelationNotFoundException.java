package mine.exceptions;

import com.oocourse.spec1.exceptions.RelationNotFoundException;

public class MyRelationNotFoundException extends RelationNotFoundException {
    private final int personId1;
    private final int personId2;

    public MyRelationNotFoundException(int id1, int id2) {
        this.personId1 = Math.min(id1, id2);
        this.personId2 = (personId1 == id1) ? id2 : id1;
        ExceptionCounter.adjustCause("rnf", personId1);
        ExceptionCounter.adjustCause("rnf", personId2);
        ExceptionCounter.adjustCount("rnf");
    }

    public void print() {
        System.out.printf("rnf-%d, id1-%d, id2-%d\n");
    }
}
