package com.randombit.uskoci.rest.gamecontrol;

import com.randombit.uskoci.card.model.Card;
import com.randombit.uskoci.game.ActionNotAllowedException;
import com.randombit.uskoci.game.GameController;
import com.randombit.uskoci.game.dao.SingletonGameControllerDB;

import java.util.ArrayList;
import java.util.List;

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
        // TODO: acces via factory
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

        gameResponse.playersCards = getPlayersCardIds(message, gameController);
        gameResponse.beginningCardDrawn = gameController.getBeginningCardDrawn();
        gameResponse.gameStarted = gameController.isGameStarted();
        gameResponse.numberOfPlayersJoined = gameController.getNumberOfPlayersJoined();
        gameResponse.currentPhase = gameController.getCurrentPhase();
        gameResponse.discardedCards = getDiscardedCardIds(gameController);
        return gameResponse;
    }

    private List<String> getPlayersCardIds(GameStatusMessage message, GameController gameController) {
        List<String> playerCardIds = new ArrayList<String>();

        for (Card cardInHand : gameController.getPlayerCards(Integer.valueOf(message.userId))) {
            playerCardIds.add(cardInHand.getId());
        }
        return playerCardIds;
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

/*
        if ("setnextturn".equals(action.toLowerCase()))
            gameController.setNextTurn(Integer.valueOf(message));
*/

        /* TODO: discard card from hand
        if ("discardfromhand".equals(action.toLowerCase()))
            gameController.setNextPhase(Integer.valueOf(message))*/

        if ("playcard".equals(action.toLowerCase()))
            if ("".equals(message.userId) && "".equals(message.cardId)) {
            gameController.playCard(Integer.valueOf(message.userId), Integer.valueOf(message.cardId));
        }
    }
}
