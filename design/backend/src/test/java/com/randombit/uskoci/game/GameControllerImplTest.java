package com.randombit.uskoci.game;

import com.randombit.uskoci.card.dao.CardDAO;
import com.randombit.uskoci.card.model.Card;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
//import org.easymock.EasyMock;

import java.util.ArrayList;
import java.util.List;

public class GameControllerImplTest {

    private static final int STARTING_NUMBER_OF_CARDS = 4;
    private static final int MAX_NUMBER_OF_CARDS_IN_HAND = 5;
    GameController gameController;
    CardDAO cardDAOMock;
    private static final int INITIAL_NUMBER_OF_CARDS_IN_THE_DECK = 60;

    int testNumberOfPlayers = 4;
    private static final int NO_OF_PHASES = 6;
    private static final int DRAW_CARD_PHASE_NUMBER = 1;

    @Before
    public void setUp() throws Exception {
        gameController = new GameControllerImpl();
        gameController.startGame(testNumberOfPlayers);
    }

    @Test
    public void testGetCardsInTheDeck() throws Exception {
        Assert.assertTrue("Deck is not empty", !gameController.getCardsInTheDeck().isEmpty());
    }

    //    Na početku igre, promiješa se špil karata (ili špilovi karata)
    //    Svakom igraču se podijeli n karata (za Uskoke (USK) n=4)
    @Test
    public void testCardShuffle() throws Exception {

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



        Assert.assertEquals("Each player is dealt an equal amount of cards",
                (gameController.getPlayerCards(1).size() == gameController.getPlayerCards(2).size()),
                (gameController.getPlayerCards(3).size() == gameController.getPlayerCards(4).size())
        )
        ;
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

    @Test
    public void testBeginTurn() throws Exception {
        int currentPhase = gameController.getCurrentPhase();


        Assert.assertEquals("Phase " + (DRAW_CARD_PHASE_NUMBER + 1) + " Does not begin if card is not drawn",
                gameController.setNextPhase(), DRAW_CARD_PHASE_NUMBER);

        gameController.drawCard(gameController.getCurrentPlayerId());
        Assert.assertEquals("Phase " + (DRAW_CARD_PHASE_NUMBER + 1) + " begins if card is drawn",
                gameController.setNextPhase(), DRAW_CARD_PHASE_NUMBER + 1);


        gameController.startGame(testNumberOfPlayers);
        Assert.assertFalse("At the beginning of the game beginning card has not yet been drawn", gameController.getBeginningCardDrawn());
//        Assert.assertTrue("At the phase "+ DRAW_CARD_PHASE_NUMBER + " draws a card", );
    }

    //    Igrač klikom na gumb prelazi iz faze u fazu, prelaskom iz završne faze,
//    započinje potez sljedećeg igrača (USK: 1 faza, završetkom faze, pokreće se pravilo 6.)
    @Test
    public void testPhase() throws Exception {
        int currentPhase = gameController.getCurrentPhase();
        Assert.assertEquals("Game begins with phase one", 1, currentPhase);

        gameController.setPhase(1);
        Assert.assertEquals("Phase 1 is not increased if beginning card has not been drawn", 1, gameController.setNextPhase());

        gameController.setPhase(currentPhase + 1);
        Assert.assertEquals("Phase is increased when set to next phase", currentPhase + 2, gameController.setNextPhase());

        gameController.startGame(testNumberOfPlayers);

        int currentPlayerId = gameController.getCurrentPlayerId();

        gameController.setPhase(NO_OF_PHASES);
        gameController.setNextPhase();

        Assert.assertNotSame("After " + String.valueOf(NO_OF_PHASES) + " phases by player NO: " + currentPlayerId +
                " next player is on the move", currentPlayerId, gameController.getCurrentPlayerId());

    }

    @Test
    public void testNextPlayer() throws Exception {

        int currentPlayer = gameController.getCurrentPlayerId();
        int expectedPlayer;

        if (currentPlayer == 4 )
        {
            expectedPlayer = 1;
        } else {
            expectedPlayer = currentPlayer + 1;
        }

        Assert.assertEquals("When previous player was " + currentPlayer + " next one is " + expectedPlayer, expectedPlayer, gameController.getNextPlayerId());
    }

    //    Opcionalno: Igrač na kraju poteza smije imati maksimalno n karata u ruci (USK: da, 5); modifikacija ovog pravila
//    je „u svakom trenutku“ gdje će se svaki trenutak provjeravati na kraju svake faze/stepa igre (USK: ne)

    @Test
    public void testCheckCardsEndOfTurn() throws Exception {
        int testPlayerId = gameController.getCurrentPlayerId();

        int numberOfCardsInPlayersHands = gameController.getPlayerCards(testPlayerId).size();

        // Draw more cards than allowed on the end of turn
        while (numberOfCardsInPlayersHands <= MAX_NUMBER_OF_CARDS_IN_HAND + 1) {
            gameController.drawCard(testPlayerId);
            numberOfCardsInPlayersHands = gameController.getPlayerCards(testPlayerId).size();
        }

        //Cycle to the last phase
        gameController.setPhase(NO_OF_PHASES);

        Assert.assertEquals("After " + (NO_OF_PHASES) + " phases last phase is set", NO_OF_PHASES, gameController.getCurrentPhase());

        Assert.assertEquals("At the beginning of phase " + gameController.getCurrentPhase() + " player has " +
                gameController.getPlayerCards(testPlayerId).size()
                + " cards in hand", MAX_NUMBER_OF_CARDS_IN_HAND + 2, numberOfCardsInPlayersHands);

        //End last phase
        gameController.setNextPhase();

        Assert.assertEquals("Player "+ testPlayerId +"  with "+ gameController.getPlayerCards(testPlayerId).size() +" cards in his hand cannot proceed from phase " + NO_OF_PHASES + " until players number of cards in hand is less or equal to " +
                MAX_NUMBER_OF_CARDS_IN_HAND, testPlayerId, gameController.getCurrentPlayerId());

        Assert.assertEquals("Phase cannot proceed from phase " + NO_OF_PHASES + " until players number of cards in hand is less or equal to " +
                MAX_NUMBER_OF_CARDS_IN_HAND, NO_OF_PHASES, gameController.getCurrentPhase());



        int numberOfPlayersWithTooManyCards = 0;
        for (int i = 1; i < testNumberOfPlayers + 1; i++) {
            if (gameController.getPlayerCards(i).size() > MAX_NUMBER_OF_CARDS_IN_HAND) {
                numberOfPlayersWithTooManyCards++;
            }
        }

        Assert.assertEquals("At the beginning of next players turn no other player has no more than" + MAX_NUMBER_OF_CARDS_IN_HAND +
                "in his hand", 1, numberOfPlayersWithTooManyCards);

    }
}
