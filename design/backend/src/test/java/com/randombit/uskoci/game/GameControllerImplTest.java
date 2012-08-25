package com.randombit.uskoci.game;

import com.randombit.uskoci.card.dao.CardDAO;
import com.randombit.uskoci.card.model.Card;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.easymock.EasyMock;

import java.util.ArrayList;
import java.util.List;

public class GameControllerImplTest {

    private static final int STARTING_NUMBER_OF_CARDS = 4;
    // TODO create basic tests
    GameController gameController = new GameControllerImpl();
    CardDAO cardDAOMock;
    private static final int INITIAL_NUMBER_OF_CARDS_IN_THE_DECK = 60;

    int testNumberOfPlayers = 4;
    private static final int NO_OF_PHASES = 6;

    @Before
    public void setUp() throws Exception {
        gameController.startGame(testNumberOfPlayers);
        cardDAOMock = EasyMock.createMock(CardDAO.class);
    }

    @Test
    public void testGetCardsInTheDeck() throws Exception {

//        gameController.setCardDAO(cardDAOMock);
        Assert.assertTrue("Deck is not empty", !gameController.getCardsInTheDeck().isEmpty());
    }

//    @Test
//    public void testPutCardOnTheTable() throws Exception {
//
//        int testCardId = 2;
//        gameController.putCardOnTheTable(testCardId);
//
//        List<Card> cardsOnTheTable = gameController.getCardsInTheDeck();
//        Assert.assertFalse("Card size", cardsOnTheTable.isEmpty());
//
//        Assert.assertEquals("expected list contains a card with provided id", String.valueOf(testCardId), cardsOnTheTable.get(0).getId());
//    }

//    @Test
//    public void testGetCards() throws Exception {
//        gameController.resetGame();
//
//        int testPlayerId = 1;
//        List<Card> actualCards = gameController.getCards(testPlayerId);
//
//        Assert.assertEquals("Player has default amount of playing cards", STARTING_NUMBER_OF_CARDS, actualCards.size());
//    }


    //    Na početku igre, promiješa se špil karata (ili špilovi karata)
    //    Svakom igraču se podijeli n karata (za Uskoke (USK) n=4)
    @Test
    public void testCardShuffle() throws Exception {
        gameController.startGame(testNumberOfPlayers);

        List<Card> cardsInTheDeck = gameController.getCardsInTheDeck();
        int expectedNumberOfCards = INITIAL_NUMBER_OF_CARDS_IN_THE_DECK - (testNumberOfPlayers * STARTING_NUMBER_OF_CARDS);
        Assert.assertEquals("Remaining number of cards is smaller after dealing", expectedNumberOfCards, cardsInTheDeck.size());

        List<Card> allPlayersHands = new ArrayList<Card>();

        for (int i = 1; i < testNumberOfPlayers + 1; i++) {
            allPlayersHands.addAll(gameController.getPlayerCards(i));
        }
        Assert.assertFalse("Cards in players' hands are not the same as ones in the deck",
                cardsInTheDeck.containsAll(allPlayersHands));
        Assert.assertFalse("Deck does not contain a card in players hand",
                cardsInTheDeck.contains(allPlayersHands.get(0)));

        gameController.startGame(testNumberOfPlayers);
        Assert.assertFalse("After restart of the game, different cards are remaining in the deck", cardsInTheDeck.containsAll(gameController.getCardsInTheDeck()));
        Assert.assertFalse("After restart of the game, players have different cards in their hands",
                allPlayersHands.containsAll(gameController.getPlayerCards(1))
                        && allPlayersHands.containsAll(gameController.getPlayerCards(2))
                        && allPlayersHands.containsAll(gameController.getPlayerCards(3))
                        && allPlayersHands.containsAll(gameController.getPlayerCards(4)));
    }

    //    Početni igrač se odredi nasumično
    @Test
    public void testFirstPlayer() throws Exception {
        int firstPlayer = gameController.getCurrentPlayerId();

        Assert.assertTrue("First player is set", firstPlayer > 0);

        gameController.startGame(testNumberOfPlayers);
        Assert.assertTrue("First player is re-set", firstPlayer > 0);
    }



    // Opcionalno: Na početku poteza vuče se n karata (USK: da, n=1)
    @Test
    public void testDrawCard() throws Exception {
        int testPlayerId = 1;
        Card cardDrawn = gameController.drawCard(testPlayerId);

        Assert.assertNotNull("Card has been drawn",
                cardDrawn);
        Assert.assertFalse("Deck does not contain a card after it has been drawn",
                gameController.getCardsInTheDeck().contains(cardDrawn));
        Assert.assertTrue("Players hand contains a card after it has been drawn",
                gameController.getPlayerCards(testPlayerId).contains(cardDrawn));
    }

//    Igrač klikom na gumb prelazi iz faze u fazu, prelaskom iz završne faze,
//    započinje potez sljedećeg igrača (USK: 1 faza, završetkom faze, pokreće se pravilo 6.)
    @Test
    public void testPhase() throws Exception {
        int currentPhase = gameController.getCurrentPhase();
        Assert.assertEquals("Game begins with phase one", 1, currentPhase);

        Assert.assertEquals("Phase is increased when set to next phase", currentPhase + 1, gameController.setNextPhase());

        gameController.startGame(testNumberOfPlayers);

        int currentPlayerId = gameController.getCurrentPlayerId();

        for (int i = 0; i < NO_OF_PHASES; i++) {
              gameController.setNextPhase();
        }

        Assert.assertTrue("After " + String.valueOf(NO_OF_PHASES) + " phases by player NO: " +currentPlayerId+
                " next player is on the move", currentPlayerId != gameController.getCurrentPlayerId() );

    }
}
