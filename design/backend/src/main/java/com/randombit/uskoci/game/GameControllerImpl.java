package com.randombit.uskoci.game;

import com.randombit.uskoci.card.dao.CardDAO;
import com.randombit.uskoci.card.dao.CardDAOSimple;
import com.randombit.uskoci.card.model.Card;

import java.util.*;

public class GameControllerImpl implements GameController {


    private static final int MAX_NUMBER_OF_PLAYERS = 6;
    private static final int MIN_NUMBER_OF_PLAYERS = 3;
    private static final int BEGINNING_NUMBER_OF_CARDS = 4;
    private static final int NO_OF_CARDS_DRAWN_A_TURN = 1;
    private static final int NO_OF_PHASES = 6;
    private static final int MAX_NUMBER_OF_CARDS_IN_HAND = 5;

    private CardDAO cardDAO = new CardDAOSimple();

    private List<Card> cardDeck = new ArrayList<Card>(cardDAO.getAllCards());

    // Map of cards in all players hands
    private Map<String, List<Card>> playerCardMap = new HashMap<String, List<Card>>();

    private int currentPlayerId;

    private boolean beginningCardDrawn = false;

    private boolean gameStarted = false;

    void setBeginningCardDrawn(boolean beginningCardDrawn) {
        this.beginningCardDrawn = beginningCardDrawn;
    }

    public void setCardDAO(CardDAO cardDAO) {
        this.cardDAO = cardDAO;
        List<Card> cardDeck = new ArrayList<Card>(cardDAO.getAllCards());
    }

    private List<Card> cardsOnTheTable = Collections.<Card>emptyList();

    public int getNumberOfPlayersJoined() {
        return numberOfPlayersJoined;
    }

    private int numberOfPlayersJoined;

    public List<Card> getCardDeck() {
        return cardDeck;
    }

    public void setCardDeck(List<Card> cardDeck) {
        this.cardDeck = cardDeck;
    }

    public int getCurrentPhase() {
        return currentPhase;
    }

    public int setNextPhase() {
        if (!beginningCardDrawn && currentPhase == 1) {
            return currentPhase;
        } else if (currentPhase == NO_OF_PHASES && !isNoOfCardsInHandValid()) {
            return currentPhase;
        } else {
            currentPhase += 1;
            if (currentPhase > NO_OF_PHASES) {
                currentPhase = 1;
                this.currentPlayerId = getNextPlayerId();
            }
            return currentPhase;
        }

    }


    /**
     * Is number of cards in hand at the end of turn valid
     *
     * @return validity
     */
    private boolean isNoOfCardsInHandValid() {
        return playerCardMap.get(String.valueOf(currentPlayerId)).size() < MAX_NUMBER_OF_CARDS_IN_HAND + 1;
    }

    @Override
    public int setPhase(int phase) {
        return this.currentPhase = phase;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean getBeginningCardDrawn() {
        return beginningCardDrawn;  //To change body of implemented methods use File | Settings | File Templates.
    }


    private int currentPhase;


    // TODO implement methods
    public int getCurrentPlayerId() {
        return currentPlayerId;
    }

    public int getNextPlayerId() {
//        int nextPlayer = (this.currentPlayerId == numberOfPlayersJoined) ? 1 : currentPlayerId++;
        int nextPlayer;
        if (currentPlayerId == numberOfPlayersJoined) {
            nextPlayer = 1;
        } else {
            nextPlayer = currentPlayerId + 1;
        }
        return nextPlayer;
    }

    public List<Card> getCardsInTheDeck() {
        return cardDeck;
    }

    private void dealCards(int numberOfPlayers) {
        Collections.shuffle(cardDeck);
        for (int i = 1; i < numberOfPlayers + 1; i++) {
            List<Card> cardsDealtToPlayer = cardDeck.subList(0, BEGINNING_NUMBER_OF_CARDS);
            playerCardMap.put(String.valueOf(i), new ArrayList<Card>(cardsDealtToPlayer));
            cardDeck.removeAll(playerCardMap.get(String.valueOf(i)));
        }
    }

    // Reset field values
    public String resetGame() {
        this.cardDeck = new ArrayList<Card>(cardDAO.getAllCards());
        this.playerCardMap = new HashMap<String, List<Card>>();
        this.currentPhase = 1;
        this.beginningCardDrawn = false;
        this.gameStarted = false;

        Random randomGenerator = new Random();
        this.currentPlayerId = randomGenerator.nextInt(numberOfPlayersJoined - 1) + 1;

        return "Game reset";

    }

    public boolean startGame(int numberOfPlayersJoined) {
        if (numberOfPlayersJoined <= MAX_NUMBER_OF_PLAYERS || numberOfPlayersJoined >= MIN_NUMBER_OF_PLAYERS) {
            this.numberOfPlayersJoined = numberOfPlayersJoined;
            resetGame();
            dealCards(this.numberOfPlayersJoined);
            this.gameStarted = true;
        }
        return gameStarted;
    }

    public List<Card> getPlayerCards(int playerId) {
        return playerCardMap.get(String.valueOf(playerId));
    }

    @Override
    public Card drawCard(int playerId) {
        Card cardDrawn = cardDeck.remove(0);
        playerCardMap.get(String.valueOf(playerId)).add(cardDrawn);

        if (!beginningCardDrawn)
            beginningCardDrawn = true;

        return cardDrawn;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }
}
