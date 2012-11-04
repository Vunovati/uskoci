package com.randombit.uskoci.game.control;

import com.randombit.uskoci.card.dao.CardDAO;
import com.randombit.uskoci.card.model.Card;
import com.randombit.uskoci.game.ActionNotAllowedException;
import com.randombit.uskoci.game.control.eventHandling.Action;
import com.randombit.uskoci.game.control.eventHandling.EventHandler;
import com.randombit.uskoci.game.control.eventHandling.Response;

import java.util.*;

public class GameControllerImpl implements GameController {

    private CardDAO cardDAO;

    private GameStatus gameStatus = new GameStatus();
    private EventHandler eventHandler = new EventHandler();

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public GameControllerImpl(CardDAO cardDAO) {
        this.cardDAO = cardDAO;
    }

    @Override
    public void setCardDAO(CardDAO cardDAO) {
        this.cardDAO = cardDAO;
    }

    @Override
    public void setCardDeck(List<Card> cardDeck) {
        this.gameStatus.setCardDeck(cardDeck);
    }

    @Override
    public int getNumberOfPlayersJoined() {
        return gameStatus.getNumberOfPlayersJoined();
    }

    @Override
    public List<Card> getDiscardPile() {
        if (gameStatus.getDiscardedCards() == null)
            return Collections.<Card>emptyList();
        return gameStatus.getDiscardedCards();
    }

    @Override
    public List<Card> getResources(int playerId) {
        List<Card> playerResources = gameStatus.getPlayersResources().get(String.valueOf(playerId));

        if (playerResources == null)
            playerResources = Collections.<Card>emptyList();
        return playerResources;
    }

    @Override
    public List<Card> getCardStack() {
    	return gameStatus.getCardStack();
    }

    @Override
    public int getPlayersPoints(int playerId) {
        int playersPoints = 0;
        List<Card> playersResources = getResources(playerId);
        for (Card card:playersResources) {
            if (GameConstants.RESOURCE.equals(card.getType())) {
                playersPoints += Integer.valueOf(card.getValue());
            }
        }
        return playersPoints;
    }

    @Override
    public void playCard(int playerId, int cardId) throws ActionNotAllowedException {
        
    	Card cardPlayed = cardDAO.getCard(cardId);

        if(playerPointsTooHigh(playerId, cardPlayed)){
            throw new ActionNotAllowedException();
        }

        if(cardIsEvent(cardPlayed))  {
            playEventCard(cardPlayed, playerId);
        }
        else  {
        	if(cardIsResource(cardPlayed)){
        		playResourceCard(cardPlayed, playerId);
        	}
        	else{
        		if(cardIsMultiplier(cardPlayed)){
        			putCardInPlayersResources(cardPlayed, playerId);
        		}
        		else {
        			throw new ActionNotAllowedException();
        		}
        	}    
        }
        removeCardFromPlayersHand(cardPlayed, playerId);
    }
    
    private void playEventCard(Card cardPlayed, int playerId) throws ActionNotAllowedException {
        List<Card> currentCardStack = getCardStack();
        String cardSummary = cardPlayed.getSummary();


        if(!currentCardStack.isEmpty()) {
        	if(cardCanBePlayedInResponse(cardSummary)) {
        		//cardPlayed.setCardOwner(String.valueOf(playerId));
        		putCardOnStack(cardPlayed);
        	}
        	else {
        		throw new ActionNotAllowedException();
        	}	
       }
       else {
    	   if(cardCanBePlayedInResponse(cardSummary)) {
    		   throw new ActionNotAllowedException();
       		}
           //cardPlayed.setCardOwner(String.valueOf(playerId));
           putCardOnStack(cardPlayed); 
       }
    }
    @Override
    public List<Action> resolveEventOnStack(int eventPlayerId){
    	List<Action> listOfActions = new ArrayList<Action>();
    	
    	Card event = removeCardFromStack();
    	listOfActions = eventHandler.resolveEvent(event, eventPlayerId);
    	
    	return listOfActions;
    }

    private boolean cardCanBePlayedInResponse(String cardSummary) {
        return GameConstants.WILL.equals(cardSummary) || GameConstants.FORT.equals(cardSummary);
    }

    private void playResourceCard(Card cardPlayed, int playerId) throws ActionNotAllowedException {
    	List<Card> currentStack = getCardStack();

    	if(playerIsNotOnTheMove(playerId)){
    		throw new ActionNotAllowedException();
    	}
        if(!currentStack.isEmpty()) {
        	throw new ActionNotAllowedException();
        }
        if(gameStatus.isResourceCardPlayed()) {
        	throw new ActionNotAllowedException();
        }
        gameStatus.setResourceCardPlayed(true);
        putCardInPlayersResources(cardPlayed, playerId);
    }
    
