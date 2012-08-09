package com.randombit.uskoci.rest.game;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GameMessage {
    public String author="";
    public String message="";

    public GameMessage() {
    }

    public GameMessage(String author, String message) {
        this.author = author;
        this.message = message;
    }
}
