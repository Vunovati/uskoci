package com.randombit.uskoci.rest.gamecontrol;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Map;

@XmlRootElement
public class GameStatusResponse {
    public boolean gameStarted;
    public int numberOfPlayersJoined;
    public String currentPlayerId;
    public boolean beginningCardDrawn;
    public boolean resourceCardPlayed;
    public List<String> discardedCards;
    public Map<String, List<String>> playersCards;
    public Map<String, List<String>> playersResources;
    public List<String> playersPoints;
    public GameStatusMessage lastAction;
    public String actionStatus;

    public GameStatusResponse() {
    }

    public GameStatusResponse(boolean gameStarted, int numberOfPlayersJoined, String currentPlayerId, boolean beginningCardDrawn, boolean resourceCardPlayed, List<String> discardedCards, Map<String, List<String>> playersCards, Map<String, List<String>> playersResources, List<String> playersPoints, GameStatusMessage lastAction, String actionStatus) {
        this.gameStarted = gameStarted;
        this.numberOfPlayersJoined = numberOfPlayersJoined;
        this.currentPlayerId = currentPlayerId;
        this.beginningCardDrawn = beginningCardDrawn;
        this.resourceCardPlayed = resourceCardPlayed;
        this.discardedCards = discardedCards;
        this.playersCards = playersCards;
        this.playersResources = playersResources;
        this.playersPoints = playersPoints;
        this.lastAction = lastAction;
        this.actionStatus = actionStatus;
    }

}
