package com.randombit.uskoci.rest.gamecontrol;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GameStatusResponse {
    public String playerOnTheMove;

    public GameStatusResponse() {
    }

    public GameStatusResponse(String playerOnTheMove) {
        this.playerOnTheMove = playerOnTheMove;
    }
}
