package com.randombit.uskoci.rest.gamecontrol;

import javax.xml.bind.annotation.XmlRootElement;


public class GameStatusMessage {
    public String userId = "";
    public String action = "";

    public GameStatusMessage() {
    }

    public GameStatusMessage(String userId, String action) {
        this.userId = userId;
        this.action = action;
    }
}
