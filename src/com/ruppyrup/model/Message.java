package com.ruppyrup.model;

import java.io.Serializable;

public class Message implements Serializable {
    MessageType messageType;
    String messsage;

    public Message(MessageType messageType, String messsage) {
        this.messageType = messageType;
        this.messsage = messsage;
    }

    @Override
    public String toString() {
        return "Message{" +
            "messageType=" + messageType +
            ", messsage='" + messsage + '\'' +
            '}';
    }
}
