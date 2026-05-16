package chat.common;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class JsonProtocolTest {

    @Test
    public void testToJsonAndFromJson() throws Exception {
        ChatMessage original = new ChatMessage("login");
        original.setName("Alice");

        String json = JsonProtocol.toJson(original);
        assertTrue(json.contains("login"));
        assertTrue(json.contains("Alice"));

        ChatMessage parsed = JsonProtocol.fromJson(json);
        assertEquals("login", parsed.getType());
        assertEquals("Alice", parsed.getName());
    }

    @Test
    public void testSendAndReceive() throws Exception {
        ChatMessage original = new ChatMessage("message");
        original.setName("Bob");
        original.setMessage("Hello World!");
        original.setSession("session-42");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonProtocol.send(new DataOutputStream(baos), original);

        ChatMessage received = JsonProtocol.receive(
            new DataInputStream(new ByteArrayInputStream(baos.toByteArray()))
        );

        assertEquals("message", received.getType());
        assertEquals("Bob", received.getName());
        assertEquals("Hello World!", received.getMessage());
        assertEquals("session-42", received.getSession());
    }

    @Test
    public void testSendMultipleMessages() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        ChatMessage msg1 = new ChatMessage("login");
        msg1.setName("Alice");
        ChatMessage msg2 = new ChatMessage("logout");
        msg2.setName("Bob");

        JsonProtocol.send(dos, msg1);
        JsonProtocol.send(dos, msg2);

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));

        ChatMessage r1 = JsonProtocol.receive(dis);
        ChatMessage r2 = JsonProtocol.receive(dis);

        assertEquals("login", r1.getType());
        assertEquals("Alice", r1.getName());
        assertEquals("logout", r2.getType());
        assertEquals("Bob", r2.getName());
    }

    @Test
    public void testMessageWithNullFields() throws Exception {
        ChatMessage msg = new ChatMessage("success");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonProtocol.send(new DataOutputStream(baos), msg);

        ChatMessage received = JsonProtocol.receive(
            new DataInputStream(new ByteArrayInputStream(baos.toByteArray()))
        );

        assertEquals("success", received.getType());
        assertNull(received.getName());
        assertNull(received.getMessage());
    }

    @Test(expected = Exception.class)
    public void testInvalidLengthThrowsException() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(-1);
        dos.flush();

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        JsonProtocol.receive(dis);
    }
}
