package chat.serialized;

import chat.common.ChatMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializeProtocol {

    public static void send(ObjectOutputStream out, ChatMessage msg) throws IOException {
        out.writeObject(msg);
        out.reset();
        out.flush();
    }

    public static ChatMessage receive(ObjectInputStream in) throws IOException {
        try {
            return (ChatMessage) in.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("Неизвестный класс: " + e.getMessage());
        }
    }
}
