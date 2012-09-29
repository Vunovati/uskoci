package com.randombit.uskoci.rest.gamecontrol;

import com.randombit.uskoci.card.model.Card;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class GameControllerRestAdapterIntegrationTest {
    GameControllerRestAdapter restAdapter;

    @Before
    public void setUp() throws Exception {
        restAdapter = new GameControllerRestAdapterImpl();
    }

    @Test
    public void test4TurnsFor4Players() {
        GameStatusResponse gameStatus = startGameFor4();
        playFourTurns(gameStatus);
    }

    private GameStatusResponse playFourTurns(GameStatusResponse gameStatus) {
        for (int i = 0; i < 16; i++) {
            gameStatus = playerDrawsACard(gameStatus);
            gameStatus = playerPlaysACard(gameStatus);
            gameStatus = playerSetsNextTurn(gameStatus);
        }
        return gameStatus;
    }

    @Test
    public void gameFor4PlayersIntegrationTest() throws Exception {
        GameStatusResponse gameStatus = startGameFor4();
        gameStatus = playerDrawsACard(gameStatus);
        gameStatus = playerPlaysACard(gameStatus);
        gameStatus = playerSetsNextTurn(gameStatus);
    }

    @Test
    public void testDiscardCardFromHandInterface() throws Exception {
        GameStatusResponse gameStatus = startGameFor4();
        String testPlayerId = gameStatus.currentPlayerId;

        String cardToBeDiscarded = getOneCardIdFromHand(gameStatus, testPlayerId);

        GameStatusMessage gameMessage = new GameStatusMessage(testPlayerId, "discardfromhand", cardToBeDiscarded, "0");
        GameStatusResponse response = getResponse(gameMessage);
        List<String> testPlayersCardIds = response.playersCards.get(testPlayerId);
        Assert.assertFalse("Card is no longer in hand", testPlayersCardIds.contains(cardToBeDiscarded));
    }

    private String getOneCardIdFromHand(GameStatusResponse gameStatus, String testPlayerId) {
        List<String> testPlayersCards = gameStatus.playersCards.get(testPlayerId);
        return testPlayersCards.get(0);
    }

    @Test
    public void testDiscardCardFromResourcesInterface() throws Exception {
        GameStatusResponse gameStatus = startGameFor4();
        String testPlayerId = gameStatus.currentPlayerId;

        gameStatus = playFourTurns(gameStatus);

        String cardToBeDiscarded = getOneResourceCardId(gameStatus, testPlayerId);

        GameStatusMessage gameMessage = new GameStatusMessage(testPlayerId, "discardfromresources", cardToBeDiscarded, "0");
        GameStatusResponse response = getResponse(gameMessage);
        List<String> testPlayersResourceCardIds = response.playersResources.get(testPlayerId);
        Assert.assertFalse("Card is no longer in players resources", testPlayersResourceCardIds.contains(cardToBeDiscarded));
    }

    private String getOneResourceCardId(GameStatusResponse gameStatus, String testPlayerId) {
        List<String> testPlayersResources = gameStatus.playersResources.get(testPlayerId);
        return testPlayersResources.get(0);
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
