package com.randombit.uskoci.rest.gamecontrol;

import com.randombit.uskoci.card.model.Card;
import com.randombit.uskoci.game.control.GameController;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashMap;
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
