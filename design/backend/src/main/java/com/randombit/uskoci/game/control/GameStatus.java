package com.randombit.uskoci.game.control;

import com.randombit.uskoci.card.model.Card;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GameStatus {
    
    Map<String, List<Card>> playerCardMap;
    Map<String, List<Card>> playersResources;
    List<Card> cardDeck;
    List<Card> discardedCards;
    LinkedList<Card> cardStack;
    int currentPlayerId;
    int numberOfPlayersJoined;
    int chosenPlayer;
    boolean beginningCardDrawn;
    boolean gameStarted;
    boolean resourceCardPlayed;
    


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


    public Map<String, List<Card>> getPlayersResources() {
        return playersResources;
    }

    public void setPlayersResources(Map<String, List<Card>> playersResources) {
        this.playersResources = playersResources;
    }


    public List<Card> getCardStack() {
        return cardStack;
    }

    public void setCardStack(List<Card> cardStack) {
        this.cardStack = (LinkedList<Card>) cardStack;
    }

    public int getChosenPlayer() {
        return chosenPlayer;
    }

    public void setChosenPlayer(int chosenPlayer) {
        this.chosenPlayer = chosenPlayer;
    }

    public GameStatus() {
    }

    public List<Card> getPlayerCards(int playerId) {
        return getPlayerCardMap().get(String.valueOf(playerId));
    }
    
    public List<Card> getResources(int playerId) {
        List<Card> playerResources = getPlayersResources().get(String.valueOf(playerId));

        if (playerResources == null)
            playerResources = Collections.<Card>emptyList();
        return playerResources;
    }
}