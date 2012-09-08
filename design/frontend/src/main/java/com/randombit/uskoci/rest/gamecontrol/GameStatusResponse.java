package com.randombit.uskoci.rest.gamecontrol;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class GameStatusResponse {
    public String currentPlayerId;
    public List<String> playersCards;
    public List<String> playersResources;
    public List<String> discardedCards;
    public boolean beginningCardDrawn;
    public boolean gameStarted;
    public int numberOfPlayersJoined;
    public int currentPhase;

    public GameStatusResponse(String currentPlayerId, List<String> playersCards, List<String> playersResources, List<String> discardedCards, boolean beginningCardDrawn, boolean gameStarted, int numberOfPlayersJoined, int currentPhase) {
        this.currentPlayerId = currentPlayerId;
        this.playersCards = playersCards;
        this.playersResources = playersResources;
        this.discardedCards = discardedCards;
        this.beginningCardDrawn = beginningCardDrawn;
        this.gameStarted = gameStarted;
        this.numberOfPlayersJoined = numberOfPlayersJoined;
        this.currentPhase = currentPhase;
    }

    public GameStatusResponse() {
    }

}
