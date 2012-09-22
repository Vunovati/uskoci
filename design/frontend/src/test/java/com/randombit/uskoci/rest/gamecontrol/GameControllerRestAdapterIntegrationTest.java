package com.randombit.uskoci.rest.gamecontrol;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

public class GameControllerRestAdapterIntegrationTest {
    GameControllerRestAdapter restAdapter;

    @Before
    public void setUp() throws Exception {
        restAdapter = new GameControllerRestAdapterImpl();
    }

    @Test
    public void test4TurnsFor4Players() {
        GameStatusResponse gameStatus = startGameFor4();
        for (int i = 0; i < 16; i++) {
            gameStatus = playerDrawsACard(gameStatus);
            gameStatus = playerPlaysACard(gameStatus);
            gameStatus = playerSetsNextTurn(gameStatus);
        }
    }

    @Test
    public void gameFor4PlayersIntegrationTest() throws Exception {
        GameStatusResponse gameStatus = startGameFor4();
        gameStatus = playerDrawsACard(gameStatus);
        gameStatus = playerPlaysACard(gameStatus);
        gameStatus = playerSetsNextTurn(gameStatus);
    }

    private GameStatusResponse playerDrawsACard(GameStatusResponse gameStatus) {
        String playerId = gameStatus.currentPlayerId;
        GameStatusMessage gameMessage = new GameStatusMessage(playerId, "drawcard", "", "0");
        return getResponse(gameMessage);
//
    }

    private GameStatusResponse playerPlaysACard(GameStatusResponse gameStatus) {
        String playerId = gameStatus.currentPlayerId;
        String cardToBePlayed = gameStatus.playersCards.get(playerId).get(0);
        GameStatusMessage gameMessage = new GameStatusMessage(playerId, "playcard", cardToBePlayed, "0");

        GameStatusResponse newStatus = getResponse(gameMessage);
        Assert.assertFalse("Players hand does not contain the card that has been played", newStatus.playersCards.get(playerId).contains(cardToBePlayed));
        return newStatus;
    }

    private GameStatusResponse playerSetsNextTurn(GameStatusResponse gameStatus) {
        String playerId = gameStatus.currentPlayerId;
        GameStatusMessage gameMessage = new GameStatusMessage(playerId, "setnextturn", "", "0");

        GameStatusResponse newStatus = getResponse(gameMessage);
        Assert.assertFalse("Player is no longer on the move", playerId.equals(newStatus.currentPlayerId));

        return newStatus;
    }

    private GameStatusResponse getResponse(GameStatusMessage gameMessage) {
        GameStatusResponse newGameStatus = restAdapter.getResponse(gameMessage, "0");
        responseIsValid(gameMessage, newGameStatus);
        return newGameStatus;
    }

    private GameStatusResponse startGameFor4() {
        GameStatusMessage gameMessage = new GameStatusMessage("1", "startgame", "", "0");
        GameStatusResponse gameStatusResponse = restAdapter.getResponse(gameMessage, "0");

        responseIsValid(gameMessage, gameStatusResponse);
        Assert.assertTrue("Game is started", gameStatusResponse.gameStarted);

        return gameStatusResponse;
    }

    private void responseIsValid(GameStatusMessage gameMessage, GameStatusResponse gameStatusResponse) {
        Assert.assertNotNull("response is sent", gameStatusResponse);
        lastMessageIsResentInResponse(gameMessage, gameStatusResponse);
        responseIsNotEmpty(gameStatusResponse);
    }

    private void responseIsNotEmpty(GameStatusResponse gameStatusResponse) {
        Assert.assertFalse("Current player id is sent", "".equals(gameStatusResponse.currentPlayerId));
    }

    private void lastMessageIsResentInResponse(GameStatusMessage testGameStatusMessage, GameStatusResponse gameStatusResponse) {
        Assert.assertEquals("Last action is resent in response", testGameStatusMessage, gameStatusResponse.lastAction);
    }
}
