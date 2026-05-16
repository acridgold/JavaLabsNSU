package chat.common;

import java.io.Serializable;

public class ChatMessage implements Serializable {

    private String type;     // тип сообщения
    private String name;     // имя
    private String message;  // текст
    private String session;  // id сессии
    private String event;    // "userlogin", "userlogout", "message"

    public ChatMessage() {}

    public ChatMessage(String type) {
        this.type = type;
    }

    public String getType()    { return type; }
    public String getName()    { return name; }
    public String getMessage() { return message; }
    public String getSession() { return session; }
    public String getEvent()   { return event; }

    public void setType(String type)       { this.type = type; }
    public void setName(String name)       { this.name = name; }
    public void setMessage(String message) { this.message = message; }
    public void setSession(String session) { this.session = session; }
    public void setEvent(String event)     { this.event = event; }

    @Override
    public String toString() {
        return "ChatMessage{type='" + type + "', name='" + name +
               "', message='" + message + "', session='" + session + "'}";
    }
}
