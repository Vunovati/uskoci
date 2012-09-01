package com.randombit.uskoci.rest.gamecontrol;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GameStatusMessage {
    public String userId = "";
    public String action = "";
    public String cardId = "";
    public String gameId = "";

    public GameStatusMessage() {
    }

    public GameStatusMessage(String userId, String action, String cardId, String gameId) {
        this.userId = userId;
        this.action = action;
        this.cardId = cardId;
        this.gameId = gameId;
    }
}
