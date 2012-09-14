package com.randombit.uskoci.rest.gamecontrol;

import com.randombit.uskoci.card.model.Card;
import com.randombit.uskoci.game.ActionNotAllowedException;
import com.randombit.uskoci.game.GameController;
import com.randombit.uskoci.game.dao.SingletonGameControllerDB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Interprets game messages into game control method calls
 */
public class GameControllerRestAdapterImpl implements GameControllerRestAdapter {
    @Override
    public GameStatusResponse getResponse(GameStatusMessage message, String gameId) {
        GameController gameController = getGameController();
        GameStatusResponse gameResponse;
        try {
            gameResponse = makeMove(message, gameController);
        } catch (ActionNotAllowedException e) {
            e.printStackTrace();
            gameResponse = new UnsupportedActionResponse();
        }
        return gameResponse;
    }

    private GameController getGameController() {
        // TODO: acces the real database DAO
        GameController gameController = SingletonGameControllerDB.instance.getAllControllers().get(0);
        if (!gameController.isGameStarted()) {
            gameController.startGame(4);
        }
        return gameController;
    }

    private GameStatusResponse makeMove(GameStatusMessage message, GameController gameController) throws ActionNotAllowedException{
        changeGameStateWithAction(gameController, message);
        return buildGameResponse(message, gameController);
    }

    private GameStatusResponse buildGameResponse(GameStatusMessage message, GameController gameController) {
        GameStatusResponse gameResponse = new GameStatusResponse();
        gameResponse.currentPlayerId = String.valueOf(gameController.getCurrentPlayerId());

        gameResponse.beginningCardDrawn = gameController.getBeginningCardDrawn();
        gameResponse.gameStarted = gameController.isGameStarted();
        gameResponse.numberOfPlayersJoined = gameController.getNumberOfPlayersJoined();
        gameResponse.currentPhase = gameController.getCurrentPhase();
        gameResponse.discardedCards = getDiscardedCardIds(gameController);
        gameResponse.playersResources = getPlayersResourceIdMap(gameController);
        gameResponse.playersCards = getPlayersCardIdMap(gameController);
        gameResponse.playersPoints = getPlayersPoints(gameController);
        return gameResponse;
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

    private void changeGameStateWithAction(GameController gameController, GameStatusMessage message) throws ActionNotAllowedException {
        String action = message.action;

        if ("drawcard".equals(action.toLowerCase()))
            gameController.drawCard(Integer.valueOf(message.userId));

        if ("setnextturn".equals(action.toLowerCase()))
            gameController.setNextPlayersTurn(Integer.valueOf(message.userId));

        /* TODO: discard card from hand
        if ("discardfromhand".equals(action.toLowerCase()))
            gameController.setNextPhase(Integer.valueOf(message))*/

        if ("playcard".equals(action.toLowerCase()))
            if (!("".equals(message.userId) && "".equals(message.cardId))) {
                gameController.playCard(Integer.valueOf(message.userId), Integer.valueOf(message.cardId));
            }
    }
}
