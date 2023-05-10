package mine.main;

import com.oocourse.spec3.main.Runner;

import static mine.exceptions.ExceptionCounter.initCauses;

public class Main {
    public static void main(String[] args) throws Exception {
        initCauses();
        Runner runner = new Runner(MyPerson.class, MyNetwork.class, MyGroup.class, MyMessage.class,
                                   MyEmojiMessage.class, MyNoticeMessage.class,
                                   MyRedEnvelopeMessage.class);
        runner.run();
    }
}
