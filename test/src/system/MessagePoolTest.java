package src.system;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import src.base.StubBody;

public class MessagePoolTest {

    @Test
    public void testGetMessageNullSafe() {
        StubBody body = new StubBody();
        String message = assertDoesNotThrow(() ->
                MessagePool.getMessage(body, MessagePool.Action.Scream));
        assertNull(message);
    }
}
