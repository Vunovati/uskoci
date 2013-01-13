package com.randombit.uskoci.game.control;

import com.randombit.uskoci.card.model.Card;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GameStatus {
    List<Card> cardDeck;
    List<Card> discardedCards;
    Map<String, List<Card>> playerCardMap;
    boolean beginningCardDrawn;
    int currentPlayerId;
    boolean gameStarted;
    int numberOfPlayersJoined;
    boolean resourceCardPlayed;
    Map<String, ResourcePile> playersResources;
    LinkedList<Card> cardStack;

    public List<Card> getCardDeck() {
        return cardDeck;
    }

    public void setCardDeck(List<Card> cardDeck) {
        this.cardDeck = cardDeck;
    }

    public List<Card> getDiscardedCards() {
        return discardedCards;
    }

    public void setDiscardedCards(List<Card> discardedCards) {
        this.discardedCards = discardedCards;
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

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }

    public int getNumberOfPlayersJoined() {
        return numberOfPlayersJoined;
    }

    public void setNumberOfPlayersJoined(int numberOfPlayersJoined) {
        this.numberOfPlayersJoined = numberOfPlayersJoined;
    }

    public boolean isResourceCardPlayed() {
        return resourceCardPlayed;
    }

    public void setResourceCardPlayed(boolean resourceCardPlayed) {
        this.resourceCardPlayed = resourceCardPlayed;
    }

    public Map<String, ResourcePile> getPlayersResources() {
        return playersResources;
    }
    
    public ResourcePile getPlayersResources(String playerId){
    	return playersResources.get(playerId);
    }

    public void setPlayersResources(Map<String, ResourcePile> playersResources) {
        this.playersResources = playersResources;
    }

    public List<Card> getCardStack() {
        return cardStack;
    }

    public void setCardStack(List<Card> cardStack) {
        this.cardStack = (LinkedList<Card>) cardStack;
    }

    public GameStatus() {
    }
}