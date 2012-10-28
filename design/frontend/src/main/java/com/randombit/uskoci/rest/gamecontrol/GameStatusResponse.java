package com.randombit.uskoci.rest.gamecontrol;

import com.randombit.uskoci.card.model.Card;
import com.randombit.uskoci.game.control.GameController;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlRootElement
public class GameStatusResponse {
    private boolean gameStarted;
    private int numberOfPlayersJoined;
    private String currentPlayerId;
    private boolean beginningCardDrawn;

    @XmlElement
    public boolean isGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }

    @XmlElement
    public int getNumberOfPlayersJoined() {
        return numberOfPlayersJoined;
    }

    public void setNumberOfPlayersJoined(int numberOfPlayersJoined) {
        this.numberOfPlayersJoined = numberOfPlayersJoined;
    }

    @XmlElement
    public String getCurrentPlayerId() {
        return currentPlayerId;
    }

    public void setCurrentPlayerId(String currentPlayerId) {
        this.currentPlayerId = currentPlayerId;
    }

    @XmlElement
    public boolean isBeginningCardDrawn() {
        return beginningCardDrawn;
    }

    public void setBeginningCardDrawn(boolean beginningCardDrawn) {
        this.beginningCardDrawn = beginningCardDrawn;
    }

    @XmlElement
    public boolean isResourceCardPlayed() {
        return resourceCardPlayed;
    }

    public void setResourceCardPlayed(boolean resourceCardPlayed) {
        this.resourceCardPlayed = resourceCardPlayed;
    }

    @XmlElement
    public List<String> getDiscardedCards() {
        return discardedCards;
    }

    public void setDiscardedCards(List<String> discardedCards) {
        this.discardedCards = discardedCards;
    }

    @XmlElement
    public Map<String, List<String>> getPlayersCards() {
        return playersCards;
    }

    public void setPlayersCards(Map<String, List<String>> playersCards) {
        this.playersCards = playersCards;
    }

    @XmlElement
    public Map<String, List<String>> getPlayersResources() {
        return playersResources;
    }

    public void setPlayersResources(Map<String, List<String>> playersResources) {
        this.playersResources = playersResources;
    }

    @XmlElement
    public List<String> getPlayersPoints() {
        return playersPoints;
    }

    public void setPlayersPoints(List<String> playersPoints) {
        this.playersPoints = playersPoints;
    }

    @XmlElement
    public GameStatusMessage getLastAction() {
        return lastAction;
    }

    public void setLastAction(GameStatusMessage lastAction) {
        this.lastAction = lastAction;
    }

    @XmlElement
    public String getActionStatus() {
        return actionStatus;
    }

    public void setActionStatus(String actionStatus) {
        this.actionStatus = actionStatus;
    }

    private boolean resourceCardPlayed;
    private List<String> discardedCards;
    private Map<String, List<String>> playersCards;
    private Map<String, List<String>> playersResources;
    private List<String> playersPoints;
    private GameStatusMessage lastAction;
    private String actionStatus;

    public GameStatusResponse() {
    }

    public GameStatusResponse(GameController gameController) {
        this.gameStarted = gameController.isGameStarted();
        this.numberOfPlayersJoined = gameController.getNumberOfPlayersJoined();
        this.currentPlayerId = String.valueOf(gameController.getCurrentPlayerId());
        this.beginningCardDrawn = gameController.getBeginningCardDrawn();
        this.resourceCardPlayed = gameController.isResourceCardPlayed();
        this.discardedCards = getDiscardedCardIds(gameController);
        this.playersResources = getPlayersResourceIdMap(gameController);
        this.playersCards = getPlayersCardIdMap(gameController);
        this.playersPoints = getPlayersPoints(gameController);
    }

    private List<String> getPlayersPoints(GameController gameController) {
        List<String> playerPoints = new ArrayList<String>();

        for (int playerId=1; playerId<=4; playerId++) {
            playerPoints.add(String.valueOf(gameController.getPlayersPoints(playerId)));
        }

        return playerPoints;
    }

    private Map<String, List<String>> getPlayersResourceIdMap(GameController gameController) {
        Map<String, List<String>> playersResourceIdMap = new HashMap<String, List<String>>();

        int numberOfPlayersJoined = gameController.getNumberOfPlayersJoined();
        for (int playerId = 1; playerId <= numberOfPlayersJoined; playerId++) {

            playersResourceIdMap.put(String.valueOf(playerId), getCardIds(gameController.getResources(playerId)));
        }

        return playersResourceIdMap;
    }

    private Map<String, List<String>> getPlayersCardIdMap(GameController gameController) {
        Map<String, List<String>> playersResourceIdMap = new HashMap<String, List<String>>();

        int numberOfPlayersJoined = gameController.getNumberOfPlayersJoined();
        for (int playerId = 1; playerId <= numberOfPlayersJoined; playerId++) {
            playersResourceIdMap.put(String.valueOf(playerId), getCardIds(gameController.getPlayerCards(playerId)));
        }

        return playersResourceIdMap;
    }

    private List<String> getCardIds(List<Card> cards) {
        List<String> cardIds = new ArrayList<String>();

        for (Card card : cards) {
            cardIds.add(card.getId());
        }
        return cardIds;
    }

    private List<String> getDiscardedCardIds(GameController gameController) {
        List<String> discardedCards = new ArrayList<String>();
        for (Card card: gameController.getDiscardPile())
        {
            discardedCards.add(card.getId());
        }
        return discardedCards;
    }
}
