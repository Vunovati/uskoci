package com.randombit.uskoci.game.control;

import com.randombit.uskoci.card.model.Card;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameState {
    private List<Card> cardDeck;
    private Map<String, List<Card>> playerCardMap = new HashMap<String, List<Card>>();
    private int currentPlayerId;
    private boolean beginningCardDrawn = false;
    private int numberOfPlayersJoined;
    private int currentPhase;

    public List<Card> getCardDeck() {
        return cardDeck;
    }

    public void setCardDeck(List<Card> cardDeck) {
        this.cardDeck = cardDeck;
    }

    public Map<String, List<Card>> getPlayerCardMap() {
        return playerCardMap;
    }

    public void setPlayerCardMap(Map<String, List<Card>> playerCardMap) {
        this.playerCardMap = playerCardMap;
    }

    public int getCurrentPlayerId() {
        return currentPlayerId;
    }

    public void setCurrentPlayerId(int currentPlayerId) {
        this.currentPlayerId = currentPlayerId;
    }

    public boolean isBeginningCardDrawn() {
        return beginningCardDrawn;
    }

    public void setBeginningCardDrawn(boolean beginningCardDrawn) {
        this.beginningCardDrawn = beginningCardDrawn;
    }

    public int getNumberOfPlayersJoined() {
        return numberOfPlayersJoined;
    }

    public void setNumberOfPlayersJoined(int numberOfPlayersJoined) {
        this.numberOfPlayersJoined = numberOfPlayersJoined;
    }

    public int getCurrentPhase() {
        return currentPhase;
    }

    public void setCurrentPhase(int currentPhase) {
        this.currentPhase = currentPhase;
    }
}
