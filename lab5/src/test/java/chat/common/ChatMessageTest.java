package chat.common;

import org.junit.Test;
import static org.junit.Assert.*;

public class ChatMessageTest {

    @Test
    public void testDefaultConstructor() {
        ChatMessage msg = new ChatMessage();
        assertNull(msg.getType());
        assertNull(msg.getName());
        assertNull(msg.getMessage());
        assertNull(msg.getSession());
    }

    @Test
    public void testConstructorWithType() {
        ChatMessage msg = new ChatMessage("login");
        assertEquals("login", msg.getType());
    }

    @Test
    public void testSettersAndGetters() {
        ChatMessage msg = new ChatMessage();
        msg.setType("message");
        msg.setName("Alice");
        msg.setMessage("Hello!");
        msg.setSession("abc-123");
        msg.setEvent("userlogin");

        assertEquals("message", msg.getType());
        assertEquals("Alice", msg.getName());
        assertEquals("Hello!", msg.getMessage());
        assertEquals("abc-123", msg.getSession());
        assertEquals("userlogin", msg.getEvent());
    }

    @Test
    public void testToString() {
        ChatMessage msg = new ChatMessage("error");
        msg.setName("Bob");
        String s = msg.toString();
        assertTrue(s.contains("error"));
        assertTrue(s.contains("Bob"));
    }
}
