package com.randombit.uskoci.rest.gamecontrol;

import com.randombit.uskoci.card.model.Card;
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

        // TODO: acces via factory

        GameController gameController = SingletonGameControllerDB.instance.getAllControllers().get(0);
        if (!gameController.isGameStarted()) {
            gameController.startGame(4);
        }

        String action = message.action;

/*
        switch (action.toLowerCase()) {

            case "drawcard":
                gameController.drawCard(Integer.valueOf(message.userId));
                break;
        }
        */

        if ("drawcard".equals(action.toLowerCase())) {
            gameController.drawCard(Integer.valueOf(message.userId));
        }

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
}
