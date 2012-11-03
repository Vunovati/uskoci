package com.randombit.uskoci.rest.gamecontrol;

import com.randombit.uskoci.game.ActionNotAllowedException;
import com.randombit.uskoci.game.control.GameController;
import com.randombit.uskoci.game.dao.GameControllerPool;

/**
 * Interprets game messages into game control method calls
 */
public class GameControllerRestAdapterImpl implements GameControllerRestAdapter {

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

        for (ACTION supportedAction:ACTION.values()) {
             if (supportedAction.actionName.equals(action.toLowerCase())) {
                 supportedAction.perform(message, gameController);
             }
        }
    }

    public enum ACTION {
        DRAW_CARD("drawcard") {
            @Override
            public void perform(GameStatusMessage message, GameController gameController) throws ActionNotAllowedException {
                gameController.drawCard(Integer.valueOf(message.userId));
            }
        },
        SET_NEXT_TURN("setnextturn") {
            @Override
            public void perform(GameStatusMessage message, GameController gameController) throws ActionNotAllowedException {
                gameController.setNextPlayersTurn(Integer.valueOf(message.userId));
            }
        },
        DISCARD_FROM_HAND("discardfromhand") {
            @Override
            public void perform(GameStatusMessage message, GameController gameController) throws ActionNotAllowedException {
                gameController.discardCardFromPlayersHand(Integer.valueOf(message.cardId), Integer.valueOf(message.userId));
            }
        },
        DISCARD_FROM_RESOURCES("discardfromresources") {
            @Override
            public void perform(GameStatusMessage message, GameController gameController) throws ActionNotAllowedException {
                gameController.discardCardFromResourcePile(Integer.valueOf(message.cardId), Integer.valueOf(message.userId));
            }
        },
        PLAY_CARD("playcard") {
            @Override
            public void perform(GameStatusMessage message, GameController gameController) throws ActionNotAllowedException {
                if (userIdAndCardIdAreSent(message)) {
                    gameController.playCard(Integer.valueOf(message.userId), Integer.valueOf(message.cardId));
                }
            }
            private boolean userIdAndCardIdAreSent(GameStatusMessage message) {
                return !("".equals(message.userId) && "".equals(message.cardId));
            }
        },
        START_GAME("startgame"){
            @Override
            public void perform(GameStatusMessage message, GameController gameController) throws ActionNotAllowedException {
                gameController.startGame(4);
            }
        };

        private String actionName;

        private ACTION(String actionName) {
            this.actionName = actionName;
        }

        public void perform(GameStatusMessage message, GameController gameController) throws ActionNotAllowedException {
        }
    }
}
