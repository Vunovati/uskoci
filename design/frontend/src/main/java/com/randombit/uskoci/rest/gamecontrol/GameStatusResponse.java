package com.randombit.uskoci.rest.gamecontrol;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class GameStatusResponse {
    public String currentPlayerId;
    public List<String> playersCards;
    public List<String> discardedCards;
    public List<String> playersResources;

    // TODO use hashmap if possible,
    public List<String> player1Resources;
    public List<String> player2Resources;
    public List<String> player3Resources;
    public List<String> player4Resources;
    //  dirty hack, Fix asap

    public boolean beginningCardDrawn;
    public boolean gameStarted;
    public int numberOfPlayersJoined;
    public List<String> playersPoints;
    public int currentPhase;

    public GameStatusResponse(String currentPlayerId, List<String> playersCards, List<String> discardedCards, List<String> playersResources, List<String> player1Resources, List<String> player2Resources, List<String> player3Resources, List<String> player4Resources, boolean beginningCardDrawn, boolean gameStarted, int numberOfPlayersJoined, List<String> playersPoints, int currentPhase) {
        this.currentPlayerId = currentPlayerId;
        this.playersCards = playersCards;
        this.discardedCards = discardedCards;
        this.playersResources = playersResources;
        this.player1Resources = player1Resources;
        this.player2Resources = player2Resources;
        this.player3Resources = player3Resources;
        this.player4Resources = player4Resources;
        this.beginningCardDrawn = beginningCardDrawn;
        this.gameStarted = gameStarted;
        this.numberOfPlayersJoined = numberOfPlayersJoined;
        this.playersPoints = playersPoints;
        this.currentPhase = currentPhase;
    }

    public GameStatusResponse() {
    }

}
