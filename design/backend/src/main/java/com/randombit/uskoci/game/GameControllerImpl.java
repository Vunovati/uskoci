package com.randombit.uskoci.game;

import com.randombit.uskoci.card.dao.CardDAO;
import com.randombit.uskoci.card.model.Card;

import java.util.*;

public class GameControllerImpl implements GameController {
    private static final int MAX_NUMBER_OF_PLAYERS = 6;
    private static final int MIN_NUMBER_OF_PLAYERS = 3;
    private static final int BEGINNING_NUMBER_OF_CARDS = 4;
    private static final int MAX_NUMBER_OF_CARDS_IN_HAND = 5;

    private CardDAO cardDAO;
    private List<Card> cardDeck;
    private List<Card> discardedCards;
    private Map<String, List<Card>> playerCardMap;
    private int currentPlayerId;
    private boolean beginningCardDrawn;
    private boolean gameStarted;
    private int numberOfPlayersJoined;
    private boolean resourceCardPlayed;
    private Map<String, List<Card>> playersResources;

    public GameControllerImpl(CardDAO cardDAO) {
        this.cardDAO = cardDAO;
    }

    @Override
    public void setCardDAO(CardDAO cardDAO) {
        this.cardDAO = cardDAO;
    }

    @Override
    public int getNumberOfPlayersJoined() {
        return numberOfPlayersJoined;
    }

    @Override
    public List<Card> getDiscardPile() {
        if (discardedCards == null)
            return Collections.<Card>emptyList();
        return discardedCards;
    }

    @Override
    public List<Card> getResources(int playerId) {
        List<Card> playerResources = playersResources.get(String.valueOf(playerId));

        if (playerResources == null)
            playerResources = Collections.<Card>emptyList();
        return playerResources;
    }

    @Override
    public int getPlayersPoints(int playerId) {
        int playersPoints = 0;
        List<Card> playersResources = getResources(playerId);
        for (Card card:playersResources) {
            if ("resource".equals(card.getType())) {
                playersPoints += Integer.valueOf(card.getValue());
            }
        }
        return playersPoints;
    }

    @Override
    public void playCard(int playerId, int cardId) throws ActionNotAllowedException {
        Card cardPlayed = cardDAO.getCard(cardId);
        if (playerIsNotOnTheMove(playerId) && !cardIsEvent(cardPlayed)) {
            throw new ActionNotAllowedException();
        }

        putCardInPlayersResources(cardPlayed, playerId);
        removeCardFromPlayersHand(cardPlayed, playerId);
    }

    private void removeCardFromPlayersHand(Card cardPlayed, int playerId) {
        playerCardMap.get(String.valueOf(playerId)).remove(cardPlayed);
    }

    @Override
    public boolean isResourceCardPlayed() {
        return resourceCardPlayed;
    }

    private boolean cardIsEvent(Card cardPlayed) {
        return "event".equals(cardPlayed.getType());
    }

    private boolean playerIsNotOnTheMove(int playerId) {
        return playerId != currentPlayerId;
    }

    private void putCardInPlayersResources(Card card, int playerId) {
        List<Card> playersCards = getResources(playerId);
        playersCards.add(card);
    }

    private boolean isNoOfCardsInHandValid() {
        return playerCardMap.get(String.valueOf(currentPlayerId)).size() < MAX_NUMBER_OF_CARDS_IN_HAND + 1;
    }

    @Override
    public boolean getBeginningCardDrawn() {
        return beginningCardDrawn;
    }

    @Override
    public int getCurrentPlayerId() {
        return currentPlayerId;
    }

    @Override
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

    @Override
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

    @Override
    public String resetGame() {
        this.cardDeck = new ArrayList<Card>(cardDAO.getAllCards());
        this.playerCardMap = new HashMap<String, List<Card>>();
        this.resourceCardPlayed = false;
        this.beginningCardDrawn = false;
        this.gameStarted = false;
        this.playersResources = generateEmptyPlayersResources();
        this.discardedCards = new ArrayList<Card>();
        Random randomGenerator = new Random();
        this.currentPlayerId = randomGenerator.nextInt(numberOfPlayersJoined - 1) + 1;
        return "Game reset";
    }

    private HashMap<String, List<Card>> generateEmptyPlayersResources() {
        HashMap<String, List<Card>> playersResourcesHashMap = new HashMap<String, List<Card>>();
        for (int playerId = 1; playerId <= numberOfPlayersJoined; playerId++) {
            playersResourcesHashMap.put(String.valueOf(playerId), new ArrayList<Card>());
        }
        return playersResourcesHashMap;
    }

    @Override
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
        
        if(cardDeck.isEmpty()) {
            Collections.shuffle(discardedCards);
            cardDeck.addAll(discardedCards);
            discardedCards.clear();
        }

        return cardDrawn;
    }

    @Override
    public void setNextPlayersTurn(int playerId) throws ActionNotAllowedException {
        if (beginningCardDrawn && playerOnTheMove(playerId) && isNoOfCardsInHandValid()) {
            beginTurn();
        } else {
            throw new ActionNotAllowedException();
        }
    }

    private void beginTurn() {
        currentPlayerId = getNextPlayerId();
        beginningCardDrawn = false;
    }

    private boolean playerOnTheMove(int playerId) {
        return playerId == currentPlayerId;
    }

    @Override
    public boolean isGameStarted() {
        return gameStarted;
    }
    
    @Override
    public void discardCardfromHand(Card card, int playerId) {  
        List<Card> playersCards = getPlayerCards(playerId);
        Iterator<Card> itr = playersCards.iterator();
        
        if(!playersCards.isEmpty()) {
            for(int i=0; i<playersCards.size(); i++){ 
                if(itr.next() == card){ 
                    playersCards.remove(card);
                    i = playersCards.size();
                }
            }               
            discardedCards.add(card);
        }
    }
    
    @Override
    public void discardCardfromResourcePile(Card card, int playerId) {   
        List<Card> playersCards = getResources(playerId);
        Iterator<Card> itr = playersCards.iterator();
        
        if(!playersCards.isEmpty()) {
            for(int i=0; i<playersCards.size(); i++){ 
                if(itr.next() == card){ 
                    playersCards.remove(card);
                    i = playersResources.size();
                }    
            }
            discardedCards.add(card);
        }    
    }
}