    private boolean playerPointsTooHigh(int playerId, Card cardPlayed){
        int playersPoints = getPlayersPoints(playerId);
        
        if (cardIsResource(cardPlayed)) {
            if((playersPoints + Integer.valueOf(cardPlayed.getValue())) > GameConstants.MAX_NUMBER_OF_PLAYER_POINTS){
                return true;
            }    
        }

        if(cardIsEvent(cardPlayed)) {
            //TODO
            return false;
        }


        return false;
    }  

    private void removeCardFromPlayersHand(Card cardPlayed, int playerId) {
        gameStatus.getPlayerCardMap().get(String.valueOf(playerId)).remove(cardPlayed);
    }

    @Override
    public boolean isResourceCardPlayed() {
        return gameStatus.isResourceCardPlayed();
    }

    private boolean cardIsEvent(Card cardPlayed) {
        return GameConstants.EVENT.equals(cardPlayed.getType());
    }
    
    private boolean cardIsResource(Card cardPlayed){

        return GameConstants.RESOURCE.equals(cardPlayed.getType());
    }

    private boolean cardIsMultiplier(Card cardPlayed){

        return GameConstants.MULTIPLIER.equals(cardPlayed.getType());
    }    

    private boolean playerIsNotOnTheMove(int playerId) {
        return playerId != gameStatus.getCurrentPlayerId();
    }

    private void putCardInPlayersResources(Card card, int playerId) {
        List<Card> playersCards = getResources(playerId);
        playersCards.add(card);
    }

    private void putCardOnStack(Card card){
        LinkedList<Card> currentCardStack =  (LinkedList<Card>) getCardStack();
        currentCardStack.add(card);
    }    

    private Card removeCardFromStack(){
        List<Card> currentCardStack = getCardStack();
        return currentCardStack.remove(0);
    }   

    private boolean isNoOfCardsInHandValid() {
        return gameStatus.getPlayerCardMap().get(String.valueOf(gameStatus.getCurrentPlayerId())).size() < GameConstants.MAX_NUMBER_OF_CARDS_IN_HAND + 1;
    }

    @Override
    public boolean getBeginningCardDrawn() {
        return gameStatus.isBeginningCardDrawn();
    }

    @Override
    public int getCurrentPlayerId() {
        return gameStatus.getCurrentPlayerId();
    }

    @Override
    public int getNextPlayerId() {
//        int nextPlayer = (this.currentPlayerId == numberOfPlayersJoined) ? 1 : currentPlayerId++;
        int nextPlayer;
        if (gameStatus.getCurrentPlayerId() == gameStatus.getNumberOfPlayersJoined()) {
            nextPlayer = 1;
        } else {
            nextPlayer = gameStatus.getCurrentPlayerId() + 1;
        }
        return nextPlayer;
    }

    @Override
    public List<Card> getCardsInTheDeck() {
        return gameStatus.getCardDeck();
    }

    private void dealCards(int numberOfPlayers) {
        Collections.shuffle(gameStatus.getCardDeck());
        giveCardsToPlayers(numberOfPlayers);
    }

    private void giveCardsToPlayers(int numberOfPlayers) {
        for (int i = 1; i < numberOfPlayers + 1; i++) {
            List<Card> cardsDealtToPlayer = gameStatus.getCardDeck().subList(0, GameConstants.BEGINNING_NUMBER_OF_CARDS);
            gameStatus.getPlayerCardMap().put(String.valueOf(i), new ArrayList<Card>(cardsDealtToPlayer));
            gameStatus.getCardDeck().removeAll(gameStatus.getPlayerCardMap().get(String.valueOf(i)));
        }
    }

    @Override
    public String resetGame() {
        this.gameStatus.setCardDeck(new ArrayList<Card>(cardDAO.getAllCards()));
        this.gameStatus.setPlayerCardMap(new HashMap<String, List<Card>>());
        this.gameStatus.setResourceCardPlayed(false);
        this.gameStatus.setBeginningCardDrawn(false);
        this.gameStatus.setGameStarted(false);
        this.gameStatus.setPlayersResources(generateEmptyPlayersResources());
        this.gameStatus.setDiscardedCards(new ArrayList<Card>());
        this.gameStatus.setCardStack(new LinkedList<Card>());
        Random randomGenerator = new Random();
        this.gameStatus.setCurrentPlayerId(randomGenerator.nextInt(gameStatus.getNumberOfPlayersJoined() - 1) + 1);
        return "Game reset";
    }

    private HashMap<String, List<Card>> generateEmptyPlayersResources() {
        HashMap<String, List<Card>> playersResourcesHashMap = new HashMap<String, List<Card>>();
        for (int playerId = 1; playerId <= gameStatus.getNumberOfPlayersJoined(); playerId++) {
            playersResourcesHashMap.put(String.valueOf(playerId), new ArrayList<Card>());
        }
        return playersResourcesHashMap;
    }


