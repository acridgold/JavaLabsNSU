package chat.client;

import org.junit.Test;
import static org.junit.Assert.*;

public class ServerConnectionTest {

    @Test
    public void testConnectToNonExistentServerReturnsFalse() {
        ServerConnection conn = new ServerConnection();
        boolean result = conn.connect("localhost", 1, "TestUser", "TestClient");
        assertFalse(result);
    }

    @Test
    public void testOnErrorCallbackCalledOnFailure() {
        ServerConnection conn = new ServerConnection();
        String[] errorHolder = {null};
        conn.setOnError(msg -> errorHolder[0] = msg);

        conn.connect("localhost", 1, "TestUser", "TestClient");

        assertNotNull(errorHolder[0]);
        assertTrue(errorHolder[0].length() > 0);
    }
}
