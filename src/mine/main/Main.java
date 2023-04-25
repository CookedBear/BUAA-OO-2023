package mine.main;

import com.oocourse.spec1.main.Runner;

import static mine.exceptions.ExceptionCounter.initCauses;

public class Main {
    public static void main(String[] args) throws Exception {
        initCauses();
        Runner runner = new Runner(MyPerson.class, MyNetwork.class);
        runner.run();
    }
}
