package managers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ManagersTest {

    @Test
    void getDefaultHistoryTest() {
        Assertions.assertNotNull(Managers.getDefaultHistory());
    }

    @Test
    void getDefaultTest() {
        Assertions.assertNotNull(Managers.getDefault());
    }
}