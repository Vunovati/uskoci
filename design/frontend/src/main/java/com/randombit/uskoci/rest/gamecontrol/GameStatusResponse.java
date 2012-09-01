package com.randombit.uskoci.rest.gamecontrol;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class GameStatusResponse {
    public String currentPlayerId;
    public List<String> playersCards;
    public boolean beginningCardDrawn;
    public boolean gameStarted;
    public int numberOfPlayersJoined;
    public int currentPhase;

    public GameStatusResponse(String currentPlayerId, List<String> playersCards, boolean beginningCardDrawn, boolean gameStarted, int numberOfPlayersJoined, int currentPhase) {
        this.currentPlayerId = currentPlayerId;
        this.playersCards = playersCards;
        this.beginningCardDrawn = beginningCardDrawn;
        this.gameStarted = gameStarted;
        this.numberOfPlayersJoined = numberOfPlayersJoined;
        this.currentPhase = currentPhase;
    }

    public GameStatusResponse() {
    }

}
