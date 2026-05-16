package chat.common;

import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


public class JsonProtocol {

    private static final Gson GSON = new Gson();


    public static void send(DataOutputStream out, ChatMessage msg) throws IOException {
        byte[] json = GSON.toJson(msg).getBytes(StandardCharsets.UTF_8);
        out.writeInt(json.length);
        out.write(json);
        out.flush();
    }

    public static ChatMessage receive(DataInputStream in) throws IOException {
        int length = in.readInt();
        if (length <= 0 || length > 1000000) {
            throw new IOException("Неверная длина пакета: " + length);
        }
        byte[] json = new byte[length];
        in.readFully(json);
        return GSON.fromJson(new String(json, StandardCharsets.UTF_8), ChatMessage.class);
    }


    public static String toJson(ChatMessage msg) {
        return GSON.toJson(msg);
    }

    public static ChatMessage fromJson(String json) {
        return GSON.fromJson(json, ChatMessage.class);
    }
}
