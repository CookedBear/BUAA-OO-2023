package mine.exceptions;

import com.oocourse.spec2.exceptions.RelationNotFoundException;

public class MyRelationNotFoundException extends RelationNotFoundException {
    private final int personId1;
    private final int personId2;
    private static final String TYPE = "rnf";

    public MyRelationNotFoundException(int id1, int id2) {
        this.personId1 = Math.min(id1, id2);
        this.personId2 = (personId1 == id1) ? id2 : id1;

        ExceptionCounter.addCount(TYPE);
        ExceptionCounter.addCause(TYPE, personId1);
        ExceptionCounter.addCause(TYPE, personId2);
    }

    public void print() {
        System.out.printf("rnf-%d, %d-%d, %d-%d\n",
                ExceptionCounter.getCount(TYPE),
                personId1,
                ExceptionCounter.getCause(TYPE, personId1),
                personId2,
                ExceptionCounter.getCause(TYPE, personId2));
    }
}
