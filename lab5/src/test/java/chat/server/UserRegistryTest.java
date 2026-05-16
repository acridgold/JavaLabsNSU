package chat.server;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;

public class UserRegistryTest {

    private UserRegistry registry;

    @Before
    public void setUp() {
        registry = new UserRegistry();
    }

    @Test
    public void testAddAndFindBySession() {
        ClientSession session = new ClientSession("Alice", "JavaClient");
        registry.add(session);

        ClientSession found = registry.findBySession(session.getSessionId());
        assertNotNull(found);
        assertEquals("Alice", found.getName());
    }

    @Test
    public void testFindBySessionNotFound() {
        assertNull(registry.findBySession("nonexistent-id"));
    }

    @Test
    public void testRemove() {
        ClientSession session = new ClientSession("Bob", "JavaClient");
        registry.add(session);
        registry.remove(session.getSessionId());
        assertNull(registry.findBySession(session.getSessionId()));
    }

    @Test
    public void testIsNameTaken() {
        registry.add(new ClientSession("Charlie", "Client"));
        assertTrue(registry.isNameTaken("Charlie"));
        assertFalse(registry.isNameTaken("Dave"));
    }

    @Test
    public void testGetAll() {
        registry.add(new ClientSession("Alice", "Client"));
        registry.add(new ClientSession("Bob", "Client"));
        List<ClientSession> all = registry.getAll();
        assertEquals(2, all.size());
    }

    @Test
    public void testEmptyRegistry() {
        assertTrue(registry.getAll().isEmpty());
    }
}
