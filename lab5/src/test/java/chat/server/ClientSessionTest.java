package chat.server;

import org.junit.Test;
import static org.junit.Assert.*;

public class ClientSessionTest {

    @Test
    public void testSessionCreation() {
        ClientSession session = new ClientSession("Alice", "JavaClient");
        assertEquals("Alice", session.getName());
        assertEquals("JavaClient", session.getClientType());
        assertNotNull(session.getSessionId());
        assertFalse(session.getSessionId().isEmpty());
    }

    @Test
    public void testTwoSessionsHaveDifferentIds() {
        ClientSession s1 = new ClientSession("Alice", "Client");
        ClientSession s2 = new ClientSession("Bob", "Client");
        assertNotEquals(s1.getSessionId(), s2.getSessionId());
    }

    @Test
    public void testSessionIdIsValidUuid() {
        ClientSession session = new ClientSession("Test", "Client");
        String id = session.getSessionId();
        assertEquals(36, id.length());
        assertEquals('-', id.charAt(8));
        assertEquals('-', id.charAt(13));
        assertEquals('-', id.charAt(18));
        assertEquals('-', id.charAt(23));
    }
}
