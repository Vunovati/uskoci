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

        GameStatusResponse gameResponse = buildGameResponse(message, gameController);
        return gameResponse;
    }

    private GameStatusResponse buildGameResponse(GameStatusMessage message, GameController gameController) {
        GameStatusResponse gameResponse = new GameStatusResponse();
        gameResponse.currentPlayerId = String.valueOf(gameController.getCurrentPlayerId());

        List<String> playerCardIds = new ArrayList<String>();

        for (Card cardInHand : gameController.getPlayerCards(Integer.valueOf(message.userId))) {
            playerCardIds.add(cardInHand.getId());
        }
        gameResponse.playersCards = playerCardIds;
        gameResponse.beginningCardDrawn = gameController.getBeginningCardDrawn();
        gameResponse.gameStarted = gameController.isGameStarted();
        gameResponse.numberOfPlayersJoined = gameController.getNumberOfPlayersJoined();
        gameResponse.currentPhase = gameController.getCurrentPhase();
        return gameResponse;
    }

    private void changeGameStateWithAction(GameController gameController, GameStatusMessage message) throws ActionNotAllowedException {
        String action = message.action;

        if ("drawcard".equals(action.toLowerCase()))
            gameController.drawCard(Integer.valueOf(message.userId));
        /* TODO: check with user id
        if ("setnextphase".equals(action.toLowerCase()))
            gameController.setNextPhase(Integer.valueOf(message))*/

        /* TODO: implement in game controller
        if ("playcard".equals(action.toLowerCase()))
            gameController.playCard(message.userId, message.cardId);*/
    }
}
