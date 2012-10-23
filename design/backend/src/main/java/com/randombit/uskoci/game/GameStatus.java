package com.randombit.uskoci.game;

import com.randombit.uskoci.card.model.Card;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GameStatus {
    List<Card> cardDeck;

    public List<Card> getCardDeck() {
        return cardDeck;
    }

    public void setCardDeck(List<Card> cardDeck) {
        this.cardDeck = cardDeck;
    }

    List<Card> discardedCards;

    public List<Card> getDiscardedCards() {
        return discardedCards;
    }

    public void setDiscardedCards(List<Card> discardedCards) {
        this.discardedCards = discardedCards;
    }

    Map<String, List<Card>> playerCardMap;

    public Map<String, List<Card>> getPlayerCardMap() {
        return playerCardMap;
    }

    public void setPlayerCardMap(Map<String, List<Card>> playerCardMap) {
        this.playerCardMap = playerCardMap;
    }

    int currentPlayerId;

    public int getCurrentPlayerId() {
        return currentPlayerId;
    }

    public void setCurrentPlayerId(int currentPlayerId) {
        this.currentPlayerId = currentPlayerId;
    }

    boolean beginningCardDrawn;

    public boolean isBeginningCardDrawn() {
        return beginningCardDrawn;
    }

    public void setBeginningCardDrawn(boolean beginningCardDrawn) {
        this.beginningCardDrawn = beginningCardDrawn;
    }

    boolean gameStarted;

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }

    int numberOfPlayersJoined;

    public int getNumberOfPlayersJoined() {
        return numberOfPlayersJoined;
    }

    public void setNumberOfPlayersJoined(int numberOfPlayersJoined) {
        this.numberOfPlayersJoined = numberOfPlayersJoined;
    }

    boolean resourceCardPlayed;

    public boolean isResourceCardPlayed() {
        return resourceCardPlayed;
    }

    public void setResourceCardPlayed(boolean resourceCardPlayed) {
        this.resourceCardPlayed = resourceCardPlayed;
    }

    Map<String, List<Card>> playersResources;

    public Map<String, List<Card>> getPlayersResources() {
        return playersResources;
    }

    public void setPlayersResources(Map<String, List<Card>> playersResources) {
        this.playersResources = playersResources;
    }

    LinkedList<Card> cardStack;

    public List<Card> getCardStack() {
        return cardStack;
    }

    public void setCardStack(List<Card> cardStack) {
        this.cardStack = (LinkedList<Card>) cardStack;
    }

    int chosenPlayer;

    public int getChosenPlayer() {
        return chosenPlayer;
    }

    public void setChosenPlayer(int chosenPlayer) {
        this.chosenPlayer = chosenPlayer;
    }

    public GameStatus() {
    }
}