    @Override
    public boolean startGame(int numberOfPlayersJoined) {
        if (numberOfPlayersJoined <= GameConstants.MAX_NUMBER_OF_PLAYERS || numberOfPlayersJoined >= GameConstants.MIN_NUMBER_OF_PLAYERS) {
            this.gameStatus.setNumberOfPlayersJoined(numberOfPlayersJoined);
            resetGame();
            dealCards(this.gameStatus.getNumberOfPlayersJoined());
            this.gameStatus.setGameStarted(true);
        }
        return gameStatus.isGameStarted();
    }

    public List<Card> getPlayerCards(int playerId) {
        return gameStatus.getPlayerCardMap().get(String.valueOf(playerId));
    }

    @Override
    public Card drawCard(int playerId) throws ActionNotAllowedException {
        if (gameStatus.isBeginningCardDrawn())
            throw new ActionNotAllowedException();

        Card cardDrawn = gameStatus.getCardDeck().remove(0);
        gameStatus.getPlayerCardMap().get(String.valueOf(playerId)).add(cardDrawn);

        if (playerIsNotOnTheMove(playerId))
            throw new ActionNotAllowedException();

        if (!gameStatus.isBeginningCardDrawn())
            gameStatus.setBeginningCardDrawn(true);
        
        if(gameStatus.getCardDeck().isEmpty()) {
            Collections.shuffle(gameStatus.getDiscardedCards());
            gameStatus.getCardDeck().addAll(gameStatus.getDiscardedCards());
            gameStatus.getDiscardedCards().clear();
        }

        return cardDrawn;
    }

    @Override
    public void setNextPlayersTurn(int playerId) throws ActionNotAllowedException {
        if (gameStatus.isBeginningCardDrawn() && playerOnTheMove(playerId) && isNoOfCardsInHandValid()) {
            beginTurn();
        } else {
            throw new ActionNotAllowedException();
        }
    }

    private void beginTurn() {
        gameStatus.setCurrentPlayerId(getNextPlayerId());
        gameStatus.setBeginningCardDrawn(false);
        gameStatus.setResourceCardPlayed(false);
        gameStatus.getCardStack().clear();
    }

    private boolean playerOnTheMove(int playerId) {
        return playerId == gameStatus.getCurrentPlayerId();
    }

    @Override
    public boolean isGameStarted() {
        return gameStatus.isGameStarted();
    }
    
    @Override
    public void discardCardFromPlayersHand(int cardId, int playerId) {
        Card card = cardDAO.getCard(cardId);
        List<Card> playersCards = getPlayerCards(playerId);
        removeCardFromZone(card, playersCards);
    }

    private void removeCardFromZone(Card card, List<Card> cardList) {
        if(!cardList.isEmpty()) {
            cardList.remove(card);
            gameStatus.getDiscardedCards().add(card);
        }
    }

    @Override
    public void discardCardFromResourcePile(int cardId, int playerId) {
        Card card = cardDAO.getCard(cardId);
        List<Card> playersResources = getResources(playerId);
        removeCardFromZone(card, playersResources);
    }

    @Override
    public void removeMultiplierFromResourcePile (int playerId, int cardId) throws ActionNotAllowedException {
        Card card = cardDAO.getCard(cardId);

        if(cardIsMultiplier(card)){
            discardCardFromResourcePile(cardId,playerId);
        } else {
            throw new ActionNotAllowedException();
        }  
    }

    @Override
    public Card flipCardFaceUp() {
        Card flippedCard = gameStatus.getCardDeck().remove(0);
        gameStatus.getDiscardedCards().add(flippedCard);
        return flippedCard;
    } 
    
    @Override
    public void moveCard(Action action){
    	// TODO : Implement interface towards frontend
    }
    @Override
    public void moveCards(Action action){
    	// TODO : Implement interface towards frontend
    }
    @Override
    public void changeValue(Card card){
    	// TODO : Implement old Sea Dog interface towards frontend
    }
  
    
    private <T> List<Card> peakCards(List<Card> area, int start, int end, T delimiter) {
    	List<Card> cards = new ArrayList<Card>();
    	
    	if(delimiter == "all"){
    		start = 0;
    		end = area.size();
    	}

    	for(int i=start; i< end; i++) {
    			cards.add(area.get(i));	
    	}

    	return cards;
    }
    
    private void moveCards(List<Card> fromArea, List<Card> toArea, int start, int end, String delimiter) {
    	
    	if(delimiter == "all"){
    		start = 0;
    		end = fromArea.size();
    	}

    	for(int i=start; i< end; i++) {
    		Card card = fromArea.get(i);	
    		fromArea.remove(card);
    		toArea.add(card);
    	}
    }
    
    public void moveCard(List<Card> fromArea, List<Card> toArea, Card card) {
    	fromArea.remove(card);
    	toArea.remove(card);
    }

}