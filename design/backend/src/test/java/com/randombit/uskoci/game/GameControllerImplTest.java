package com.randombit.uskoci.game;

import com.randombit.uskoci.card.model.Card;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

public class GameControllerImplTest {

    // TODO create basic tests
    GameController gameController = new GameControllerImpl();

    @Before
    public void setUp() throws Exception {
        gameController.resetGame();
    }

    @Test
    public void testCurrentPlayerId() throws Exception {
        Assert.assertEquals(1, gameController.getCurrentPlayerId());
    }

    @Test
    public void testNextPlayer() throws Exception {
        gameController.setNextPlayer();
        Assert.assertEquals(2, gameController.getCurrentPlayerId());
    }

    @Test
    public void testResetGame() throws Exception {
        gameController.setNextPlayer();
        gameController.resetGame();
        Assert.assertEquals(1, gameController.getCurrentPlayerId());
    }

    @Test
    public void testGetCardsOnTheTable() throws Exception {

        List<Card> expectedCards = Collections.<Card>emptyList();
        Assert.assertEquals(expectedCards, gameController.getCardsOnTheTable());
    }

    @Test
    public void testPutCardOnTheTable() throws Exception {

        int testCardId = 2;
        gameController.putCardOnTheTable(testCardId);

        List<Card> cardsOnTheTable = gameController.getCardsOnTheTable();
        Assert.assertFalse("Card size", cardsOnTheTable.isEmpty());

        Assert.assertEquals("expected list contains a card with provided id", String.valueOf(testCardId), cardsOnTheTable.get(0).getId());
    }
}
