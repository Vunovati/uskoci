package com.randombit.uskoci.rest.game;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement
public class GameResponse {
    public String text;
    public String author;
    public long time;

    public GameResponse(String author, String text) {
        this.author = author;
        this.text = text;
        this.time = new Date().getTime();
    }

    public GameResponse() {
    }
}
