package bothSides;

import java.io.Serializable;

public class ChatMessage implements Serializable {
    private String name;
    private String message;

    public ChatMessage(String name, String message){
        this.name = name;
        this.message = message;
    }

    //Getter/Setter
    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }
}

