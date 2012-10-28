package com.randombit.uskoci.rest.gamecontrol;

import com.randombit.uskoci.game.ActionNotAllowedException;
import com.randombit.uskoci.game.control.GameController;
import com.randombit.uskoci.game.dao.GameControllerPool;

/**
 * Interprets game messages into game control method calls
 */
public class GameControllerRestAdapterImpl implements GameControllerRestAdapter {

    public static final String DRAW_CARD = "drawcard";
    public static final String SET_NEXT_TURN = "setnextturn";
    public static final String DISCARD_FROM_HAND = "discardfromhand";
    public static final String DISCARD_FROM_RESOURCES = "discardfromresources";
    public static final String PLAY_CARD = "playcard";
    public static final String START_GAME = "startgame";
    public static final String OK = "OK";


    // TODO: add logs
    @Override
    public GameStatusResponse getResponse(GameStatusMessage message, String gameId) {
        GameController gameController = getGameController();
        GameStatusResponse gameResponse;
        try {
            gameResponse = makeMove(message, gameController);
            gameResponse.setActionStatus(OK);
        } catch (ActionNotAllowedException e) {
            e.printStackTrace();
            gameResponse = new GameStatusResponse(gameController);
            gameResponse.setActionStatus(e.getMessage());
        }
        gameResponse.setLastAction(message);
        return gameResponse;
    }

    private GameController getGameController() {
        return GameControllerPool.instance.getController(1);
    }

    private GameStatusResponse makeMove(GameStatusMessage message, GameController gameController) throws ActionNotAllowedException{
        changeGameStateWithAction(gameController, message);
        return new GameStatusResponse(gameController);
    }

    private void changeGameStateWithAction(GameController gameController, GameStatusMessage message) throws ActionNotAllowedException {
        String action = message.action;

        if (START_GAME.equals((action.toLowerCase()))) {
            gameController.startGame(4);
        }

        if (DRAW_CARD.equals(action.toLowerCase())) {
            gameController.drawCard(Integer.valueOf(message.userId));
        }

        if (SET_NEXT_TURN.equals(action.toLowerCase())) {
            gameController.setNextPlayersTurn(Integer.valueOf(message.userId));
        }

        if (DISCARD_FROM_HAND.equals(action.toLowerCase())) {
            gameController.discardCardFromPlayersHand(Integer.valueOf(message.cardId), Integer.valueOf(message.userId));
        }

        if (DISCARD_FROM_RESOURCES.equals(action.toLowerCase())) {
            gameController.discardCardFromResourcePile(Integer.valueOf(message.cardId), Integer.valueOf(message.userId));
        }

        if (PLAY_CARD.equals(action.toLowerCase())) {
            if (userIdAndCardIdAreSent(message)) {
                gameController.playCard(Integer.valueOf(message.userId), Integer.valueOf(message.cardId));
            }
        }
    }

    private boolean userIdAndCardIdAreSent(GameStatusMessage message) {
        return !("".equals(message.userId) && "".equals(message.cardId));
    }
}
