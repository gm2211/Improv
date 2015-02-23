package overtone.wrapper;

import org.junit.Test;
import org.junit.experimental.categories.Category;

public class OvertoneWrapperTest {

    private OvertoneWrapper overtoneWrapper;

    public void setupWrapper() {
        overtoneWrapper = new OvertoneWrapper(new OvertoneWrapperConfig());
    }

    @Category(SlowTest.class)
    @Test
    public void shouldBeepFiveTimes() throws InterruptedException {
        setupWrapper();
        for (int i = 0; i < 5; i++) {
            overtoneWrapper.sendCommand("(demo (sin-osc))");
            Thread.sleep(2000);
        }
    }
}
