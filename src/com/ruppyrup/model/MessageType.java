package com.ruppyrup.model;

public enum MessageType {
    INFO("Info"),
    MESSAGE("Message"),
    COMMAND("Command");

    private String type;

    MessageType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
